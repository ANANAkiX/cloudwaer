package com.cloudwaer.admin.api.dto;

import lombok.Data;

@Data
public class DictDTO {
    private Long id;
    private String type;
    private String code;
    private String value;
    private String label;
    private Integer sort;
    private Integer status;
    private String description;
}
