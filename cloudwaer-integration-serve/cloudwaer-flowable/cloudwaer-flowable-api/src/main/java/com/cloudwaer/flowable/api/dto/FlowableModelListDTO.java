package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FlowableModelListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String modelKey;
    private String modelName;
    private String category;
    private Integer version;
    private Integer modelStatus;
    private LocalDateTime updateTime;
    private String remark;

    /**
     * 模型到期时间
     */
    private LocalDateTime endTime;
}
