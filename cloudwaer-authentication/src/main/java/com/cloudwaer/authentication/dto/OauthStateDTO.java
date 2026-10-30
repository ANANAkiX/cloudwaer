package com.cloudwaer.authentication.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * OAuth 授权 state 载荷（Redis 短期存储，防 CSRF 并携带发起方上下文）
 *
 * @author cloudwaer
 */
@Data
public class OauthStateDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String INTENT_LOGIN = "LOGIN";

	public static final String INTENT_BIND = "BIND";

	/**
	 * 意图：LOGIN-登录 / BIND-绑定已有账号
	 */
	private String intent;

	/**
	 * 供应商类型
	 */
	private String providerType;

	/**
	 * 发起方客户端标识（admin / blog），登录签发 Token 时绑定
	 */
	private String clientId;

	/**
	 * 授权完成后回跳的前端地址（白名单校验）
	 */
	private String returnTo;

	/**
	 * 绑定目标用户ID（intent=BIND 时有值）
	 */
	private Long userId;

}
