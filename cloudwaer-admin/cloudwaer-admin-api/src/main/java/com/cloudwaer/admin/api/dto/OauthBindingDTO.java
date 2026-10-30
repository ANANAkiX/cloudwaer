package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方账号绑定展示信息
 *
 * @author cloudwaer
 */
@Data
public class OauthBindingDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String providerType;

	private String providerUsername;

	private String avatarUrl;

}
