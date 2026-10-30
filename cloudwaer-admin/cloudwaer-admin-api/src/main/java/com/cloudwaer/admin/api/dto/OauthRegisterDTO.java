package com.cloudwaer.admin.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登录自动注册 + 绑定 DTO（内部调用）
 *
 * @author cloudwaer
 */
@Data
public class OauthRegisterDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 建议用户名（冲突时服务端自动加后缀）
	 */
	@NotBlank(message = "用户名不能为空")
	private String username;

	/**
	 * 昵称（可空，默认同用户名）
	 */
	private String nickname;

	private String email;

	private String avatar;

	/**
	 * 第三方身份
	 */
	@NotBlank(message = "供应商类型不能为空")
	private String providerType;

	@NotBlank(message = "第三方用户ID不能为空")
	private String providerUserId;

	private String providerUsername;

	private String avatarUrl;

}
