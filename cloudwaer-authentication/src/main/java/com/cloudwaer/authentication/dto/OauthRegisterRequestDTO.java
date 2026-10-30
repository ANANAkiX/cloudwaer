package com.cloudwaer.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登录后的注册请求（注册并绑定授权的第三方账号）
 *
 * @author cloudwaer
 */
@Data
public class OauthRegisterRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 「待注册」票据（授权成功未绑定时签发，一次性）
	 */
	@NotBlank(message = "注册凭证不能为空")
	private String registerTicket;

	@NotBlank(message = "用户名不能为空")
	private String username;

	/**
	 * 明文或 RSA 密文（密码传输加密启用时），长度在解密后校验
	 */
	@NotBlank(message = "密码不能为空")
	private String password;

	/**
	 * 昵称（可空，默认取第三方昵称）
	 */
	private String nickname;

	/**
	 * 邮箱（可空，默认取第三方邮箱）
	 */
	private String email;

}
