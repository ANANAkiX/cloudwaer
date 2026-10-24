package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.CommentAuditDTO;
import com.cloudwaer.blog.api.dto.CommentDTO;
import com.cloudwaer.blog.serve.service.BlogCommentService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客评论管理控制器（管理端审核）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/comment")
@Tag(name = "博客评论管理", description = "评论分页、审核与删除")
public class BlogCommentAdminController {

	@Autowired
	private BlogCommentService blogCommentService;

	/**
	 * 评论分页
	 */
	@GetMapping("/page")
	@PermissionCode("blog:comment:view")
	@Operation(summary = "评论分页", description = "支持按文章、审核状态、关键字筛选")
	public Result<PageResult<CommentDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "articleId", required = false) Long articleId,
			@RequestParam(value = "auditStatus", required = false) Integer auditStatus,
			@RequestParam(value = "keyword", required = false) String keyword) {
		return Result.success(blogCommentService.page(current, size, articleId, auditStatus, keyword));
	}

	/**
	 * 审核评论
	 */
	@PostMapping("/audit")
	@PermissionCode("blog:comment:audit")
	@Operation(summary = "审核评论", description = "1-通过，2-拒绝；仅待审核可操作")
	public Result<Boolean> audit(@RequestBody @Validated CommentAuditDTO dto) {
		return Result.success(blogCommentService.audit(dto.getId(), dto.getAuditStatus()));
	}

	/**
	 * 删除评论
	 */
	@DeleteMapping("/delete")
	@PermissionCode("blog:comment:delete")
	@Operation(summary = "删除评论", description = "逻辑删除，连带删除同根楼中楼")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(blogCommentService.deleteComment(id));
	}

}
