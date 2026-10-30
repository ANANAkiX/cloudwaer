package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.SendMessageDTO;
import com.cloudwaer.admin.serve.service.MessageService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内信内部发送接口（Feign：业务模块 → admin）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/message")
@Tag(name = "站内信内部接口", description = "业务模块经 Feign 发送系统提示")
public class SysMessageAdminController {

	@Autowired
	private MessageService messageService;

	@PostMapping("/send-internal")
	@PermissionCode(value = "admin:message:send-internal", enable = false)
	@Operation(summary = "发送站内信（内部）", description = "Feign 内部调用；业务模块用它落系统提示")
	public Result<Boolean> sendInternal(@RequestBody SendMessageDTO dto) {
		return Result.success(messageService.send(dto));
	}

}
