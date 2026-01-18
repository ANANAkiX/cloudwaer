package com.cloudwaer.admin.api.dto;

import lombok.Data;

@Data
public class DictItemDTO {
    private Long id;
    private Long dictId;
    private String code;
    private String value;
    private String label;
    private Integer sort;
    private Integer status;
    private String description;
}
