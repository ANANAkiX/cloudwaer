package com.cloudwaer.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求DTO
 *
 * @author cloudwaer
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名不能为空")
	@Schema(description = "用户名", required = true, example = "visitor")
	private String username;

	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	@Schema(description = "密码", required = true, example = "abc123456")
	private String password;

	/**
	 * 昵称（可空，默认同用户名）
	 */
	@Schema(description = "昵称", example = "访客")
	private String nickname;

	/**
	 * 邮箱（可空）
	 */
	private String email;

}
