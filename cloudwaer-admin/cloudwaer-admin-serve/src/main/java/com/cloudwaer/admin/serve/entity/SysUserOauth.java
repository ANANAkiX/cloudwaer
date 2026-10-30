package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方账号绑定
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_oauth")
public class SysUserOauth extends BaseEntity {

	/**
	 * 系统用户ID
	 */
	private Long userId;

	/**
	 * 供应商类型：GITHUB|GITEE|WECHAT|GOOGLE|CUSTOM
	 */
	private String providerType;

	/**
	 * 第三方用户唯一ID
	 */
	private String providerUserId;

	/**
	 * 第三方用户名
	 */
	private String providerUsername;

	private String email;

	private String avatarUrl;

}
