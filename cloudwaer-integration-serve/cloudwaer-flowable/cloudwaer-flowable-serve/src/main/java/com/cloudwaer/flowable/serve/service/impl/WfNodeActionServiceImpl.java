package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 节点动作服务实现类
 * 
 * @author cloudwaer
 * @since 2026-01-15
 */
@Slf4j
@Service
public class WfNodeActionServiceImpl implements WfNodeActionService {

    @Autowired
    private WfNodeActionMapper nodeActionMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Override
    public List<WfNodeAction> listActions(String modelKey, Integer modelVersion, String nodeId, String eventType) {
        LambdaQueryWrapper<WfNodeAction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfNodeAction::getModelKey, modelKey)
                .eq(WfNodeAction::getModelVersion, modelVersion)
                .eq(WfNodeAction::getEnabled, 1);
        List<String> eventTypes = resolveEventTypes(eventType);
        if (eventTypes.size() == 1) {
            wrapper.eq(WfNodeAction::getEventType, eventTypes.get(0));
        } else {
            wrapper.in(WfNodeAction::getEventType, eventTypes);
        }
        if (nodeId == null || nodeId.isBlank()) {
            wrapper.and(w -> w.isNull(WfNodeAction::getNodeId).or().eq(WfNodeAction::getNodeId, ""));
        } else {
            wrapper.eq(WfNodeAction::getNodeId, nodeId);
        }
        return nodeActionMapper.selectList(wrapper);
    }

    @Override
    public boolean saveNodeAction(WfNodeAction nodeAction) {
        try {
            if (nodeAction.getId() == null) {
                return nodeActionMapper.insert(nodeAction) > 0;
            } else {
                return nodeActionMapper.updateById(nodeAction) > 0;
            }
        } catch (Exception e) {
            log.error("保存节点动作配置异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteNodeAction(Long id) {
        try {
            return nodeActionMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("删除节点动作配置异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void executeNodeActions(String processDefinitionId, String nodeId, String eventType, String processInstanceId) {
        try {
            // 获取流程定义信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            if (processDefinition == null) {
                log.warn("未找到流程定义: {}", processDefinitionId);
                return;
            }

            // 查询节点动作配置
            List<WfNodeAction> actions = listActions(
                    processDefinition.getKey(),
                    processDefinition.getVersion(),
                    nodeId,
                    eventType
            );

            if (actions.isEmpty()) {
                log.debug("节点 {} 事件 {} 未配置动作", nodeId, eventType);
                return;
            }

            // 执行动作
            for (WfNodeAction action : actions) {
                executeAction(action, processInstanceId);
            }

        } catch (Exception e) {
            log.error("执行节点动作异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getNodeActionConfig(String processDefinitionId, String nodeId) {
        try {
            // 获取流程定义信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            if (processDefinition == null) {
                return null;
            }

            // 查询节点动作配置
            List<WfNodeAction> actions = listActions(
                    processDefinition.getKey(),
                    processDefinition.getVersion(),
                    nodeId,
                    "create"
            );

            if (!actions.isEmpty()) {
                // 返回第一个动作的配置
                return actions.get(0).getActionConfig();
            }

            return null;
        } catch (Exception e) {
            log.error("获取节点动作配置异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean batchSaveNodeActions(List<WfNodeAction> nodeActions) {
        try {
            for (WfNodeAction action : nodeActions) {
                if (!saveNodeAction(action)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("批量保存节点动作配置异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<WfNodeAction> listActionsByModelId(Long modelId) {
        try {
            LambdaQueryWrapper<WfNodeAction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WfNodeAction::getModelId, modelId)
                    .eq(WfNodeAction::getEnabled, 1);
            return nodeActionMapper.selectList(wrapper);
        } catch (Exception e) {
            log.error("根据模型ID查询动作配置异常: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean updateNodeActionStatus(Long id, Integer enabled) {
        try {
            WfNodeAction nodeAction = new WfNodeAction();
            nodeAction.setId(id);
            nodeAction.setEnabled(enabled);
            return nodeActionMapper.updateById(nodeAction) > 0;
        } catch (Exception e) {
            log.error("更新节点动作状态异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 执行具体动作
     */
    private void executeAction(WfNodeAction action, String processInstanceId) {
        try {
            log.info("执行节点动作: 动作类型={}, 节点ID={}, 流程实例ID={}", 
                    action.getActionType(), action.getNodeId(), processInstanceId);

            // 根据动作类型执行不同的逻辑
            switch (action.getActionType()) {
                case "approval":
                    executeApprovalAction(action, processInstanceId);
                    break;
                case "notification":
                    executeNotificationAction(action, processInstanceId);
                    break;
                case "service":
                    executeServiceAction(action, processInstanceId);
                    break;
                case "script":
                    executeScriptAction(action, processInstanceId);
                    break;
                default:
                    log.warn("未知的动作类型: {}", action.getActionType());
            }

        } catch (Exception e) {
            log.error("执行节点动作异常: {}", e.getMessage(), e);
        }
    }

    private List<String> resolveEventTypes(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return List.of();
        }
        switch (eventType) {
            case "task_create":
                return List.of("task_create", "create");
            case "create":
                return List.of("create", "task_create");
            case "task_complete":
                return List.of("task_complete", "complete");
            case "complete":
                return List.of("complete", "task_complete");
            case "process_start":
                return List.of("process_start", "start");
            case "start":
                return List.of("start", "process_start");
            case "process_end":
                return List.of("process_end", "end");
            case "end":
                return List.of("end", "process_end");
            default:
                return List.of(eventType);
        }
    }

    /**
     * 执行审批动作
     */
    private void executeApprovalAction(WfNodeAction action, String processInstanceId) {
        log.info("执行审批动作: 配置={}", action.getActionConfig());
        
        // 设置流程变量
        runtimeService.setVariable(processInstanceId, "approvalResult", "approved");
        runtimeService.setVariable(processInstanceId, "approvalTime", System.currentTimeMillis());
    }

    /**
     * 执行通知动作
     */
    private void executeNotificationAction(WfNodeAction action, String processInstanceId) {
        log.info("执行通知动作: 配置={}", action.getActionConfig());
        
        // 这里可以集成具体的通知实现
        runtimeService.setVariable(processInstanceId, "notificationSent", true);
        runtimeService.setVariable(processInstanceId, "notificationTime", System.currentTimeMillis());
    }

    /**
     * 执行服务调用动作
     */
    private void executeServiceAction(WfNodeAction action, String processInstanceId) {
        log.info("执行服务调用动作: 配置={}", action.getActionConfig());
        
        // 这里可以调用其他服务
        runtimeService.setVariable(processInstanceId, "serviceCallResult", "success");
        runtimeService.setVariable(processInstanceId, "serviceCallTime", System.currentTimeMillis());
    }

    /**
     * 执行脚本动作
     */
    private void executeScriptAction(WfNodeAction action, String processInstanceId) {
        log.info("执行脚本动作: 配置={}", action.getActionConfig());
        
        // 这里可以执行脚本
        runtimeService.setVariable(processInstanceId, "scriptExecuted", true);
        runtimeService.setVariable(processInstanceId, "scriptExecutedTime", System.currentTimeMillis());
    }
}
