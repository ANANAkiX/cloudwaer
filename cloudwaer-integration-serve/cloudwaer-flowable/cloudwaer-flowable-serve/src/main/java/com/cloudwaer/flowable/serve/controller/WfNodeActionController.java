package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 节点动作控制器
 *
 * @author cloudwaer
 * @since 2026-01-15
 */
@RestController
@RequestMapping("/node-action")
@Tag(name = "Node Action", description = "节点动作管理接口")
public class WfNodeActionController {

	@Autowired
	private WfNodeActionService nodeActionService;

	@PostMapping("/save")
	@Operation(summary = "保存节点动作配置")
	public Result<Boolean> save(@RequestBody WfNodeAction nodeAction) {
		boolean success = nodeActionService.saveNodeAction(nodeAction);
		return Result.success(success);
	}

	@PostMapping("/batch-save")
	@Operation(summary = "批量保存节点动作配置")
	public Result<Boolean> batchSave(@RequestBody List<WfNodeAction> nodeActions) {
		boolean success = nodeActionService.batchSaveNodeActions(nodeActions);
		return Result.success(success);
	}

	@DeleteMapping("/delete/{id}")
	@Operation(summary = "删除节点动作配置")
	public Result<Boolean> delete(@PathVariable Long id) {
		boolean success = nodeActionService.deleteNodeAction(id);
		return Result.success(success);
	}

	@GetMapping("/list")
	@Operation(summary = "查询节点动作列表")
	public Result<List<WfNodeAction>> list(@RequestParam String modelKey, @RequestParam Integer modelVersion,
			@RequestParam(required = false) String nodeId, @RequestParam String eventType) {
		List<WfNodeAction> actions = nodeActionService.listActions(modelKey, modelVersion, nodeId, eventType);
		return Result.success(actions);
	}

	@GetMapping("/model/{modelId}")
	@Operation(summary = "根据模型ID查询动作配置")
	public Result<List<WfNodeAction>> listByModelId(@PathVariable Long modelId) {
		List<WfNodeAction> actions = nodeActionService.listActionsByModelId(modelId);
		return Result.success(actions);
	}

	@PutMapping("/status/{id}")
	@Operation(summary = "更新节点动作状态")
	public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer enabled) {
		boolean success = nodeActionService.updateNodeActionStatus(id, enabled);
		return Result.success(success);
	}

	@GetMapping("/config")
	@Operation(summary = "获取节点动作配置")
	public Result<String> getConfig(@RequestParam String processDefinitionId, @RequestParam String nodeId) {
		String config = nodeActionService.getNodeActionConfig(processDefinitionId, nodeId);
		return Result.success(config);
	}

}
