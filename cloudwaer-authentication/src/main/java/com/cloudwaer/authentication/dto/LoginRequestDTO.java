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
	@Schema(description = "用户名", required = true, example = "admin")
	private String username;

	/**
	 * 用户名
	 */
	@Schema(description = "邮箱", required = true, example = "admin@qq.com")
	private String email;

	/**
	 * 手机号
	 */
	@Schema(description = "手机号", required = true, example = "13333333333")
	private String phone;

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

	/**
	 * 客户端标识：admin-管理后台 / blog-博客前台（Token 跨端不通用）
	 */
	@NotBlank(message = "clientId不能为空")
	@Schema(description = "客户端标识", required = true, example = "admin")
	private String clientId;

	/**
	 * 第三方登录供应商类型（loginType=THIRD_PARTY 时使用）
	 */
	@Schema(description = "第三方供应商类型", example = "GITHUB")
	private String provider;

	/**
	 * 第三方授权码（loginType=THIRD_PARTY 时使用）
	 */
	@Schema(description = "第三方授权码")
	private String code;

	/**
	 * 第三方授权 state（loginType=THIRD_PARTY 时使用）
	 */
	@Schema(description = "第三方授权 state")
	private String state;

}
