package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.service.BpmnDeployService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BPMN部署控制器
 * 
 * @author cloudwaer
 * @since 2026-01-15
 */
@RestController
@RequestMapping("/bpmn-deploy")
@Tag(name = "BPMN Deploy", description = "BPMN部署管理接口")
public class BpmnDeployController {

    @Autowired
    private BpmnDeployService bpmnDeployService;

    @PostMapping("/deploy")
    @Operation(summary = "部署BPMN流程")
    public Result<Deployment> deploy(@RequestBody BpmnDeployRequest request) {
        Deployment deployment = bpmnDeployService.deployBpmn(
                request.getBpmnXml(), 
                request.getDeploymentName(), 
                request.getCategory()
        );
        return Result.success(deployment);
    }

    @PostMapping("/deploy-with-actions")
    @Operation(summary = "部署带节点配置的BPMN流程")
    public Result<Deployment> deployWithActions(@RequestBody BpmnDeployWithActionsRequest request) {
        Deployment deployment = bpmnDeployService.deployBpmnWithActions(
                request.getBpmnXml(), 
                request.getDeploymentName(), 
                request.getCategory(),
                request.getNodeActions()
        );
        return Result.success(deployment);
    }

    @PostMapping("/update")
    @Operation(summary = "更新BPMN流程")
    public Result<Deployment> update(@RequestBody BpmnUpdateRequest request) {
        Deployment deployment = bpmnDeployService.updateBpmn(
                request.getBpmnXml(), 
                request.getDeploymentName(), 
                request.getCategory(),
                request.getProcessKey()
        );
        return Result.success(deployment);
    }

    @DeleteMapping("/delete/{deploymentId}")
    @Operation(summary = "删除部署")
    public Result<Boolean> delete(@PathVariable String deploymentId) {
        boolean success = bpmnDeployService.deleteDeployment(deploymentId);
        return Result.success(success);
    }

    @GetMapping("/detail/{deploymentId}")
    @Operation(summary = "获取部署详情")
    public Result<Deployment> getDetail(@PathVariable String deploymentId) {
        Deployment deployment = bpmnDeployService.getDeployment(deploymentId);
        return Result.success(deployment);
    }

    @PostMapping("/extract-nodes")
    @Operation(summary = "提取BPMN节点信息")
    public Result<List<String>> extractNodes(@RequestBody BpmnExtractRequest request) {
        List<String> nodeIds = bpmnDeployService.extractNodeIds(request.getBpmnXml());
        return Result.success(nodeIds);
    }

    @PostMapping("/extract-user-tasks")
    @Operation(summary = "提取用户任务节点")
    public Result<List<String>> extractUserTasks(@RequestBody BpmnExtractRequest request) {
        List<String> userTaskIds = bpmnDeployService.extractUserTaskIds(request.getBpmnXml());
        return Result.success(userTaskIds);
    }

    @PostMapping("/validate")
    @Operation(summary = "验证BPMN XML格式")
    public Result<Boolean> validate(@RequestBody BpmnValidateRequest request) {
        boolean valid = bpmnDeployService.validateBpmnXml(request.getBpmnXml());
        return Result.success(valid);
    }

    @GetMapping("/diagram/{processDefinitionId}")
    @Operation(summary = "生成流程图")
    public Result<String> generateDiagram(@PathVariable String processDefinitionId) {
        String diagram = bpmnDeployService.generateProcessDiagram(processDefinitionId);
        return Result.success(diagram);
    }

    // 请求DTO类
    public static class BpmnDeployRequest {
        private String bpmnXml;
        private String deploymentName;
        private String category;

        // getters and setters
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
        public String getDeploymentName() { return deploymentName; }
        public void setDeploymentName(String deploymentName) { this.deploymentName = deploymentName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class BpmnDeployWithActionsRequest {
        private String bpmnXml;
        private String deploymentName;
        private String category;
        private List<WfNodeAction> nodeActions;

        // getters and setters
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
        public String getDeploymentName() { return deploymentName; }
        public void setDeploymentName(String deploymentName) { this.deploymentName = deploymentName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<WfNodeAction> getNodeActions() { return nodeActions; }
        public void setNodeActions(List<WfNodeAction> nodeActions) { this.nodeActions = nodeActions; }
    }

    public static class BpmnUpdateRequest {
        private String bpmnXml;
        private String deploymentName;
        private String category;
        private String processKey;

        // getters and setters
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
        public String getDeploymentName() { return deploymentName; }
        public void setDeploymentName(String deploymentName) { this.deploymentName = deploymentName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getProcessKey() { return processKey; }
        public void setProcessKey(String processKey) { this.processKey = processKey; }
    }

    public static class BpmnExtractRequest {
        private String bpmnXml;

        // getters and setters
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
    }

    public static class BpmnValidateRequest {
        private String bpmnXml;

        // getters and setters
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
    }
}
