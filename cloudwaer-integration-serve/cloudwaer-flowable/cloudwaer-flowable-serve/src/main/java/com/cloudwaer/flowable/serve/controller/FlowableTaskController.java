package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.api.dto.FlowableTaskClaimDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskCompleteDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskDeleteDTO;
import com.cloudwaer.flowable.serve.service.FlowableTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flowable/task")
@Tag(name = "Flowable Task", description = "Flowable task endpoints")
public class FlowableTaskController {

	@Autowired
	private FlowableTaskService taskService;

	@GetMapping("/todo")
	@Operation(summary = "Todo tasks")
	public Result<PageResult<FlowableTaskDTO>> todo(PageDTO pageDTO) {
		return Result.success(taskService.listTodo(pageDTO));
	}

	@GetMapping("/done")
	@Operation(summary = "Done tasks")
	public Result<PageResult<FlowableTaskDTO>> done(PageDTO pageDTO) {
		return Result.success(taskService.listDone(pageDTO));
	}

	@PostMapping("/claim")
	@Operation(summary = "Claim task")
	public Result<Boolean> claim(@RequestBody FlowableTaskClaimDTO dto) {
		return Result.success(taskService.claim(dto.getTaskId()));
	}

	@PostMapping("/complete")
	@Operation(summary = "Complete task")
	public Result<Boolean> complete(@RequestBody FlowableTaskCompleteDTO dto) {
		return Result.success(taskService.complete(dto));
	}

	@GetMapping("/detail")
	@Operation(summary = "Task detail")
	public Result<FlowableTaskDTO> detail(@RequestParam String id) {
		return Result.success(taskService.getDetail(id));
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Delete task")
	public Result<Boolean> delete(@RequestBody FlowableTaskDeleteDTO dto) {
		return Result.success(taskService.deleteTask(dto.getTaskId()));
	}

}
