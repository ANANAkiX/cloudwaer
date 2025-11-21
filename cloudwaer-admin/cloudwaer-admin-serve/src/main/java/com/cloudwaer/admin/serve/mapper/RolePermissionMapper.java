package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关系Mapper
 * 注意：中间表使用物理删除，不使用逻辑删除
 *
 * @author cloudwaer
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 物理删除角色的所有权限关联（不使用逻辑删除）
     *
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询拥有该权限的所有角色ID列表
     *
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    @org.apache.ibatis.annotations.Select("SELECT DISTINCT role_id FROM sys_role_permission WHERE permission_id = #{permissionId}")
    List<Long> selectRoleIdsByPermissionId(@Param("permissionId") Long permissionId);
}



