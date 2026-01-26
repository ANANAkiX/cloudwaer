package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.api.dto.FlowableModelCopyDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelDetailDTO;
import com.cloudwaer.flowable.api.dto.FlowableIdDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelListDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelPublishDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelRollbackDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelSaveDTO;
import com.cloudwaer.flowable.serve.service.FlowableModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/flowable/model")
@Tag(name = "Flowable Model", description = "Flowable model endpoints")
public class FlowableModelController {

	@Autowired
	private FlowableModelService modelService;

	@PostMapping("/save")
	@Operation(summary = "Save model")
	public Result<Long> save(@RequestBody FlowableModelSaveDTO dto) {
		return Result.success(modelService.saveModel(dto));
	}

	@GetMapping("/detail")
	@Operation(summary = "Model detail")
	public Result<FlowableModelDetailDTO> detail(@RequestParam Long id) {
		return Result.success(modelService.getDetail(id));
	}

	@GetMapping("/list")
	@Operation(summary = "获取模型列表")
	public Result<PageResult<FlowableModelListDTO>> list(PageDTO pageDTO) {
		return Result.success(modelService.list(pageDTO));
	}

	@GetMapping("/versions")
	@Operation(summary = "Model versions")
	public Result<List<FlowableModelListDTO>> versions(@RequestParam String modelKey) {
		return Result.success(modelService.listVersions(modelKey));
	}

	@PostMapping("/publish")
	@Operation(summary = "Publish model")
	public Result<Boolean> publish(@RequestBody FlowableModelPublishDTO dto) {
		return Result.success(modelService.publish(dto.getId()));
	}

	@PostMapping("/copy")
	@Operation(summary = "Copy model")
	public Result<Boolean> copy(@RequestBody FlowableModelCopyDTO dto) {
		return Result.success(modelService.copy(dto));
	}

	@PostMapping("/rollback")
	@Operation(summary = "Rollback model")
	public Result<Boolean> rollback(@RequestBody FlowableModelRollbackDTO dto) {
		return Result.success(modelService.rollback(dto));
	}

	@GetMapping("/bpmn")
	@Operation(summary = "Get BPMN XML")
	public Result<String> bpmn(@RequestParam Long id) {
		return Result.success(modelService.getBpmnXml(id));
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Delete model")
	public Result<Boolean> delete(@RequestBody FlowableIdDTO dto) {
		return Result.success(modelService.deleteModel(dto.getId()));
	}

}
