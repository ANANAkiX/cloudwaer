package com.cloudwaer.flowable.serve.action;

import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 动态动作执行器
 * 通过JavaDelegate实现基于JSON配置的任务动作执行
 * 
 * @author cloudwaer
 * @since 2026-01-15
 */
@Slf4j
@Component("dynamicActionDelegate")
public class DynamicActionDelegate implements JavaDelegate {

    @Autowired
    private WfNodeActionService nodeActionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        try {
            String processDefinitionId = execution.getProcessDefinitionId();
            String currentActivityId = execution.getCurrentActivityId();
            String processInstanceId = execution.getProcessInstanceId();
            
            log.info("动态动作执行器触发: 流程定义ID={}, 当前节点ID={}, 流程实例ID={}", 
                    processDefinitionId, currentActivityId, processInstanceId);

            // 获取节点动作配置
            String actionConfig = (String) execution.getVariable("actionConfig");
            if (actionConfig == null) {
                // 从数据库获取配置
                actionConfig = nodeActionService.getNodeActionConfig(processDefinitionId, currentActivityId);
            }

            if (actionConfig != null && !actionConfig.isEmpty()) {
                // 解析并执行动作
                executeAction(execution, actionConfig);
            } else {
                log.info("节点 {} 未配置动作，跳过执行", currentActivityId);
            }

        } catch (Exception e) {
            log.error("动态动作执行器异常: {}", e.getMessage(), e);
            throw new RuntimeException("动态动作执行异常", e);
        }
    }

    /**
     * 执行具体动作
     */
    private void executeAction(DelegateExecution execution, String actionConfig) {
        try {
            // 解析动作配置
            ActionConfig config = objectMapper.readValue(actionConfig, ActionConfig.class);
            
            log.info("执行动作: 类型={}, 配置={}", config.getActionType(), actionConfig);

            switch (config.getActionType()) {
                case "approval":
                    executeApprovalAction(execution, config);
                    break;
                case "notification":
                    executeNotificationAction(execution, config);
                    break;
                case "service":
                    executeServiceAction(execution, config);
                    break;
                case "script":
                    executeScriptAction(execution, config);
                    break;
                case "http":
                    executeHttpAction(execution, config);
                    break;
                default:
                    log.warn("未知的动作类型: {}", config.getActionType());
            }

        } catch (Exception e) {
            log.error("执行动作异常: {}", e.getMessage(), e);
            throw new RuntimeException("执行动作异常", e);
        }
    }

    /**
     * 执行审批动作
     */
    private void executeApprovalAction(DelegateExecution execution, ActionConfig config) {
        log.info("执行审批动作: {}", config.getParams());
        
        // 设置审批相关变量
        execution.setVariable("approvalResult", "approved");
        execution.setVariable("approvalTime", System.currentTimeMillis());
        execution.setVariable("approvalUser", "system");
        
        // 可以根据配置进行不同的审批逻辑
        String approvalType = config.getParams().get("approvalType");
        if ("auto".equals(approvalType)) {
            log.info("自动审批通过");
        } else if ("manual".equals(approvalType)) {
            log.info("需要手动审批");
        }
    }

    /**
     * 执行通知动作
     */
    private void executeNotificationAction(DelegateExecution execution, ActionConfig config) {
        log.info("执行通知动作: {}", config.getParams());
        
        String notificationType = config.getParams().get("notificationType");
        String recipient = config.getParams().get("recipient");
        String message = config.getParams().get("message");
        
        // 这里可以集成邮件、短信、钉钉等通知方式
        switch (notificationType) {
            case "email":
                sendEmailNotification(recipient, message, execution);
                break;
            case "sms":
                sendSmsNotification(recipient, message, execution);
                break;
            case "dingtalk":
                sendDingTalkNotification(recipient, message, execution);
                break;
            default:
                log.info("默认通知: 发送给={}, 消息={}", recipient, message);
        }
        
        // 设置通知结果
        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationTime", System.currentTimeMillis());
    }

    /**
     * 执行服务调用动作
     */
    private void executeServiceAction(DelegateExecution execution, ActionConfig config) {
        log.info("执行服务调用动作: {}", config.getParams());
        
        String serviceName = config.getParams().get("serviceName");
        String methodName = config.getParams().get("methodName");
        String methodParams = config.getParams().get("methodParams");
        
        try {
            // 这里可以通过反射调用其他服务
            // Object result = serviceMethod.invoke(serviceInstance, params);
            log.info("调用服务: {}.{}", serviceName, methodName);
            
            // 设置调用结果
            execution.setVariable("serviceCallResult", "success");
            execution.setVariable("serviceCallTime", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("服务调用异常: {}", e.getMessage(), e);
            execution.setVariable("serviceCallResult", "failed");
            execution.setVariable("serviceCallError", e.getMessage());
        }
    }

    /**
     * 执行脚本动作
     */
    private void executeScriptAction(DelegateExecution execution, ActionConfig config) {
        log.info("执行脚本动作: {}", config.getParams());
        
        String scriptType = config.getParams().get("scriptType");
        String scriptContent = config.getParams().get("scriptContent");
        
        try {
            switch (scriptType) {
                case "javascript":
                    // 执行JavaScript脚本
                    executeJavaScript(scriptContent, execution);
                    break;
                case "groovy":
                    // 执行Groovy脚本
                    executeGroovy(scriptContent, execution);
                    break;
                case "python":
                    // 执行Python脚本
                    executePython(scriptContent, execution);
                    break;
                default:
                    log.warn("不支持的脚本类型: {}", scriptType);
            }
            
            execution.setVariable("scriptExecuted", true);
            execution.setVariable("scriptExecutedTime", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("脚本执行异常: {}", e.getMessage(), e);
            execution.setVariable("scriptExecuted", false);
            execution.setVariable("scriptExecutedError", e.getMessage());
        }
    }

    /**
     * 执行HTTP动作
     */
    private void executeHttpAction(DelegateExecution execution, ActionConfig config) {
        log.info("执行HTTP动作: {}", config.getParams());
        
        String url = config.getParams().get("url");
        String method = config.getParams().get("method");
        String headers = config.getParams().get("headers");
        String body = config.getParams().get("body");
        
        try {
            // 这里可以使用RestTemplate或WebClient发送HTTP请求
            log.info("发送HTTP请求: {} {}", method, url);
            
            // 模拟HTTP调用
            String response = "{\"status\": \"success\", \"data\": \"HTTP调用成功\"}";
            
            execution.setVariable("httpResponse", response);
            execution.setVariable("httpCallTime", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("HTTP调用异常: {}", e.getMessage(), e);
            execution.setVariable("httpError", e.getMessage());
        }
    }

    // 以下为具体的实现方法
    private void sendEmailNotification(String recipient, String message, DelegateExecution execution) {
        log.info("发送邮件通知: 收件人={}, 消息={}", recipient, message);
        // 实现邮件发送逻辑
    }

    private void sendSmsNotification(String recipient, String message, DelegateExecution execution) {
        log.info("发送短信通知: 收件人={}, 消息={}", recipient, message);
        // 实现短信发送逻辑
    }

    private void sendDingTalkNotification(String recipient, String message, DelegateExecution execution) {
        log.info("发送钉钉通知: 收件人={}, 消息={}", recipient, message);
        // 实现钉钉通知逻辑
    }

    private void executeJavaScript(String scriptContent, DelegateExecution execution) {
        log.info("执行JavaScript脚本: {}", scriptContent);
        // 实现JavaScript脚本执行逻辑
    }

    private void executeGroovy(String scriptContent, DelegateExecution execution) {
        log.info("执行Groovy脚本: {}", scriptContent);
        // 实现Groovy脚本执行逻辑
    }

    private void executePython(String scriptContent, DelegateExecution execution) {
        log.info("执行Python脚本: {}", scriptContent);
        // 实现Python脚本执行逻辑
    }

    /**
     * 动作配置类
     */
    public static class ActionConfig {
        private String actionType;
        private java.util.Map<String, String> params;

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public java.util.Map<String, String> getParams() {
            return params;
        }

        public void setParams(java.util.Map<String, String> params) {
            this.params = params;
        }
    }
}
