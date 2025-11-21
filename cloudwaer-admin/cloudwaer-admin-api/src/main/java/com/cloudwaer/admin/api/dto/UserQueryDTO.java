package com.cloudwaer.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户查询请求DTO
 *
 * @author cloudwaer
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
}

