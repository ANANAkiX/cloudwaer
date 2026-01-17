package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FlowableTaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String processDefinitionKey;
    private String assignee;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private String status;
}
