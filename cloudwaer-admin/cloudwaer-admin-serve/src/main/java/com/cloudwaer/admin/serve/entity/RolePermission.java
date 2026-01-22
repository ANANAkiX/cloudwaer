package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关系实体 注意：中间表使用物理删除，不使用逻辑删除，避免唯一索引冲突
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {

	/**
	 * 角色ID
	 */
	private Long roleId;

	/**
	 * 权限ID
	 */
	private Long permissionId;

}
