package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.AuthorApplyDTO;
import com.cloudwaer.blog.serve.service.BlogAuthorApplyService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客主申请控制器（MVP：自助打标 + 管理端处理）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/author")
@Tag(name = "博客主申请", description = "自助申请与管理端处理；角色分配仍走用户管理页")
public class BlogAuthorApplyController {

	@Autowired
	private BlogAuthorApplyService blogAuthorApplyService;

	/**
	 * 自助申请
	 */
	@PostMapping("/apply")
	@RequireLogin
	@PermissionCode("blog:author:apply")
	@Operation(summary = "申请成为博客主", description = "登录访客自助申请，待管理员在用户页分配 BLOGGER")
	public Result<Long> apply(@RequestParam(value = "reason", required = false) String reason) {
		return Result.success(blogAuthorApplyService.apply(reason));
	}

	/**
	 * 我的申请状态
	 */
	@GetMapping("/my")
	@RequireLogin
	@Operation(summary = "我的申请", description = "查询当前用户最近一次申请")
	public Result<AuthorApplyDTO> my() {
		return Result.success(blogAuthorApplyService.myApply());
	}

	/**
	 * 管理端申请分页
	 */
	@GetMapping("/page")
	@PermissionCode("blog:author:view")
	@Operation(summary = "申请分页", description = "支持按状态与关键字筛选")
	public Result<PageResult<AuthorApplyDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "applyStatus", required = false) Integer applyStatus,
			@RequestParam(value = "keyword", required = false) String keyword) {
		return Result.success(blogAuthorApplyService.page(current, size, applyStatus, keyword));
	}

	/**
	 * 处理申请
	 */
	@PostMapping("/handle")
	@PermissionCode("blog:author:handle")
	@Operation(summary = "处理申请", description = "1-通过，2-拒绝；角色分配请在用户管理完成")
	public Result<Boolean> handle(@RequestParam("id") Long id, @RequestParam("applyStatus") Integer applyStatus,
			@RequestParam(value = "remark", required = false) String remark) {
		return Result.success(blogAuthorApplyService.handle(id, applyStatus, remark));
	}

}
