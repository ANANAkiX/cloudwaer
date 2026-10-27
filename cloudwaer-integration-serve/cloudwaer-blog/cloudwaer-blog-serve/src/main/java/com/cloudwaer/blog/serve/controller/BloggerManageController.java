package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.BloggerStatusDTO;
import com.cloudwaer.blog.api.dto.BloggerSuspendDTO;
import com.cloudwaer.blog.serve.service.BloggerManageService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 博客主管理控制器（暂停/解封/状态）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/blogger")
@Tag(name = "博客主管理", description = "暂停至指定时间、解封、状态查询；取消角色走 admin assign-roles")
public class BloggerManageController {

	/**
	 * 单次批量查询上限
	 */
	private static final int MAX_STATUS_IDS = 100;

	@Autowired
	private BloggerManageService bloggerManageService;

	/**
	 * 暂停博客主发布权限至指定时间
	 */
	@PostMapping("/suspend")
	@PermissionCode("blog:blogger:suspend")
	@Operation(summary = "暂停博客主", description = "suspendUntil 为空表示永久；发布链路即刻拦截")
	public Result<Boolean> suspend(@RequestBody @Validated BloggerSuspendDTO dto) {
		return Result.success(bloggerManageService.suspend(dto));
	}

	/**
	 * 解除暂停
	 */
	@PostMapping("/release")
	@PermissionCode("blog:blogger:suspend")
	@Operation(summary = "解除暂停", description = "幂等；解除后发布链路不再拦截")
	public Result<Boolean> release(@RequestParam("userId") Long userId) {
		return Result.success(bloggerManageService.release(userId));
	}

	/**
	 * 批量查询暂停状态（管理端列表回填）
	 */
	@GetMapping("/status")
	@PermissionCode("blog:blogger:view")
	@Operation(summary = "批量查询暂停状态", description = "userIds 逗号分隔，最多100个")
	public Result<List<BloggerStatusDTO>> status(@RequestParam("userIds") List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Result.success(List.of());
		}
		if (userIds.size() > MAX_STATUS_IDS) {
			userIds = userIds.subList(0, MAX_STATUS_IDS);
		}
		return Result.success(bloggerManageService.listByUserIds(userIds));
	}

	/**
	 * 我的暂停状态（前台自助查询，登录即可）
	 */
	@GetMapping("/my-status")
	@RequireLogin
	@Operation(summary = "我的暂停状态", description = "前台个人中心展示；无暂停记录返回 null")
	public Result<BloggerStatusDTO> myStatus() {
		return Result.success(bloggerManageService.myStatus());
	}

}
