package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlowableModelDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String modelKey;
    private String modelName;
    private String category;
    private Integer version;
    private Integer modelStatus;
    private String bpmnXml;
    private List<FlowableNodeActionDTO> nodeActions;
    private String remark;
    private LocalDateTime updateTime;
}
