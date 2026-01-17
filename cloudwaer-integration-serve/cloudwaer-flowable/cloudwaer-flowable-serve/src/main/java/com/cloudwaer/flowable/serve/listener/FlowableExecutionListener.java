package com.cloudwaer.flowable.serve.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.flowable.serve.entity.WfModel;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * Flowable 执行监听器
 * 用于处理开始事件、结束事件等执行监听
 * 
 * @author cloudwaer
 * @since 2026-01-16
 */
@Slf4j
public class FlowableExecutionListener implements ExecutionListener {

    private WfModelMapper wfModelMapper;
    private RepositoryService repositoryService;

    // Setter方法用于依赖注入
    public void setWfModelMapper(WfModelMapper wfModelMapper) {
        this.wfModelMapper = wfModelMapper;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public void notify(DelegateExecution execution) {
        try {
            String eventName = execution.getEventName();
            String processDefinitionId = execution.getProcessDefinitionId();
            String processInstanceId = execution.getProcessInstanceId();
            
            log.info("执行监听器触发: 事件={}, 流程定义ID={}, 流程实例ID={}", 
                    eventName, processDefinitionId, processInstanceId);

            // 获取流程定义信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();
            
            if (processDefinition == null) {
                log.warn("未找到流程定义: {}", processDefinitionId);
                return;
            }

            String processDefinitionKey = processDefinition.getKey();
            log.info("流程定义Key: {}", processDefinitionKey);

            // 处理不同的事件类型
            switch (eventName) {
                case "start":
                    handleProcessStart(execution, processDefinitionKey);
                    break;
                case "end":
                    handleProcessEnd(execution, processDefinitionKey);
                    break;
                default:
                    log.info("未处理的执行事件: {}", eventName);
                    break;
            }

        } catch (Exception e) {
            log.error("执行监听器处理异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理流程开始事件
     */
    private void handleProcessStart(DelegateExecution execution, String processDefinitionKey) {
        log.info("=== 流程开始事件处理 ===");
        log.info("流程实例ID: {}", execution.getId());
        log.info("流程定义Key: {}", processDefinitionKey);
        log.info("业务Key: {}", execution.getProcessInstanceBusinessKey());
        
        try {
            // 获取流程模型信息
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, processDefinitionKey)
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));
            
            log.info("查询到的流程模型: {}", wfModel);
            
            // 这里可以添加流程开始时的自定义逻辑
            // 例如：初始化流程变量、发送通知、记录日志等
            
            log.info("=== 流程开始事件处理完成 ===");
            
        } catch (Exception e) {
            log.error("处理流程开始事件异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理流程结束事件
     */
    private void handleProcessEnd(DelegateExecution execution, String processDefinitionKey) {
        log.info("=== 流程结束事件处理 ===");
        log.info("流程实例ID: {}", execution.getId());
        log.info("流程定义Key: {}", processDefinitionKey);
        log.info("业务Key: {}", execution.getProcessInstanceBusinessKey());
        
        try {
            // 获取流程模型信息
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, processDefinitionKey)
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));
            
            log.info("查询到的流程模型: {}", wfModel);
            
            // 这里可以添加流程结束时的自定义逻辑
            // 例如：清理数据、发送完成通知、更新统计信息等
            
            log.info("=== 流程结束事件处理完成 ===");
            
        } catch (Exception e) {
            log.error("处理流程结束事件异常: {}", e.getMessage(), e);
        }
    }
}
