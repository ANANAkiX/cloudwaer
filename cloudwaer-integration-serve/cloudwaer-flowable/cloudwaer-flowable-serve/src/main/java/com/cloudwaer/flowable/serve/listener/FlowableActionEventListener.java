package com.cloudwaer.flowable.serve.listener;

import com.cloudwaer.flowable.serve.action.ActionContext;
import com.cloudwaer.flowable.serve.action.ActionDispatcher;
import com.cloudwaer.flowable.serve.constant.FlowableConstants;
import com.cloudwaer.flowable.serve.entity.WfDeployment;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.mapper.WfDeploymentMapper;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEvent;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FlowableActionEventListener implements FlowableEventListener {

    @Autowired
    private WfNodeActionService nodeActionService;

    @Autowired
    private ActionDispatcher actionDispatcher;

    @Autowired
    private WfDeploymentMapper deploymentMapper;

    @Override
    public void onEvent(FlowableEvent event) {
        if (event.getType() == FlowableEngineEventType.TASK_CREATED) {
            handleTaskEvent(event, FlowableConstants.EVENT_TASK_CREATE);
        } else if (event.getType() == FlowableEngineEventType.TASK_COMPLETED) {
            handleTaskEvent(event, FlowableConstants.EVENT_TASK_COMPLETE);
        } else if (event.getType() == FlowableEngineEventType.PROCESS_STARTED) {
            handleProcessEvent(event, FlowableConstants.EVENT_PROCESS_START);
        } else if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED) {
            handleProcessEvent(event, FlowableConstants.EVENT_PROCESS_END);
        }
    }

    private void handleTaskEvent(FlowableEvent event, String eventType) {
        if (!(event instanceof FlowableEntityEvent)) {
            return;
        }
        Object entity = ((FlowableEntityEvent) event).getEntity();
        if (!(entity instanceof Task)) {
            return;
        }
        Task task = (Task) entity;
        String processDefinitionId = task.getProcessDefinitionId();
        String processDefinitionKey = resolveProcessDefinitionKey(processDefinitionId);
        Integer modelVersion = resolveModelVersion(processDefinitionId);
        List<WfNodeAction> actions = nodeActionService.listActions(
                processDefinitionKey, modelVersion, task.getTaskDefinitionKey(), eventType);
        ActionContext context = new ActionContext();
        context.setProcessDefinitionId(processDefinitionId);
        context.setProcessDefinitionKey(processDefinitionKey);
        context.setProcessDefinitionVersion(modelVersion);
        context.setProcessInstanceId(task.getProcessInstanceId());
        context.setTaskId(task.getId());
        context.setTaskDefinitionKey(task.getTaskDefinitionKey());
        context.setEventType(eventType);
        actionDispatcher.dispatch(context, actions);
    }

    private void handleProcessEvent(FlowableEvent event, String eventType) {
        if (!(event instanceof FlowableEngineEvent)) {
            return;
        }
        FlowableEngineEvent processEvent = (FlowableEngineEvent) event;
        String processDefinitionId = processEvent.getProcessDefinitionId();
        String processDefinitionKey = resolveProcessDefinitionKey(processDefinitionId);
        Integer modelVersion = resolveModelVersion(processDefinitionId);
        List<WfNodeAction> actions = nodeActionService.listActions(
                processDefinitionKey, modelVersion, null, eventType);
        ActionContext context = new ActionContext();
        context.setProcessDefinitionId(processDefinitionId);
        context.setProcessDefinitionKey(processDefinitionKey);
        context.setProcessDefinitionVersion(modelVersion);
        context.setProcessInstanceId(processEvent.getProcessInstanceId());
        context.setEventType(eventType);
        actionDispatcher.dispatch(context, actions);
    }

    private Integer resolveModelVersion(String processDefinitionId) {
        WfDeployment deployment = deploymentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WfDeployment>()
                        .eq(WfDeployment::getProcessDefinitionId, processDefinitionId)
                        .eq(WfDeployment::getStatus, 1));
        if (deployment != null) {
            return deployment.getModelVersion();
        }
        return parseDefinitionVersion(processDefinitionId);
    }

    private String resolveProcessDefinitionKey(String processDefinitionId) {
        WfDeployment deployment = deploymentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WfDeployment>()
                        .eq(WfDeployment::getProcessDefinitionId, processDefinitionId)
                        .eq(WfDeployment::getStatus, 1));
        if (deployment != null) {
            return deployment.getProcessDefinitionKey();
        }
        return parseDefinitionKey(processDefinitionId);
    }

    private String parseDefinitionKey(String processDefinitionId) {
        if (processDefinitionId == null) {
            return null;
        }
        String[] parts = processDefinitionId.split(":");
        return parts.length > 0 ? parts[0] : processDefinitionId;
    }

    private Integer parseDefinitionVersion(String processDefinitionId) {
        if (processDefinitionId == null) {
            return null;
        }
        String[] parts = processDefinitionId.split(":");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
