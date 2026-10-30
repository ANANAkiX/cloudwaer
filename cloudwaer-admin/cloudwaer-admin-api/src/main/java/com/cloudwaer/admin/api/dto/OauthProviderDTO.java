package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登录供应商配置 DTO（内部调用，含 Secret）
 *
 * @author cloudwaer
 */
@Data
public class OauthProviderDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String providerType;

	private String providerName;

	private String clientId;

	private String clientSecret;

	private String authUrl;

	private String tokenUrl;

	private String userInfoUrl;

	private String scope;

	private String redirectUri;

	private String icon;

	private Integer enabled;

}
