package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.api.dto.WorkflowFormDTO;
import com.cloudwaer.workflow.serve.service.WorkflowFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 表单控制器
 */
@RestController
@RequestMapping("/workflow/form")
@Tag(name = "流程表单")
public class WorkflowFormController {

	@Autowired
	private WorkflowFormService formService;

	@GetMapping("/list")
	@PermissionCode("workflow:form:list")
	@Operation(summary = "表单列表")
	public Result<PageResult<WorkflowFormDTO>> list(PageDTO pageDTO, @RequestParam(required = false) String category,
			@RequestParam(required = false) String keyword) {
		return Result.success(formService.list(pageDTO, category, keyword));
	}

	@GetMapping("/detail")
	@PermissionCode("workflow:form:detail")
	@Operation(summary = "表单详情")
	public Result<WorkflowFormDTO> detail(@RequestParam Long id) {
		return Result.success(formService.getDetail(id));
	}

	@PostMapping("/save")
	@PermissionCode("workflow:form:save")
	@Operation(summary = "保存表单")
	public Result<Long> save(@RequestBody WorkflowFormDTO dto) {
		return Result.success(formService.saveForm(dto));
	}

	@DeleteMapping("/delete")
	@PermissionCode("workflow:form:delete")
	@Operation(summary = "删除表单")
	public Result<Boolean> delete(@RequestParam Long id) {
		return Result.success(formService.deleteForm(id));
	}

}
