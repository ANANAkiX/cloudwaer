package com.cloudwaer.flowable.serve.config;

import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.*;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.*;

/**
 * Flowable 全局事件监听器
 *
 * @author cloudwaer
 * @since 2026-01-15
 */
@Slf4j
@Component
public class FlowableGlobalEventListener implements FlowableEventListener {

    @Autowired
    private WfNodeActionService nodeActionService;

    @Override
    public void onEvent(FlowableEvent event) {
        try {
            log.info("Flowable事件触发: {}, 类型: {}", event.getClass().getSimpleName(), event.getType());

            FlowableEventType type = event.getType();
            if (type.equals(TASK_CREATED)) {
                handleTaskCreated((FlowableEngineEntityEvent) event);
            } else if (type.equals(TASK_COMPLETED)) {
                handleTaskCompleted((FlowableEngineEntityEvent) event);
            } else if (type.equals(PROCESS_STARTED)) {
                handleProcessStarted((FlowableEngineEntityEvent) event);
            } else if (type.equals(PROCESS_COMPLETED)) {
                handleProcessCompleted((FlowableEngineEntityEvent) event);
            } else {
                log.debug("未处理的事件类型: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("处理Flowable事件异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理任务创建事件
     */
    private void handleTaskCreated(FlowableEngineEntityEvent event) {
        TaskEntityImpl task = (TaskEntityImpl) event.getEntity();
        log.info("任务创建: 任务ID={}, 任务名称={}, 流程实例ID={}",
                task.getId(), task.getName(), task.getProcessInstanceId());
        // 执行节点动作
        nodeActionService.executeNodeActions(task.getProcessDefinitionId(),
                task.getTaskDefinitionKey(),
                "create",
                task.getProcessInstanceId());
    }

    /**
     * 处理任务完成事件
     */
    private void handleTaskCompleted(FlowableEngineEntityEvent event) {
        TaskEntityImpl task = (TaskEntityImpl) event.getEntity();
        log.info("任务完成: 任务ID={}, 任务名称={}, 流程实例ID={}",
                task.getId(), task.getName(), task.getProcessInstanceId());

        // 执行节点动作
        nodeActionService.executeNodeActions(task.getProcessDefinitionId(),
                task.getTaskDefinitionKey(),
                "complete",
                task.getProcessInstanceId());
    }

    /**
     * 处理流程开始事件
     */
    private void handleProcessStarted(FlowableEngineEntityEvent event) {
        log.info("流程开始: 流程实例ID={}", event.getProcessInstanceId());
    }

    /**
     * 处理流程完成事件
     */
    private void handleProcessCompleted(FlowableEngineEntityEvent event) {
        log.info("流程完成: 流程实例ID={}", event.getProcessInstanceId());
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

    @Override
    public Collection<? extends FlowableEventType> getTypes() {
        return Arrays.asList(
                TASK_CREATED,
                TASK_COMPLETED,
                PROCESS_STARTED,
                FlowableEngineEventType.PROCESS_COMPLETED
        );
    }
}
