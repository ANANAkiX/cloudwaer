package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.api.dto.FlowableProcessDeleteDTO;
import com.cloudwaer.flowable.api.dto.FlowableProcessInstanceDTO;
import com.cloudwaer.flowable.api.dto.FlowableProcessStartDTO;
import com.cloudwaer.flowable.serve.service.FlowableProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/process")
@Tag(name = "Flowable Process", description = "Flowable process endpoints")
public class FlowableProcessController {

    @Autowired
    private FlowableProcessService processService;

    @PostMapping("/start")
    @Operation(summary = "Start process")
    public Result<String> start(@RequestBody FlowableProcessStartDTO dto) {
        return Result.success(processService.startProcess(dto));
    }

    @GetMapping("/started")
    @Operation(summary = "Started processes")
    public Result<PageResult<FlowableProcessInstanceDTO>> started(PageDTO pageDTO) {
        return Result.success(processService.listStarted(pageDTO));
    }

    @GetMapping("/detail")
    @Operation(summary = "Process detail")
    public Result<FlowableProcessInstanceDTO> detail(@RequestParam String id) {
        return Result.success(processService.getDetail(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete process instance")
    public Result<Boolean> delete(@RequestBody FlowableProcessDeleteDTO dto) {
        return Result.success(processService.deleteProcess(dto.getProcessInstanceId()));
    }

    @PostMapping("/suspend")
    @Operation(summary = "Suspend process instance")
    public Result<Boolean> suspend(@RequestParam String processInstanceId) {
        return Result.success(processService.suspendProcess(processInstanceId));
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate process instance")
    public Result<Boolean> activate(@RequestParam String processInstanceId) {
        return Result.success(processService.activateProcess(processInstanceId));
    }

        @PostMapping("/restart")
    @Operation(summary = "Restart process instance")
    public Result<Boolean> restart(@RequestParam String processInstanceId) {
        return Result.success(processService.restartProcess(processInstanceId));
    }

@PostMapping("/terminate")
    @Operation(summary = "Terminate process instance")
    public Result<Boolean> terminate(@RequestParam String processInstanceId) {
        return Result.success(processService.terminateProcess(processInstanceId));
    }

    @GetMapping("/diagram")
    @Operation(summary = "Get process diagram")
    public Result<String> getDiagram(@RequestParam String processInstanceId) {
        return Result.success(processService.getProcessDiagram(processInstanceId));
    }

    @GetMapping("/variables")
    @Operation(summary = "Get process variables")
    public Result<List<Map<String, Object>>> getVariables(@RequestParam String processInstanceId) {
        return Result.success(processService.getProcessVariables(processInstanceId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get process history")
    public Result<List<Map<String, Object>>> getHistory(@RequestParam String processInstanceId) {
        return Result.success(processService.getProcessHistory(processInstanceId));
    }
}
