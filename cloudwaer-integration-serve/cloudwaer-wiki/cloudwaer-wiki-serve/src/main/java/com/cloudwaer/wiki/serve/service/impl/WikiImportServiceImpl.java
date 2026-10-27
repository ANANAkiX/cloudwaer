package com.cloudwaer.wiki.serve.service.impl;

import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.wiki.api.dto.WikiImportResultDTO;
import com.cloudwaer.wiki.serve.entity.WikiDoc;
import com.cloudwaer.wiki.serve.mapper.WikiDocMapper;
import com.cloudwaer.wiki.serve.mapper.WikiSpaceMapper;
import com.cloudwaer.wiki.serve.service.WikiImportService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库导入：zip / 文件夹多文件 → 文档树。
 *
 * <p>
 * 路径中的目录段会同步建成「目录文档」（空正文）；文件仅收 md/markdown/txt。zip 内 2MB 以上单文件跳过。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WikiImportServiceImpl implements WikiImportService {

	private static final Set<String> ALLOWED_EXT = Set.of("md", "markdown", "txt");

	private static final long MAX_ENTRY_BYTES = 2L * 1024 * 1024;

	private final WikiDocMapper docMapper;

	private final WikiSpaceMapper spaceMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WikiImportResultDTO importArchive(Long spaceId, Long parentId, MultipartFile file) {
		requireSpace(spaceId);
		if (file == null || file.isEmpty()) {
			throw new BusinessException("请选择要导入的文件");
		}
		String name = file.getOriginalFilename() == null ? "import" : file.getOriginalFilename();
		String lower = name.toLowerCase(Locale.ROOT);
		WikiImportResultDTO result = new WikiImportResultDTO();
		Map<String, Long> dirCache = new HashMap<>();
		Long root = parentId == null ? 0L : parentId;
		try {
			byte[] bytes = file.getBytes();
			if (lower.endsWith(".zip")) {
				importZip(spaceId, root, bytes, dirCache, result);
			}
			else if (isAllowed(name)) {
				String title = stripExt(baseName(normalizePath(name)));
				createDoc(spaceId, root, title, decodeText(bytes));
				result.setDocCount(result.getDocCount() + 1);
			}
			else {
				throw new BusinessException("仅支持 zip 或 md/markdown/txt 文件");
			}
		}
		catch (IOException ex) {
			log.warn("wiki import io error", ex);
			throw new BusinessException("读取导入文件失败");
		}
		if (result.getDocCount() == 0 && result.getDirCount() == 0) {
			throw new BusinessException("包内没有可导入的 md/markdown/txt 文档");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public WikiImportResultDTO importFiles(Long spaceId, Long parentId, List<String> paths, List<MultipartFile> files) {
		requireSpace(spaceId);
		if (files == null || files.isEmpty()) {
			throw new BusinessException("请选择要导入的文件夹/文件");
		}
		if (paths != null && !paths.isEmpty() && paths.size() != files.size()) {
			throw new BusinessException("路径与文件数量不一致");
		}
		WikiImportResultDTO result = new WikiImportResultDTO();
		Map<String, Long> dirCache = new HashMap<>();
		Long root = parentId == null ? 0L : parentId;
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (file == null || file.isEmpty()) {
				continue;
			}
			String rawPath = paths != null && i < paths.size() ? paths.get(i) : file.getOriginalFilename();
			String rel = normalizePath(rawPath);
			if (rel == null) {
				result.addSkipped(String.valueOf(rawPath), "非法路径");
				continue;
			}
			String base = baseName(rel);
			if (!isAllowed(base)) {
				result.addSkipped(rel, "仅支持 md/markdown/txt");
				continue;
			}
			try {
				Long parent = resolveDirs(spaceId, root, dirOf(rel), dirCache, result);
				createDoc(spaceId, parent, stripExt(base), decodeText(file.getBytes()));
				result.setDocCount(result.getDocCount() + 1);
			}
			catch (Exception ex) {
				log.warn("import file failed: {}", rel, ex);
				result.addError(rel, ex.getMessage() == null ? "导入失败" : ex.getMessage());
			}
		}
		if (result.getDocCount() == 0 && result.getDirCount() == 0 && !result.getErrors().isEmpty()) {
			throw new BusinessException("导入失败：" + result.getErrors().get(0));
		}
		return result;
	}

	private void importZip(Long spaceId, Long rootParent, byte[] bytes, Map<String, Long> dirCache,
			WikiImportResultDTO result) throws IOException {
		Set<String> seen = new HashSet<>();
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				if (name == null || name.isBlank()) {
					continue;
				}
				String rel = normalizePath(name);
				if (rel == null) {
					result.addSkipped(name, "非法路径");
					continue;
				}
				if (rel.startsWith("__MACOSX/") || baseName(rel).startsWith(".")) {
					continue;
				}
				if (entry.isDirectory() || rel.endsWith("/")) {
					String dir = rel.endsWith("/") ? rel.substring(0, rel.length() - 1) : rel;
					resolveDirs(spaceId, rootParent, dir, dirCache, result);
					continue;
				}
				String base = baseName(rel);
				if (!isAllowed(base)) {
					result.addSkipped(rel, "仅支持 md/markdown/txt");
					continue;
				}
				if (!seen.add(rel)) {
					result.addSkipped(rel, "重复路径");
					continue;
				}
				Long parent = resolveDirs(spaceId, rootParent, dirOf(rel), dirCache, result);
				try {
					createDoc(spaceId, parent, stripExt(base), decodeText(readEntry(zis)));
					result.setDocCount(result.getDocCount() + 1);
				}
				catch (BusinessException ex) {
					result.addSkipped(rel, ex.getMessage());
				}
			}
		}
	}

	/**
	 * 按 {@code a/b/c} 逐级确保目录文档存在，返回最末目录 id；dirPath 为空返回 rootParent。
	 */
	private Long resolveDirs(Long spaceId, Long rootParent, String dirPath, Map<String, Long> dirCache,
			WikiImportResultDTO result) {
		if (!StringUtils.hasText(dirPath)) {
			return rootParent;
		}
		String[] parts = dirPath.split("/");
		Long current = rootParent;
		StringBuilder key = new StringBuilder();
		for (String part : parts) {
			if (part.isBlank()) {
				continue;
			}
			if (key.length() > 0) {
				key.append('/');
			}
			key.append(part);
			String cacheKey = current + "|" + key;
			Long hit = dirCache.get(cacheKey);
			if (hit != null) {
				current = hit;
				continue;
			}
			WikiDoc doc = new WikiDoc();
			doc.setSpaceId(spaceId);
			doc.setParentId(current == null ? 0L : current);
			doc.setTitle(part);
			doc.setContent("");
			doc.setSummary("目录");
			doc.setSort(0);
			doc.setAuthorId(SecurityContextUtil.getCurrentUserId());
			doc.setAuthorName(SecurityContextUtil.getCurrentUsername());
			docMapper.insert(doc);
			dirCache.put(cacheKey, doc.getId());
			current = doc.getId();
			result.setDirCount(result.getDirCount() + 1);
		}
		return current;
	}

	private WikiDoc createDoc(Long spaceId, Long parentId, String title, String content) {
		WikiDoc doc = new WikiDoc();
		doc.setSpaceId(spaceId);
		doc.setParentId(parentId == null ? 0L : parentId);
		doc.setTitle(title);
		doc.setContent(content == null ? "" : content);
		if (StringUtils.hasText(content)) {
			String plain = content.replaceAll("\\s+", " ").trim();
			doc.setSummary(plain.length() > 200 ? plain.substring(0, 200) : plain);
		}
		else {
			doc.setSummary("目录");
		}
		doc.setSort(0);
		doc.setAuthorId(SecurityContextUtil.getCurrentUserId());
		doc.setAuthorName(SecurityContextUtil.getCurrentUsername());
		docMapper.insert(doc);
		return doc;
	}

	private void requireSpace(Long spaceId) {
		if (spaceId == null || spaceMapper.selectById(spaceId) == null) {
			throw new BusinessException("空间不存在");
		}
	}

	private static boolean isAllowed(String filename) {
		return ALLOWED_EXT.contains(extOf(filename));
	}

	private static String extOf(String filename) {
		int dot = filename == null ? -1 : filename.lastIndexOf('.');
		return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
	}

	private static String stripExt(String filename) {
		int dot = filename.lastIndexOf('.');
		return dot <= 0 ? filename : filename.substring(0, dot);
	}

	private static String baseName(String path) {
		int i = path.lastIndexOf('/');
		return i < 0 ? path : path.substring(i + 1);
	}

	private static String dirOf(String path) {
		int i = path.lastIndexOf('/');
		return i < 0 ? "" : path.substring(0, i);
	}

	private static String normalizePath(String raw) {
		if (raw == null) {
			return null;
		}
		String p = raw.replace('\\', '/').trim();
		while (p.startsWith("./")) {
			p = p.substring(2);
		}
		while (p.startsWith("/")) {
			p = p.substring(1);
		}
		if (p.isBlank() || p.contains("..")) {
			return null;
		}
		return p;
	}

	private static String decodeText(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private static byte[] readEntry(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int n;
		long total = 0;
		while ((n = in.read(buf)) > 0) {
			total += n;
			if (total > MAX_ENTRY_BYTES) {
				throw new BusinessException("单个文档超过 2MB");
			}
			bos.write(buf, 0, n);
		}
		return bos.toByteArray();
	}

}
