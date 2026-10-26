package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.LikeToggleDTO;
import com.cloudwaer.blog.api.dto.TopicDTO;
import com.cloudwaer.blog.api.dto.TopicReplyDTO;
import com.cloudwaer.blog.api.dto.TopicReplySaveDTO;
import com.cloudwaer.blog.api.dto.TopicSaveDTO;
import com.cloudwaer.blog.serve.service.BlogLikeService;
import com.cloudwaer.blog.serve.service.BlogTopicService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 微论坛控制器（公开读 + 登录发帖 + 管理端）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog")
@Tag(name = "微论坛", description = "话题短帖、回复与点赞")
public class BlogTopicController {

	@Autowired
	private BlogTopicService blogTopicService;

	@Autowired
	private BlogLikeService blogLikeService;

	// ===== 公开 =====

	/**
	 * 话题分页
	 */
	@GetMapping("/open/topic/list")
	@Operation(summary = "话题列表", description = "置顶优先，支持关键字与加精筛选")
	public Result<PageResult<TopicDTO>> publicList(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "essence", required = false) Boolean essence) {
		return Result.success(blogTopicService.publicPage(current, size, keyword, essence));
	}

	/**
	 * 话题详情
	 */
	@GetMapping("/open/topic/detail")
	@Operation(summary = "话题详情")
	public Result<TopicDTO> publicDetail(@RequestParam("id") Long id) {
		return Result.success(blogTopicService.publicDetail(id));
	}

	/**
	 * 话题回复树
	 */
	@GetMapping("/open/topic/replies")
	@Operation(summary = "话题回复", description = "两级楼中楼")
	public Result<List<TopicReplyDTO>> replies(@RequestParam("topicId") Long topicId) {
		return Result.success(blogTopicService.listReplies(topicId));
	}

	// ===== 登录写 =====

	/**
	 * 发帖
	 */
	@PostMapping("/topic/save")
	@RequireLogin
	@PermissionCode("blog:topic:post")
	@Operation(summary = "发布话题")
	public Result<Long> save(@RequestBody @Validated TopicSaveDTO dto) {
		return Result.success(blogTopicService.saveTopic(dto));
	}

	/**
	 * 回复
	 */
	@PostMapping("/topic/reply")
	@RequireLogin
	@PermissionCode("blog:topic:post")
	@Operation(summary = "回复话题")
	public Result<Long> reply(@RequestBody @Validated TopicReplySaveDTO dto) {
		return Result.success(blogTopicService.reply(dto));
	}

	/**
	 * 点赞切换（登录即可；blog:like 权限码从未进过种子，已按约定收敛为登录级）
	 */
	@PostMapping("/like/toggle")
	@RequireLogin
	@Operation(summary = "点赞切换", description = "再点取消；type:1文章 2评论 3话题 4回复；登录即可")
	public Result<LikeToggleDTO> likeToggle(@RequestParam("type") Integer type,
			@RequestParam("targetId") Long targetId) {
		return Result.success(blogLikeService.toggle(type, targetId));
	}

	// ===== 管理端 =====

	/**
	 * 管理分页
	 */
	@GetMapping("/topic/admin/page")
	@PermissionCode("blog:topic:view")
	@Operation(summary = "话题管理分页")
	public Result<PageResult<TopicDTO>> adminPage(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "isTop", required = false) Integer isTop,
			@RequestParam(value = "isEssence", required = false) Integer isEssence) {
		return Result.success(blogTopicService.adminPage(current, size, keyword, isTop, isEssence));
	}

	/**
	 * 置顶/加精
	 */
	@PutMapping("/topic/admin/flags")
	@PermissionCode("blog:topic:manage")
	@Operation(summary = "置顶/加精")
	public Result<Boolean> adminFlags(@RequestParam("id") Long id,
			@RequestParam(value = "isTop", required = false) Integer isTop,
			@RequestParam(value = "isEssence", required = false) Integer isEssence) {
		return Result.success(blogTopicService.adminUpdateFlags(id, isTop, isEssence));
	}

	/**
	 * 删除话题
	 */
	@DeleteMapping("/topic/admin/delete")
	@PermissionCode("blog:topic:manage")
	@Operation(summary = "删除话题")
	public Result<Boolean> adminDelete(@RequestParam("id") Long id) {
		return Result.success(blogTopicService.adminDelete(id));
	}

	/**
	 * 刷新全部已发布文章热度
	 */
	@PostMapping("/article/refresh-heat")
	@PermissionCode("blog:article:edit")
	@Operation(summary = "刷新文章热度", description = "对数加权+时间衰减兜底重算")
	public Result<Integer> refreshHeat() {
		return Result.success(blogTopicService.refreshArticleHeat());
	}

}
