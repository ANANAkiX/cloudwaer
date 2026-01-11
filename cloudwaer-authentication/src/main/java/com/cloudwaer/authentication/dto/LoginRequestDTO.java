package com.cloudwaer.authentication.dto;

import com.cloudwaer.authentication.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 登录请求DTO
 *
 * @author cloudwaer
 */
@Data
@Schema(description = "登录请求")
public class LoginRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "admin123")
    private String password;

    /**
     * 登录方式
     */
    @NotBlank(message = "登录方式不能为空")
    @Schema(description = "登录方式", required = true, example = "")
    public String loginType;

    /**
     * 范围
     */
    @Schema(description = "有效范围", required = true, example = "")
    public String scope;

}

