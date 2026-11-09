package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.serve.service.TriggerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 触发器控制器：外部唤醒触发器节点
 */
@RestController
@RequestMapping("/workflow/trigger")
@Tag(name = "触发器")
public class TriggerController {

	@Autowired
	private TriggerService triggerService;

	@PostMapping("/fire")
	@PermissionCode("workflow:instance:operate")
	@Operation(summary = "触发唤醒（使触发器节点继续推进）")
	public Result<Boolean> fire(@RequestParam String instanceId, @RequestParam String nodeId) {
		triggerService.fire(instanceId, nodeId);
		return Result.success(true);
	}

}
