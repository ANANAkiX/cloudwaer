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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@PostMapping("/rollback")
	@PermissionCode("workflow:def:save")
	@Operation(summary = "恢复历史版本（复制为新草稿版本，历史不变）")
	public Result<Long> rollback(@RequestParam Long id) {
		return Result.success(defService.rollback(id));
	}

	@PostMapping("/batch-offline")
	@PermissionCode("workflow:def:publish")
	@Operation(summary = "批量下线（仅已发布，其余跳过）")
	public Result<Map<String, Integer>> batchOffline(@RequestBody List<Long> ids) {
		int success = 0;
		int failed = 0;
		int skipped = 0;
		for (Long id : ids) {
			try {
				WorkflowDefDTO def = defService.getDetail(id);
				if (def == null || def.getDefStatus() == null || def.getDefStatus() != 1) {
					skipped++;
					continue;
				}
				defService.offline(id);
				success++;
			}
			catch (Exception e) {
				failed++;
			}
		}
		return Result.success(counts(success, failed, skipped));
	}

	@PostMapping("/batch-delete")
	@PermissionCode("workflow:def:delete")
	@Operation(summary = "批量删除（已发布跳过）")
	public Result<Map<String, Integer>> batchDelete(@RequestBody List<Long> ids) {
		int success = 0;
		int failed = 0;
		int skipped = 0;
		for (Long id : ids) {
			try {
				WorkflowDefDTO def = defService.getDetail(id);
				if (def != null && def.getDefStatus() != null && def.getDefStatus() == 1) {
					skipped++;
					continue;
				}
				defService.deleteDef(id);
				success++;
			}
			catch (Exception e) {
				failed++;
			}
		}
		return Result.success(counts(success, failed, skipped));
	}

	private Map<String, Integer> counts(int success, int failed, int skipped) {
		Map<String, Integer> r = new HashMap<>();
		r.put("success", success);
		r.put("failed", failed);
		r.put("skipped", skipped);
		return r;
	}

}
