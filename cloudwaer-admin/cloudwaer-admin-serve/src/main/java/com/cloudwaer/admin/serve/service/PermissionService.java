package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.admin.api.dto.PermissionDTO;
import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.serve.entity.Permission;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 *
 * @author cloudwaer
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据用户ID获取路由树
     *
     * @param userId 用户ID
     * @return 路由树列表
     */
    List<RouteDTO> getRoutesByUserId(Long userId);

    /**
     * 获取权限树
     *
     * @return 权限树列表
     */
    List<PermissionDTO> getPermissionTree();

    /**
     * 获取权限树（支持搜索）
     *
     * @param keyword 搜索关键词（权限名称、权限代码、权限描述）
     * @return 权限树列表
     */
    List<PermissionDTO> getPermissionTree(String keyword);

    /**
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限DTO
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 保存权限
     *
     * @param permissionDTO 权限DTO
     * @return 是否成功
     */
    Boolean savePermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     *
     * @param permissionDTO 权限DTO
     * @return 是否成功
     */
    Boolean updatePermission(PermissionDTO permissionDTO);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    Boolean deletePermission(Long id);

    /**
     * 根据用户ID获取权限代码集合
     *
     * @param userId 用户ID
     * @return 权限代码集合
     */
    List<String> getPermissionCodesByUserId(Long userId);

    /**
     * 获取所有权限的API映射
     * 返回格式：Map<"GET /api/user/list", "admin:user:list">
     * 只返回权限类型为"操作"（permissionType=3）且配置了httpMethod和apiUrl的权限
     *
     * @return 权限API映射
     */
    Map<String, String> getPermissionApiMapping();
}

