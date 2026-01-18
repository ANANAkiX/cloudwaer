package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowableNodeActionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeId;
    private String nodeName;
    private String eventType;
    private String actionType;
    private String actionConfig;
}
