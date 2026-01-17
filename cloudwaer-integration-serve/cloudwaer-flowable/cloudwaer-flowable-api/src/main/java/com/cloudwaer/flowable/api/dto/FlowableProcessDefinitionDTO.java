package com.cloudwaer.flowable.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlowableProcessDefinitionDTO implements Serializable {
    
    private String id;
    
    private String processKey;
    
    private String processName;
    
    private String category;
    
    private Integer version;
    
    private String description;
    
    private String diagram;
    
    private Integer instanceCount;
    
    private String avgDuration;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private List<FormFieldDTO> formFields;
}
