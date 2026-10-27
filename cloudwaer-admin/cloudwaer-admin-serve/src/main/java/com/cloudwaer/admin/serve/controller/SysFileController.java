package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.serve.entity.SysFile;
import com.cloudwaer.admin.serve.entity.SysOssConfig;
import com.cloudwaer.admin.serve.service.SysFileService;
import com.cloudwaer.admin.serve.storage.FileStorage;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 平台文件管理
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/file")
@Tag(name = "平台文件管理", description = "上传/下载/删除（同步 OSS）/批量下载/OSS 配置")
public class SysFileController {

	/**
	 * 文本预览上限 20MB
	 */
	private static final long MAX_PREVIEW_TEXT_SIZE = 20L * 1024 * 1024;

	/**
	 * 支持抽取文本的 Office 格式（legacy doc/xls/ppt 未覆盖）
	 */
	private static final Set<String> OFFICE_EXTS = Set.of("docx", "xlsx", "pptx");

	@Autowired
	private SysFileService sysFileService;

	@Autowired
	private List<FileStorage> storages;

	@GetMapping("/page")
	@PermissionCode("sys:file:view")
	@Operation(summary = "文件分页")
	public Result<PageResult<SysFile>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "provider", required = false) String provider) {
		return Result.success(sysFileService.page(current, size, keyword, provider));
	}

	@PostMapping("/upload")
	@PermissionCode("sys:file:upload")
	@Operation(summary = "上传文件")
	public Result<SysFile> upload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "bizType", required = false) String bizType,
			@RequestParam(value = "bizId", required = false) String bizId) {
		return Result.success(sysFileService.upload(file, bizType, bizId));
	}

	@DeleteMapping("/delete")
	@PermissionCode("sys:file:delete")
	@Operation(summary = "删除文件（同步删 OSS 对象）")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(sysFileService.delete(id));
	}

	@GetMapping("/download")
	@PermissionCode("sys:file:view")
	@Operation(summary = "单文件下载")
	public void download(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
		SysFile file = sysFileService.detail(id);
		FileStorage storage = sysFileService.storageOf(file.getProvider());
		try (InputStream in = storage.open(sysFileService.ossConfigOf(file), file.getObjectKey())) {
			if (in == null) {
				throw new BusinessException("文件不存在或已删除");
			}
			response.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
			String name = URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + name);
			in.transferTo(response.getOutputStream());
		}
	}

	@PostMapping("/batch-download")
	@PermissionCode("sys:file:view")
	@Operation(summary = "批量打包下载 zip")
	public void batchDownload(@RequestBody List<Long> ids, HttpServletResponse response) throws Exception {
		if (ids == null || ids.isEmpty()) {
			throw new BusinessException("请选择文件");
		}
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=files.zip");
		try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
			sysFileService.writeZip(ids, zos);
		}
	}

	@GetMapping("/raw")
	@Operation(summary = "本地文件预览（匿名，仅 LOCAL）")
	public void raw(@RequestParam("path") String path, HttpServletResponse response) throws Exception {
		try (InputStream in = sysFileService.storageOf("LOCAL").open(null, path)) {
			if (in == null) {
				response.setStatus(404);
				return;
			}
			response.setContentType("application/octet-stream");
			in.transferTo(response.getOutputStream());
		}
	}

	/**
	 * 内联预览（图片 / PDF / 视频等浏览器可直接渲染的类型）
	 *
	 * <p>
	 * Content-Type 按扩展名白名单推导，HTML/SVG 一律降级为纯文本，避免同源脚本执行。
	 * </p>
	 */
	@GetMapping("/preview")
	@PermissionCode("sys:file:view")
	@Operation(summary = "文件内联预览", description = "图片/PDF/视频按 Content-Type inline 输出；HTML/SVG 降级为纯文本")
	public void preview(@RequestParam("id") Long id, HttpServletResponse response) throws Exception {
		SysFile file = sysFileService.detail(id);
		FileStorage storage = sysFileService.storageOf(file.getProvider());
		try (InputStream in = storage.open(sysFileService.ossConfigOf(file), file.getObjectKey())) {
			if (in == null) {
				throw new BusinessException("文件不存在或已删除");
			}
			response.setContentType(previewContentType(file.getFileName()));
			response.setHeader("X-Content-Type-Options", "nosniff");
			response.setHeader("Content-Disposition", "inline");
			in.transferTo(response.getOutputStream());
		}
	}

	/**
	 * 文本型预览（代码/纯文本直读，Office 文档用 POI 抽正文）
	 */
	@GetMapping("/preview-text")
	@PermissionCode("sys:file:view")
	@Operation(summary = "文件文本预览", description = "txt/md/json/代码与 docx/xlsx/pptx 抽取文本；限 20MB")
	public Result<String> previewText(@RequestParam("id") Long id) {
		SysFile file = sysFileService.detail(id);
		if (file.getFileSize() != null && file.getFileSize() > MAX_PREVIEW_TEXT_SIZE) {
			throw new BusinessException("文件过大（超过 20MB），无法在线预览");
		}
		String ext = extOf(file.getFileName());
		FileStorage storage = sysFileService.storageOf(file.getProvider());
		try (InputStream in = storage.open(sysFileService.ossConfigOf(file), file.getObjectKey())) {
			if (in == null) {
				throw new BusinessException("文件不存在或已删除");
			}
			byte[] bytes = in.readAllBytes();
			if (OFFICE_EXTS.contains(ext)) {
				return Result.success(extractOfficeText(bytes, ext));
			}
			return Result.success(toPlainText(bytes));
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.warn("文件文本预览失败: id={}", id, e);
			throw new BusinessException("预览失败，请下载后查看");
		}
	}

	/**
	 * 浏览器可内联渲染的类型白名单（按扩展名）；未列出的一律 octet-stream
	 */
	private String previewContentType(String fileName) {
		return switch (extOf(fileName)) {
			case "jpg", "jpeg" -> "image/jpeg";
			case "png" -> "image/png";
			case "gif" -> "image/gif";
			case "webp" -> "image/webp";
			case "bmp" -> "image/bmp";
			case "ico" -> "image/x-icon";
			case "pdf" -> "application/pdf";
			case "mp4", "m4v" -> "video/mp4";
			case "webm" -> "video/webm";
			case "ogg", "ogv" -> "video/ogg";
			case "mp3" -> "audio/mpeg";
			case "wav" -> "audio/wav";
			default -> "application/octet-stream";
		};
	}

	private String extOf(String fileName) {
		if (fileName == null) {
			return "";
		}
		int dot = fileName.lastIndexOf('.');
		return dot >= 0 && dot < fileName.length() - 1 ? fileName.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
	}

	private String extractOfficeText(byte[] bytes, String ext) throws Exception {
		try (InputStream in = new ByteArrayInputStream(bytes)) {
			return switch (ext) {
				case "docx" -> {
					try (XWPFDocument doc = new XWPFDocument(in)) {
						yield new XWPFWordExtractor(doc).getText();
					}
				}
				case "xlsx" -> {
					try (XSSFWorkbook wb = new XSSFWorkbook(in)) {
						yield extractSheetText(wb);
					}
				}
				case "pptx" -> {
					try (XMLSlideShow ppt = new XMLSlideShow(in)) {
						yield extractSlideText(ppt);
					}
				}
				default -> throw new BusinessException("该格式暂不支持在线预览");
			};
		}
	}

	private String extractSheetText(XSSFWorkbook wb) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);
			sb.append("【").append(sheet.getSheetName()).append("】\n");
			for (Row row : sheet) {
				List<String> cells = new ArrayList<>();
				for (int c = 0; c < row.getLastCellNum(); c++) {
					cells.add(cellText(row.getCell(c)));
				}
				sb.append(String.join(" | ", cells)).append('\n');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private String cellText(Cell cell) {
		if (cell == null) {
			return "";
		}
		return switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> {
				double v = cell.getNumericCellValue();
				yield (v == Math.floor(v) && !Double.isInfinite(v)) ? String.valueOf((long) v) : String.valueOf(v);
			}
			case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
			case FORMULA -> cell.getCellFormula();
			default -> "";
		};
	}

	private String extractSlideText(XMLSlideShow ppt) {
		StringBuilder sb = new StringBuilder();
		int idx = 1;
		for (XSLFSlide slide : ppt.getSlides()) {
			sb.append("— 第 ").append(idx++).append(" 页 —\n");
			for (XSLFShape shape : slide.getShapes()) {
				if (shape instanceof XSLFTextShape textShape) {
					String text = textShape.getText();
					if (text != null && !text.isBlank()) {
						sb.append(text.trim()).append('\n');
					}
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * 按 UTF-8 解码为可读文本；含 NUL 字节视为二进制，拒绝
	 */
	private String toPlainText(byte[] bytes) {
		int probe = Math.min(bytes.length, 8192);
		for (int i = 0; i < probe; i++) {
			if (bytes[i] == 0) {
				throw new BusinessException("该文件不支持文本预览");
			}
		}
		String text = new String(bytes, StandardCharsets.UTF_8);
		return text.isBlank() ? "（空文件）" : text;
	}

	@PostMapping("/my/upload")
	@RequireLogin
	@Operation(summary = "登录用户上传", description = "头像等，bizType 默认 avatar")
	public Result<SysFile> myUpload(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "bizType", defaultValue = "avatar") String bizType) {
		Long userId = SecurityContextUtil.getCurrentUserId();
		return Result.success(sysFileService.upload(file, bizType, userId == null ? null : String.valueOf(userId)));
	}

	/**
	 * 实时签名 URL（可选：需要临时直连时；默认建议用 /display）
	 */
	@GetMapping("/signed")
	@RequireLogin
	@Operation(summary = "文件签名访问地址", description = "私有 OSS 临时直链，会过期")
	public Result<String> signed(@RequestParam("id") Long id) {
		return Result.success(sysFileService.signedAccessUrl(id));
	}

	/**
	 * 登录后经本站读文件（兜底；主路径为 OSS 公共读直链）
	 */
	@GetMapping("/display")
	@RequireLogin
	@Operation(summary = "登录后展示文件", description = "兜底代理；请优先使用 access_url 公网地址")
	public void display(@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "objectKey", required = false) String objectKey, HttpServletResponse response)
			throws Exception {
		if (id != null) {
			sysFileService.writeDisplay(id, response);
			return;
		}
		throw new BusinessException("缺少文件 id");
	}

	@GetMapping("/oss/list")
	@PermissionCode("sys:oss:config")
	@Operation(summary = "OSS 配置列表")
	public Result<List<SysOssConfig>> ossList() {
		return Result.success(sysFileService.listOssConfig());
	}

	@PostMapping("/oss/save")
	@PermissionCode("sys:oss:config")
	@Operation(summary = "保存 OSS 配置")
	public Result<Boolean> ossSave(@RequestBody SysOssConfig config) {
		return Result.success(sysFileService.saveOssConfig(config));
	}

}
