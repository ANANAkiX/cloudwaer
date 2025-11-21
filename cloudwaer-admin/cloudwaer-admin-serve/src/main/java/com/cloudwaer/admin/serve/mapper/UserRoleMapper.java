package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关系Mapper
 * 注意：中间表使用物理删除，不使用逻辑删除
 *
 * @author cloudwaer
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 物理删除用户的所有角色关联（不使用逻辑删除）
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询拥有该角色的所有用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @org.apache.ibatis.annotations.Select("SELECT DISTINCT user_id FROM sys_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
}

