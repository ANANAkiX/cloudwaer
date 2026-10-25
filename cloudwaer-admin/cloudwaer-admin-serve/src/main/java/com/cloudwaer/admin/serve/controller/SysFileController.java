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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
