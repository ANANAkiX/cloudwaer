package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限ID查询DTO
 *
 * @author cloudwaer
 */
@Data
public class PermissionIdQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID（支持字符串和数字格式，避免前端精度丢失）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "权限ID不能为空")
    private Long id;
}

