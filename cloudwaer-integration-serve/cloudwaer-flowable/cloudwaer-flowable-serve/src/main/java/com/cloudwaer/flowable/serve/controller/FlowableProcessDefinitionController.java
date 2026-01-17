package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.api.dto.FlowableProcessDefinitionDTO;
import com.cloudwaer.flowable.serve.service.FlowableProcessDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/definition")
@Tag(name = "流程定义管理", description = "流程定义相关接口")
public class FlowableProcessDefinitionController {

    @Autowired
    private FlowableProcessDefinitionService processDefinitionService;

    @GetMapping("/list")
    @Operation(summary = "获取流程定义列表")
    public Result<PageResult<FlowableProcessDefinitionDTO>> getProcessDefinitions(
            PageDTO pageDTO,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        PageResult<FlowableProcessDefinitionDTO> result = processDefinitionService.getProcessDefinitions(pageDTO, category, keyword);
        return Result.success(result);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取流程定义详情")
    public Result<FlowableProcessDefinitionDTO> getProcessDefinitionDetail(@RequestParam String id) {
        FlowableProcessDefinitionDTO result = processDefinitionService.getProcessDefinitionDetail(id);
        return Result.success(result);
    }
}
