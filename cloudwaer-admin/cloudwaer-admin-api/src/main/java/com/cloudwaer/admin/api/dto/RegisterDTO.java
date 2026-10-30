package com.cloudwaer.admin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册DTO
 *
 * @author cloudwaer
 */
@Data
public class RegisterDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	@NotBlank(message = "用户名不能为空")
	private String username;

	/**
	 * 密码
	 */
	@NotBlank(message = "密码不能为空")
	@Size(min = 6, max = 32, message = "密码长度须在6-32位之间")
	private String password;

	/**
	 * 昵称（可空，默认同用户名）
	 */
	private String nickname;

	/**
	 * 邮箱（可空）
	 */
	private String email;

}
