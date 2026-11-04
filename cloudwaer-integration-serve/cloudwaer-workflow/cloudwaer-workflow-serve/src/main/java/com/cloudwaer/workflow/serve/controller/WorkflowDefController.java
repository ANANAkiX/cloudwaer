package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.api.dto.WorkflowDefDTO;
import com.cloudwaer.workflow.serve.service.WorkflowDefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程定义控制器
 */
@RestController
@RequestMapping("/workflow/definition")
@Tag(name = "流程定义")
public class WorkflowDefController {

	@Autowired
	private WorkflowDefService defService;

	@GetMapping("/list")
	@PermissionCode("workflow:def:list")
	@Operation(summary = "流程定义列表")
	public Result<PageResult<WorkflowDefDTO>> list(PageDTO pageDTO, @RequestParam(required = false) String category,
			@RequestParam(required = false) String keyword) {
		return Result.success(defService.list(pageDTO, category, keyword));
	}

	@GetMapping("/detail")
	@PermissionCode("workflow:def:detail")
	@Operation(summary = "流程定义详情")
	public Result<WorkflowDefDTO> detail(@RequestParam Long id) {
		return Result.success(defService.getDetail(id));
	}

	@GetMapping("/versions")
	@PermissionCode("workflow:def:list")
	@Operation(summary = "流程定义版本列表")
	public Result<List<WorkflowDefDTO>> versions(@RequestParam String defKey) {
		return Result.success(defService.listVersions(defKey));
	}

	@PostMapping("/save")
	@PermissionCode("workflow:def:save")
	@Operation(summary = "保存流程定义")
	public Result<Long> save(@Valid @RequestBody WorkflowDefDTO dto) {
		return Result.success(defService.saveDef(dto));
	}

	@PostMapping("/publish")
	@PermissionCode("workflow:def:publish")
	@Operation(summary = "发布流程定义")
	public Result<Boolean> publish(@RequestParam Long id) {
		return Result.success(defService.publish(id));
	}

	@PostMapping("/offline")
	@PermissionCode("workflow:def:publish")
	@Operation(summary = "下线流程定义")
	public Result<Boolean> offline(@RequestParam Long id) {
		return Result.success(defService.offline(id));
	}

	@DeleteMapping("/delete")
	@PermissionCode("workflow:def:delete")
	@Operation(summary = "删除流程定义")
	public Result<Boolean> delete(@RequestParam Long id) {
		return Result.success(defService.deleteDef(id));
	}

}
