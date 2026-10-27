package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 博客图片上传（登录用户；文章封面/正文配图）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/upload")
@Tag(name = "博客上传", description = "图片上传，返回 /uploads/** 可访问路径")
public class BlogUploadController {

	/**
	 * 允许的图片扩展名
	 */
	private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

	/**
	 * 单文件最大 5MB
	 */
	private static final long MAX_SIZE = 5L * 1024 * 1024;

	@Value("${blog.upload-dir:./uploads/blog}")
	private String uploadDir;

	/**
	 * 上传图片（作者：文章封面/正文）
	 */
	@PostMapping("/image")
	@RequireLogin
	@PermissionCode("blog:article:add")
	@Operation(summary = "上传图片", description = "jpg/png/gif/webp，≤5MB；返回 /blog/uploads/** 路径")
	public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
		return Result.success(doUpload(file));
	}

	/**
	 * 上传评论图片/表情包（登录即可，不要求作者权限）
	 */
	@PostMapping("/comment-image")
	@RequireLogin
	@Operation(summary = "上传评论图片", description = "登录用户评论配图/表情包；jpg/png/gif/webp，≤5MB")
	public Result<String> uploadCommentImage(@RequestParam("file") MultipartFile file) {
		return Result.success(doUpload(file));
	}

	private String doUpload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("文件不能为空");
		}
		if (file.getSize() > MAX_SIZE) {
			throw new BusinessException("图片不能超过 5MB");
		}
		String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
		String ext = "";
		int dot = original.lastIndexOf('.');
		if (dot > 0 && dot < original.length() - 1) {
			ext = original.substring(dot + 1).toLowerCase(Locale.ROOT);
		}
		if (!ALLOWED_EXT.contains(ext)) {
			throw new BusinessException("仅支持 jpg/jpeg/png/gif/webp");
		}

		String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
		try {
			Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
			Files.createDirectories(dir);
			Path target = dir.resolve(filename).normalize();
			// 防路径穿越
			if (!target.startsWith(dir)) {
				throw new BusinessException("非法文件路径");
			}
			file.transferTo(target);
		}
		catch (IOException e) {
			log.error("图片上传失败", e);
			throw new BusinessException("上传失败，请稍后重试");
		}
		return "/blog/uploads/" + filename;
	}

}
