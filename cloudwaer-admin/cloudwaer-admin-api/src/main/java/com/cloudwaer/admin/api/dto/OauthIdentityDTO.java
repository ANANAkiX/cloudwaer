package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方身份标识（OAuth 换票后拿到的用户信息）
 *
 * @author cloudwaer
 */
@Data
public class OauthIdentityDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 供应商类型：GITHUB|GITEE|...
	 */
	private String providerType;

	/**
	 * 第三方用户唯一 ID
	 */
	private String providerUserId;

	/**
	 * 第三方用户名
	 */
	private String providerUsername;

	private String email;

	private String avatarUrl;

	/**
	 * 供应商 access_token（不落库，仅当次流程内使用）
	 */
	private String accessToken;

}
