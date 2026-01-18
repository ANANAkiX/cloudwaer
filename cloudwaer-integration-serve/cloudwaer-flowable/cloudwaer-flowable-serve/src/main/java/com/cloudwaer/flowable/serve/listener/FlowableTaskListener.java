package com.cloudwaer.flowable.serve.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.flowable.api.enums.ProcessStatusEnum;
import com.cloudwaer.flowable.serve.entity.WfModel;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.entity.WfProcessExt;
import com.cloudwaer.flowable.serve.entity.WfTaskHandleRecord;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.mapper.WfProcessExtMapper;
import com.cloudwaer.flowable.serve.mapper.WfTaskHandleRecordMapper;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Arrays;
import java.util.List;

/**
 * Flowable 任务监听器
 *
 * @author cloudwaer
 * @since 2026-01-15
 */
@Slf4j
public class FlowableTaskListener implements TaskListener {

    private WfNodeActionService nodeActionService;
    private ObjectMapper objectMapper;
    private WfNodeActionMapper wfNodeActionMapper;
    private WfModelMapper wfModelMapper;
    private RepositoryService repositoryService;
    private WfTaskHandleRecordMapper taskHandleRecordMapper;
    private WfProcessExtMapper processExtMapper;

    // Setter方法用于依赖注入
    public void setNodeActionService(WfNodeActionService nodeActionService) {
        this.nodeActionService = nodeActionService;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setWfNodeActionMapper(WfNodeActionMapper wfNodeActionMapper) {
        this.wfNodeActionMapper = wfNodeActionMapper;
    }

    public void setWfModelMapper(WfModelMapper wfModelMapper) {
        this.wfModelMapper = wfModelMapper;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setTaskHandleRecordMapper(WfTaskHandleRecordMapper taskHandleRecordMapper) {
        this.taskHandleRecordMapper = taskHandleRecordMapper;
    }

    public void setProcessExtMapper(WfProcessExtMapper processExtMapper) {
        this.processExtMapper = processExtMapper;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            String eventName = delegateTask.getEventName();
            String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
            String processInstanceId = delegateTask.getProcessInstanceId();
            String processDefinitionId = delegateTask.getProcessDefinitionId();

            log.info("任务监听器触发: 事件={}, 任务Key={}, 流程实例ID={}",
                    eventName, taskDefinitionKey, processInstanceId);

            // 处理不同的事件类型
            switch (eventName) {
                case "create":
                    handleTaskCreate(delegateTask);
                    break;
                case "complete":
                    handleTaskComplete(delegateTask);
                    break;
                case "assignment":
                    handleTaskAssignment(delegateTask);
                    break;
                case "delete":
                    handleTaskDelete(delegateTask);
                    break;
                default:
                    log.debug("未处理的任务事件: {}", eventName);
            }

            // 执行节点动作
            nodeActionService.executeNodeActions(processDefinitionId, taskDefinitionKey,
                    eventName, processInstanceId);

        } catch (Exception e) {
            log.error("任务监听器处理异常: {}", e.getMessage(), e);
            throw new RuntimeException("任务监听器处理异常", e);
        }
    }

    /**
     * 处理任务创建事件
     */
    private void handleTaskCreate(DelegateTask delegateTask) {
        log.info("=== 任务创建监听器被调用 ===");
        log.info("任务创建: 任务ID={}, 任务名称={}, 处理人={}",
                delegateTask.getId(), delegateTask.getName(), delegateTask.getAssignee());
        log.info("任务定义Key={}, 流程定义ID={}",
                delegateTask.getTaskDefinitionKey(), delegateTask.getProcessDefinitionId());
        String processInstanceId = delegateTask.getProcessInstanceId();
        // 根据当前运行的节点动态查询处理人配置
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getProcessDefinitionId();

        // 通过RepositoryService获取流程定义Key
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        String processDefinitionKey = processDefinition != null ? processDefinition.getKey() : null;

        try {
            log.info("查询流程模型信息: processKey={}", processDefinitionKey);
            // 获取流程模型信息
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, processDefinitionKey)
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));

            log.info("查询到的流程模型: {}", wfModel);

