package com.cloudwaer.admin.serve.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.admin.serve.dto.MessageItemVO;
import com.cloudwaer.admin.serve.service.MessageService;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 站内信（用户自助：收件箱/已读/删除）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/message")
@Tag(name = "站内信", description = "收件箱、未读数、已读与删除")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@GetMapping("/unread-count")
	@RequireLogin
	@Operation(summary = "未读数")
	public Result<Long> unreadCount() {
		return Result.success(messageService.unreadCount(SecurityContextUtil.getCurrentUserId()));
	}

	@GetMapping("/inbox")
	@RequireLogin
	@Operation(summary = "收件箱分页")
	public Result<Page<MessageItemVO>> inbox(@RequestParam(value = "page", defaultValue = "1") long page,
			@RequestParam(value = "size", defaultValue = "10") long size) {
		return Result.success(messageService.inbox(SecurityContextUtil.getCurrentUserId(), page, size));
	}

	@PutMapping("/read")
	@RequireLogin
	@Operation(summary = "批量已读")
	public Result<Boolean> read(@RequestBody List<Long> recipientIds) {
		return Result.success(messageService.markRead(SecurityContextUtil.getCurrentUserId(), recipientIds));
	}

	@PutMapping("/read-all")
	@RequireLogin
	@Operation(summary = "全部已读")
	public Result<Boolean> readAll() {
		return Result.success(messageService.markAllRead(SecurityContextUtil.getCurrentUserId()));
	}

	@DeleteMapping("/{recipientId}")
	@RequireLogin
	@Operation(summary = "删除收件箱条目")
	public Result<Boolean> remove(@PathVariable("recipientId") Long recipientId) {
		return Result.success(messageService.remove(SecurityContextUtil.getCurrentUserId(), recipientId));
	}

}
