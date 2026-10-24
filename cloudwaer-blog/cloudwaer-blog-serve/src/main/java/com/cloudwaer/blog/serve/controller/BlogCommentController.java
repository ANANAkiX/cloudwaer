package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.CommentDTO;
import com.cloudwaer.blog.api.dto.CommentSaveDTO;
import com.cloudwaer.blog.serve.service.BlogCommentService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客评论控制器（发表 + 公开列表）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog")
@Tag(name = "博客评论", description = "评论发表与文章评论树")
public class BlogCommentController {

	@Autowired
	private BlogCommentService blogCommentService;

	/**
	 * 发表评论（登录访客及以上）
	 */
	@PostMapping("/comment/add")
	@RequireLogin
	@PermissionCode("blog:comment:add")
	@Operation(summary = "发表评论", description = "登录用户发表，审核通过后前台可见")
	public Result<Long> add(@RequestBody @Validated CommentSaveDTO dto, HttpServletRequest request) {
		return Result.success(blogCommentService.addComment(dto, resolveClientIp(request)));
	}

	private String resolveClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
			int idx = ip.indexOf(',');
			return idx > 0 ? ip.substring(0, idx).trim() : ip.trim();
		}
		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
			return ip.trim();
		}
		return request.getRemoteAddr();
	}

	/**
	 * 文章公开评论树
	 */
	@GetMapping("/open/comment/list")
	@Operation(summary = "文章评论", description = "仅已通过审核的评论，含楼中楼")
	public Result<List<CommentDTO>> list(@RequestParam("articleId") Long articleId) {
		return Result.success(blogCommentService.listByArticle(articleId));
	}

}
