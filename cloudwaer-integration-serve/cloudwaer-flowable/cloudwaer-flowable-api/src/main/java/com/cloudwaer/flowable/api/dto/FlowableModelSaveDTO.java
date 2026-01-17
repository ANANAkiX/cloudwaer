package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlowableModelSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String modelKey;
    private String modelName;
    private String category;
    private String remark;
    private String bpmnXml;
    private List<FlowableNodeActionDTO> nodeActions;
}
