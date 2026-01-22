package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;

	/**
	 * 角色描述
	 */
	private String description;

}
