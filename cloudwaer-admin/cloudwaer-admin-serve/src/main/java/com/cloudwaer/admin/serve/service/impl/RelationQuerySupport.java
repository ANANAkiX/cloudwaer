package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.admin.serve.entity.RolePermission;
import com.cloudwaer.admin.serve.entity.UserRole;
import com.cloudwaer.admin.serve.mapper.RolePermissionMapper;
import com.cloudwaer.admin.serve.mapper.UserRoleMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared relation query helpers for admin module.
 */
final class RelationQuerySupport {

	private RelationQuerySupport() {
	}

	static List<Long> queryRoleIdsByUserId(UserRoleMapper mapper, Long userId) {
		LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserRole::getUserId, userId);
		List<UserRole> userRoles = mapper.selectList(wrapper);
		return userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
	}

	static List<Long> queryPermissionIdsByRoleId(RolePermissionMapper mapper, Long roleId) {
		LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(RolePermission::getRoleId, roleId);
		List<RolePermission> rolePermissions = mapper.selectList(wrapper);
		return rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
	}

}
