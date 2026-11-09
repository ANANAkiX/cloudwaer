package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.api.dto.ProcessStartDTO;
import com.cloudwaer.workflow.api.dto.WorkflowInstanceDTO;
import com.cloudwaer.workflow.serve.service.WorkflowInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cloudwaer.workflow.api.dto.WorkflowHistoryDTO;

/**
 * 流程实例控制器
 */
@RestController
@RequestMapping("/workflow/instance")
@Tag(name = "流程实例")
public class WorkflowInstanceController {

	@Autowired
	private WorkflowInstanceService instanceService;

	@Autowired
	private com.cloudwaer.workflow.serve.service.InstanceNodeFormService instanceNodeFormService;

	@PostMapping("/start")
	@PermissionCode("workflow:instance:start")
	@Operation(summary = "启动流程")
	public Result<String> start(@Valid @RequestBody ProcessStartDTO dto) {
		return Result.success(instanceService.startProcess(dto));
	}

	@GetMapping("/started")
	@PermissionCode("workflow:instance:started")
	@Operation(summary = "我发起的流程")
	public Result<PageResult<WorkflowInstanceDTO>> started(PageDTO pageDTO) {
		return Result.success(instanceService.listStarted(pageDTO));
	}

	@GetMapping("/list")
	@PermissionCode("workflow:instance:list")
	@Operation(summary = "流程实例列表")
	public Result<PageResult<WorkflowInstanceDTO>> list(PageDTO pageDTO, @RequestParam(required = false) String status,
			@RequestParam(required = false) String keyword) {
		return Result.success(instanceService.listAll(pageDTO, status, keyword));
	}

	@GetMapping("/detail")
	@PermissionCode("workflow:instance:detail")
	@Operation(summary = "流程实例详情")
	public Result<WorkflowInstanceDTO> detail(@RequestParam String instanceId) {
		return Result.success(instanceService.getDetail(instanceId));
	}

	@PostMapping("/suspend")
	@PermissionCode("workflow:instance:operate")
	@Operation(summary = "挂起流程")
	public Result<Boolean> suspend(@RequestParam String instanceId) {
		return Result.success(instanceService.suspend(instanceId));
	}

	@PostMapping("/activate")
	@PermissionCode("workflow:instance:operate")
	@Operation(summary = "激活流程")
	public Result<Boolean> activate(@RequestParam String instanceId) {
		return Result.success(instanceService.activate(instanceId));
	}

	@PostMapping("/terminate")
	@PermissionCode("workflow:instance:operate")
	@Operation(summary = "终止流程")
	public Result<Boolean> terminate(@RequestParam String instanceId) {
		return Result.success(instanceService.terminate(instanceId));
	}

	@PostMapping("/restart")
	@PermissionCode("workflow:instance:operate")
	@Operation(summary = "重新发起")
	public Result<Boolean> restart(@RequestParam String instanceId) {
		return Result.success(instanceService.restart(instanceId));
	}

	@DeleteMapping("/delete")
	@PermissionCode("workflow:instance:delete")
	@Operation(summary = "删除流程实例")
	public Result<Boolean> delete(@RequestParam String instanceId) {
		return Result.success(instanceService.deleteInstance(instanceId));
	}

	@GetMapping("/history")
	@PermissionCode("workflow:instance:detail")
	@Operation(summary = "流程历史时间线")
	public Result<java.util.List<WorkflowHistoryDTO>> history(@RequestParam String instanceId) {
		return Result.success(instanceService.getHistory(instanceId));
	}

	@GetMapping("/node-form")
	@PermissionCode("workflow:instance:detail")
	@Operation(summary = "查询节点表单数据")
	public Result<java.util.Map<String, Object>> nodeForm(@RequestParam String instanceId,
			@RequestParam String nodeId) {
		var record = instanceNodeFormService.getByInstanceAndNode(instanceId, nodeId);
		java.util.Map<String, Object> data = new java.util.HashMap<>();
		if (record != null) {
			data.put("formId", record.getFormId());
			data.put("formSchema", record.getFormSchema());
			data.put("formData", record.getFormData());
		}
		return Result.success(data);
	}

}
