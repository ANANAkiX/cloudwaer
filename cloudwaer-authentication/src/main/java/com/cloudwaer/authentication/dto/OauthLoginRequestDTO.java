package com.cloudwaer.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登录回调请求（授权码换 Token 登录）
 *
 * @author cloudwaer
 */
@Data
public class OauthLoginRequestDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 供应商类型：GITHUB|GITEE|...
	 */
	@NotBlank(message = "供应商类型不能为空")
	private String providerType;

	/**
	 * 供应商回调携带的授权码
	 */
	@NotBlank(message = "授权码不能为空")
	private String code;

	/**
	 * 发起授权时生成的 state
	 */
	@NotBlank(message = "state不能为空")
	private String state;

	/**
	 * 客户端标识（可选，缺省取 state 中记录的值）
	 */
	private String clientId;

}
