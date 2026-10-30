package com.cloudwaer.admin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方账号绑定 DTO（内部调用）
 *
 * @author cloudwaer
 */
@Data
public class OauthBindDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "用户ID不能为空")
	private Long userId;

	@NotBlank(message = "供应商类型不能为空")
	private String providerType;

	@NotBlank(message = "第三方用户ID不能为空")
	private String providerUserId;

	private String providerUsername;

	private String email;

	private String avatarUrl;

}
