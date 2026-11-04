package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.api.dto.WorkflowCcRecordDTO;
import com.cloudwaer.workflow.serve.service.CcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 抄送控制器
 */
@RestController
@RequestMapping("/workflow/cc")
@Tag(name = "抄送")
public class CcController {

	@Autowired
	private CcService ccService;

	@GetMapping("/list")
	@PermissionCode("workflow:task:todo")
	@Operation(summary = "抄送给我的流程")
	public Result<PageResult<WorkflowCcRecordDTO>> list(PageDTO pageDTO) {
		return Result.success(ccService.listMyCc(pageDTO));
	}

	@PostMapping("/read")
	@PermissionCode("workflow:task:todo")
	@Operation(summary = "标记已读")
	public Result<Boolean> markRead(@RequestParam Long id) {
		return Result.success(ccService.markRead(id));
	}

	@PostMapping("/read-all")
	@PermissionCode("workflow:task:todo")
	@Operation(summary = "全部标记已读")
	public Result<Boolean> markAllRead() {
		return Result.success(ccService.markAllRead());
	}

}
