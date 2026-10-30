package com.cloudwaer.authentication.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登录「待注册」票据载荷（Redis 短期存储，一次性使用）
 * <p>
 * 授权成功但未绑定系统账号时签发：记住第三方身份与发起方客户端，供注册页提交后建号并绑定。
 *
 * @author cloudwaer
 */
@Data
public class OauthRegisterTicketDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String providerType;

	private String providerUserId;

	private String providerUsername;

	private String email;

	private String avatarUrl;

	/**
	 * 发起登录的客户端标识（admin / blog），注册后签发 Token 时绑定
	 */
	private String clientId;

}