            if (wfModel != null) {
                log.info("查询节点配置: taskKey={}, modelVersion={}", taskDefinitionKey, wfModel.getVersion());
                // 查询当前节点的处理人配置
                WfNodeAction nodeAction = wfNodeActionMapper.selectOne(
                        new LambdaQueryWrapper<WfNodeAction>()
                                .eq(WfNodeAction::getModelKey, processDefinitionKey)
                                .eq(WfNodeAction::getModelVersion, wfModel.getVersion())
                                .eq(WfNodeAction::getNodeId, taskDefinitionKey)
                                .in(WfNodeAction::getActionType, Arrays.asList("assign", "user_task", "task"))
                                .eq(WfNodeAction::getEnabled, 1)
                                .last("LIMIT 1")
                );

                log.info("查询到的节点配置: {}", nodeAction);

                if (nodeAction != null && nodeAction.getActionConfig() != null) {
                    applyAssignmentFromConfig(delegateTask, nodeAction.getActionConfig(), taskDefinitionKey);
                } else {
                    log.warn("未找到节点配置的处理人: 任务Key={}, 流程Key={}",
                            taskDefinitionKey, processDefinitionKey);
                }
            } else {
                log.warn("未找到流程模型: 流程Key={}", processDefinitionKey);
            }
        } catch (Exception e) {
            log.error("查询节点配置异常: {}", e.getMessage(), e);
        }

        // 保存任务扩展信息
        if (taskHandleRecordMapper != null) {
            boolean rejected = false;
            if (processExtMapper != null) {
                WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                        .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
                rejected = ext != null && ProcessStatusEnum.REJECTED.getCode().equals(ext.getStatus());
            }
            if (!rejected) {
                WfTaskHandleRecord record = new WfTaskHandleRecord();
                record.setProcessInstanceId(processInstanceId);
                record.setTaskId(delegateTask.getId());
                record.setTaskDefinitionKey(delegateTask.getTaskDefinitionKey());
                record.setTaskName(delegateTask.getName());
                record.setAssignee(delegateTask.getAssignee());
                record.setRecordType("wait");
                String assignee = delegateTask.getAssignee();
                record.setAction("等待 " + (assignee == null || assignee.isBlank() ? "未知" : assignee) + " 处理");
                taskHandleRecordMapper.insert(record);
            }
        }

        saveTaskExtension(delegateTask, "create");
        log.info("=== 任务创建监听器处理完成 ===");
    }

    /**
     * 处理任务完成事件
     */
    private void handleTaskComplete(DelegateTask delegateTask) {
        log.info("任务完成: 任务ID={}, 任务名称={}, 处理人={}",
                delegateTask.getId(), delegateTask.getName(), delegateTask.getAssignee());

        // 保存任务扩展信息
        saveTaskExtension(delegateTask, "complete");
    }

    /**
     * 处理任务分配事件
     */
    private void handleTaskAssignment(DelegateTask delegateTask) {
        log.info("任务分配: 任务ID={}, 任务名称={}, 处理人={}",
                delegateTask.getId(), delegateTask.getName(), delegateTask.getAssignee());

        // 保存任务扩展信息
        saveTaskExtension(delegateTask, "assignment");
    }

    /**
     * 处理任务删除事件
     */
    private void handleTaskDelete(DelegateTask delegateTask) {
        log.info("任务删除: 任务ID={}, 任务名称={}",
                delegateTask.getId(), delegateTask.getName());

        // 保存任务扩展信息
        saveTaskExtension(delegateTask, "delete");
    }

    /**
     * 保存任务扩展信息
     */
    private void saveTaskExtension(DelegateTask delegateTask, String event) {
        try {
            // 这里可以保存任务扩展信息到数据库
            // 例如：任务变量、处理时间、处理人等

            // 获取任务变量
            Object actionConfig = delegateTask.getVariable("actionConfig");
            if (actionConfig != null) {
                log.info("任务动作配置: {}", objectMapper.writeValueAsString(actionConfig));
            }

        } catch (Exception e) {
            log.error("保存任务扩展信息异常: {}", e.getMessage(), e);
        }
    }

    private void applyAssignmentFromConfig(DelegateTask delegateTask, String actionConfig, String taskDefinitionKey) {
        String assignee = null;
        try {
            JsonNode root = objectMapper.readTree(actionConfig);
            if (root != null && root.isObject()) {
                assignee = getText(root, "assignee");
                String candidateUsers = getText(root, "candidateUsers");
                String candidateGroups = getText(root, "candidateGroups");
                if (assignee != null && !assignee.isBlank()) {
                    delegateTask.setAssignee(assignee);
                }
                addCandidates(delegateTask, candidateUsers, candidateGroups);
            } else {
                assignee = root != null ? root.asText() : null;
                if (assignee != null && !assignee.isBlank()) {
                    delegateTask.setAssignee(assignee);
                }
            }
        } catch (Exception e) {
            assignee = actionConfig;
            if (assignee != null && !assignee.isBlank()) {
                delegateTask.setAssignee(assignee);
            }
        }

        log.info("根据节点配置动态分配处理人: 任务Key={}, 处理人={}",
                taskDefinitionKey, assignee);
    }

    private String getText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text != null && !text.isBlank() ? text : null;
    }

    private void addCandidates(DelegateTask delegateTask, String candidateUsers, String candidateGroups) {
        List<String> userList = splitCandidates(candidateUsers);
        if (!userList.isEmpty()) {
            delegateTask.addCandidateUsers(userList);
        }
        List<String> groupList = splitCandidates(candidateGroups);
        if (!groupList.isEmpty()) {
            delegateTask.addCandidateGroups(groupList);
        }
    }

    private List<String> splitCandidates(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("[,;]"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
