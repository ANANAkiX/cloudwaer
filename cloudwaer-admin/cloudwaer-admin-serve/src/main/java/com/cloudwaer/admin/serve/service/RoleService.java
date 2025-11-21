package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.admin.api.dto.RoleDTO;
import com.cloudwaer.admin.serve.entity.Role;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author cloudwaer
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<RoleDTO> getAllRoles();

    /**
     * 分页查询角色列表
     *
     * @param pageDTO 分页参数
     * @return 分页结果
     */
    PageResult<RoleDTO> getRolePage(PageDTO pageDTO);

    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色DTO
     */
    RoleDTO getRoleById(Long id);

    /**
     * 保存角色
     *
     * @param roleDTO 角色DTO
     * @return 是否成功
     */
    Boolean saveRole(RoleDTO roleDTO);

    /**
     * 更新角色
     *
     * @param roleDTO 角色DTO
     * @return 是否成功
     */
    Boolean updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    Boolean deleteRole(Long id);

    /**
     * 分配权限给角色
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    Boolean assignPermissions(Long roleId, List<Long> permissionIds);
}

