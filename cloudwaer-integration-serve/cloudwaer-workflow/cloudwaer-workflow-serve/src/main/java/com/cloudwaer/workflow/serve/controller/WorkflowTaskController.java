package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.api.dto.TaskCompleteDTO;
import com.cloudwaer.workflow.api.dto.WorkflowTaskDTO;
import com.cloudwaer.workflow.serve.service.WorkflowTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户任务控制器
 */
@RestController
@RequestMapping("/workflow/task")
@Tag(name = "用户任务")
public class WorkflowTaskController {

	@Autowired
	private WorkflowTaskService taskService;

	@GetMapping("/todo")
	@PermissionCode("workflow:task:todo")
	@Operation(summary = "待办任务")
	public Result<PageResult<WorkflowTaskDTO>> todo(PageDTO pageDTO) {
		return Result.success(taskService.listTodo(pageDTO));
	}

	@GetMapping("/done")
	@PermissionCode("workflow:task:done")
	@Operation(summary = "已办任务")
	public Result<PageResult<WorkflowTaskDTO>> done(PageDTO pageDTO) {
		return Result.success(taskService.listDone(pageDTO));
	}

	@GetMapping("/by-instance")
	@PermissionCode("workflow:task:list")
	@Operation(summary = "按实例查任务")
	public Result<PageResult<WorkflowTaskDTO>> byInstance(@RequestParam String instanceId) {
		return Result.success(taskService.listByInstance(instanceId));
	}

	@GetMapping("/detail")
	@PermissionCode("workflow:task:detail")
	@Operation(summary = "任务详情")
	public Result<WorkflowTaskDTO> detail(@RequestParam String taskId) {
		return Result.success(taskService.getDetail(taskId));
	}

	@PostMapping("/claim")
	@PermissionCode("workflow:task:claim")
	@Operation(summary = "认领任务")
	public Result<Boolean> claim(@RequestParam String taskId) {
		return Result.success(taskService.claim(taskId));
	}

	@PostMapping("/complete")
	@PermissionCode("workflow:task:complete")
	@Operation(summary = "完成任务（审批/驳回/转办/退回）")
	public Result<Boolean> complete(@Valid @RequestBody TaskCompleteDTO dto) {
		return Result.success(taskService.complete(dto));
	}

	@DeleteMapping("/delete")
	@PermissionCode("workflow:task:delete")
	@Operation(summary = "删除任务")
	public Result<Boolean> delete(@RequestParam String taskId) {
		return Result.success(taskService.deleteTask(taskId));
	}

}
