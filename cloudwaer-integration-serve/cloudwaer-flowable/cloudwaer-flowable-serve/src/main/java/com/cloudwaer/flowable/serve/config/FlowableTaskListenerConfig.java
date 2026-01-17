package com.cloudwaer.flowable.serve.config;

import com.cloudwaer.flowable.serve.listener.FlowableExecutionListener;
import com.cloudwaer.flowable.serve.listener.FlowableTaskListener;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.engine.RepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 监听器配置
 * 
 * @author cloudwaer
 * @since 2026-01-16
 */
@Configuration
public class FlowableTaskListenerConfig {

    /**
     * 注册任务监听器Bean
     * 确保Spring能够正确识别和注入任务监听器
     */
    @Bean("flowableTaskListener")
    public FlowableTaskListener flowableTaskListener(
            WfNodeActionService nodeActionService,
            ObjectMapper objectMapper,
            WfNodeActionMapper wfNodeActionMapper,
            WfModelMapper wfModelMapper,
            RepositoryService repositoryService) {
        
        FlowableTaskListener listener = new FlowableTaskListener();
        
        // 手动注入依赖
        listener.setNodeActionService(nodeActionService);
        listener.setObjectMapper(objectMapper);
        listener.setWfNodeActionMapper(wfNodeActionMapper);
        listener.setWfModelMapper(wfModelMapper);
        listener.setRepositoryService(repositoryService);
        
        return listener;
    }
    
    /**
     * 注册执行监听器Bean
     * 用于处理流程开始、结束等事件
     */
    @Bean("flowableExecutionListener")
    public FlowableExecutionListener flowableExecutionListener(
            WfModelMapper wfModelMapper,
            RepositoryService repositoryService) {
        
        FlowableExecutionListener listener = new FlowableExecutionListener();
        
        // 手动注入依赖
        listener.setWfModelMapper(wfModelMapper);
        listener.setRepositoryService(repositoryService);
        
        return listener;
    }
}
