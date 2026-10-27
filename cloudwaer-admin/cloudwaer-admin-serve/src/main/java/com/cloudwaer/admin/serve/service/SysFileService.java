package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.admin.serve.entity.SysFile;
import com.cloudwaer.admin.serve.entity.SysOssConfig;
import com.cloudwaer.admin.serve.mapper.SysFileMapper;
import com.cloudwaer.admin.serve.mapper.SysOssConfigMapper;
import com.cloudwaer.admin.serve.storage.FileStorage;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 平台文件服务
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class SysFileService {

	@Autowired
	private SysFileMapper fileMapper;

	@Autowired
	private SysOssConfigMapper ossConfigMapper;

	@Autowired
	private List<FileStorage> storages;

	public PageResult<SysFile> page(Long current, Long size, String keyword, String provider) {
		Page<SysFile> page = new Page<>(current, size);
		LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(SysFile::getFileName, keyword);
		}
		if (StringUtils.hasText(provider)) {
			wrapper.eq(SysFile::getProvider, provider);
		}
		wrapper.orderByDesc(SysFile::getCreateTime);
		IPage<SysFile> pr = fileMapper.selectPage(page, wrapper);
		return new PageResult<>(pr.getRecords(), pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Transactional(rollbackFor = Exception.class)
	public SysFile upload(MultipartFile file, String bizType, String bizId) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("请选择文件");
		}
		SysOssConfig config = defaultConfig();
		String provider = config == null ? "LOCAL" : config.getProvider();
		FileStorage storage = storageOf(provider);
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
		String ext = "";
		int dot = original.lastIndexOf('.');
		if (dot >= 0) {
			ext = original.substring(dot);
		}
		String base = config != null && StringUtils.hasText(config.getBasePath()) ? config.getBasePath() : "uploads";
		String objectKey = base + "/" + date + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

		try (InputStream in = file.getInputStream()) {
			storage.upload(config, objectKey, in, file.getSize(), file.getContentType());
		}
		catch (Exception e) {
			throw new BusinessException("上传失败：" + e.getMessage());
		}

		SysFile entity = new SysFile();
		entity.setFileName(original);
		entity.setProvider(provider);
		entity.setOssConfigId(config == null ? null : config.getId());
		entity.setObjectKey(objectKey);
		entity.setBucket(config == null ? null : config.getBucket());
		entity.setContentType(file.getContentType());
		entity.setFileSize(file.getSize());
		entity.setBizType(bizType);
		entity.setBizId(bizId);
		// 先占位 NOT NULL，再写公网可读地址（依赖 OSS 公共读 / CDN）
		entity.setAccessUrl("");
		fileMapper.insert(entity);
		entity.setAccessUrl(buildPublicAccessUrl(config, provider, objectKey));
		fileMapper.updateById(entity);
		return entity;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean delete(Long id) {
		SysFile file = fileMapper.selectById(id);
		if (file == null) {
			throw new BusinessException("文件不存在");
		}
		try {
			SysOssConfig cfg = file.getOssConfigId() == null ? null : ossConfigMapper.selectById(file.getOssConfigId());
			storageOf(file.getProvider()).delete(cfg, file.getObjectKey());
		}
		catch (Exception e) {
			log.warn("删除存储对象失败 file={}, err={}", id, e.getMessage());
		}
		fileMapper.deleteById(id);
		return Boolean.TRUE;
	}

	public SysFile detail(Long id) {
		SysFile file = fileMapper.selectById(id);
		if (file == null) {
			throw new BusinessException("文件不存在");
		}
		return file;
	}

	/**
	 * 实时签名访问 URL：仅用于需要临时直连 OSS 的场景；默认展示走 /display 代理
	 */
	public String signedAccessUrl(Long id) {
		SysFile file = detail(id);
		if ("LOCAL".equalsIgnoreCase(file.getProvider())) {
			return file.getAccessUrl();
		}
		SysOssConfig cfg = ossConfigOf(file);
		String signed = storageOf(file.getProvider()).accessUrl(cfg, file.getObjectKey());
		return StringUtils.hasText(signed) ? signed : file.getAccessUrl();
	}

	/**
	 * 登录后经本站读取文件内容（OSS 私有桶；不暴露可离线打开的永久直链）
	 */
	public void writeDisplay(Long id, jakarta.servlet.http.HttpServletResponse response) throws Exception {
		SysFile file = detail(id);
		FileStorage storage = storageOf(file.getProvider());
		SysOssConfig cfg = ossConfigOf(file);
		try (InputStream in = storage.open(cfg, file.getObjectKey())) {
			if (in == null) {
				response.setStatus(404);
				return;
			}
			response.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
			in.transferTo(response.getOutputStream());
		}
	}

	public void writeZip(List<Long> ids, ZipOutputStream zos) throws Exception {
		for (Long id : ids) {
			SysFile file = fileMapper.selectById(id);
			if (file == null) {
				continue;
			}
			SysOssConfig cfg = file.getOssConfigId() == null ? null : ossConfigMapper.selectById(file.getOssConfigId());
			try (InputStream in = storageOf(file.getProvider()).open(cfg, file.getObjectKey())) {
				if (in == null) {
					continue;
				}
				zos.putNextEntry(new ZipEntry(file.getFileName()));
				in.transferTo(zos);
				zos.closeEntry();
			}
		}
	}

	public List<SysOssConfig> listOssConfig() {
		return ossConfigMapper
			.selectList(new LambdaQueryWrapper<SysOssConfig>().orderByDesc(SysOssConfig::getIsDefault));
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean saveOssConfig(SysOssConfig config) {
		if (config == null || !StringUtils.hasText(config.getProvider())) {
			throw new BusinessException("供应商不能为空");
		}
		if (config.getId() == null) {
			ossConfigMapper.insert(config);
		}
		else {
			ossConfigMapper.updateById(config);
		}
		return Boolean.TRUE;
	}

	public FileStorage storageOf(String provider) {
		String p = provider == null ? "LOCAL" : provider;
		return storages.stream()
			.filter(s -> s.provider().equalsIgnoreCase(p))
			.findFirst()
			.orElseThrow(() -> new BusinessException("未实现的存储供应商：" + p));
	}

	/**
	 * 按文件记录取 OSS 配置；无则默认配置
	 */
	public SysOssConfig ossConfigOf(SysFile file) {
		if (file != null && file.getOssConfigId() != null) {
			SysOssConfig cfg = ossConfigMapper.selectById(file.getOssConfigId());
			if (cfg != null) {
				return cfg;
			}
		}
		return defaultConfig();
	}

	private SysOssConfig defaultConfig() {
		List<SysOssConfig> list = ossConfigMapper
			.selectList(new LambdaQueryWrapper<SysOssConfig>().eq(SysOssConfig::getStatus, 1)
				.orderByDesc(SysOssConfig::getIsDefault)
				.last("LIMIT 1"));
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * 公网访问地址：LOCAL → 本站代理；OSS → CDN 域名优先，否则 https://bucket.endpoint/key
	 * <p>
	 * 依赖：桶或 uploads/ 前缀在控制台配置为公共读
	 */
	private String buildPublicAccessUrl(SysOssConfig config, String provider, String objectKey) {
		if ("LOCAL".equalsIgnoreCase(provider)) {
			return "/api/admin/file/raw?path=" + objectKey;
		}
		if (config != null && StringUtils.hasText(config.getCdnDomain())) {
			return config.getCdnDomain().replaceAll("/+$", "") + "/" + objectKey;
		}
		if (config != null && StringUtils.hasText(config.getEndpoint()) && StringUtils.hasText(config.getBucket())) {
			String endpoint = config.getEndpoint().replaceAll("/+$", "");
			// virtual-host 风格
			return "https://" + config.getBucket() + "." + endpoint + "/" + objectKey;
		}
		// 兜底：相对 display，需登录
		return "/api/admin/file/display?objectKey=" + objectKey;
	}

}
