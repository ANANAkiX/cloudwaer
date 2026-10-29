package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方登录供应商（可配置多组）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oauth_provider")
public class SysOauthProvider extends BaseEntity {

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

	private Integer sort;

	private Integer enabled;

	private String remark;

}
