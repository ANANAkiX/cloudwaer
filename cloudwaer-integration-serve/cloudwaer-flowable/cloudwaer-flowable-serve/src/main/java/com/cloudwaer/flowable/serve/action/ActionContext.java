package com.cloudwaer.flowable.serve.action;

import lombok.Data;

import java.util.Map;

@Data
public class ActionContext {

    private String processDefinitionId;
    private String processDefinitionKey;
    private Integer processDefinitionVersion;
    private String processInstanceId;
    private String taskId;
    private String taskDefinitionKey;
    private String eventType;
    private Map<String, Object> variables;
}
