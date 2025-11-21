package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.admin.api.dto.RoleDTO;
import com.cloudwaer.admin.serve.entity.Role;
import com.cloudwaer.admin.serve.entity.RolePermission;
import com.cloudwaer.admin.serve.mapper.RoleMapper;
import com.cloudwaer.admin.serve.mapper.RolePermissionMapper;
import com.cloudwaer.admin.serve.mapper.UserRoleMapper;
import com.cloudwaer.admin.serve.service.PermissionService;
import com.cloudwaer.admin.serve.service.RoleService;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.service.PermissionCacheService;
import com.cloudwaer.common.core.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired(required = false)
    private PermissionService permissionService;

    @Autowired(required = false)
    private PermissionCacheService permissionCacheService;

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = this.list();
        return roles.stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            
            // 查询角色的权限ID列表
            LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RolePermission::getRoleId, role.getId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
            List<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toList());
            roleDTO.setPermissionIds(permissionIds);
            
            return roleDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<RoleDTO> getRolePage(PageDTO pageDTO) {
        // 创建分页对象
        Page<Role> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        
        // 构建查询条件（复合搜索：角色名称、角色代码）
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().trim().isEmpty()) {
            String keyword = pageDTO.getKeyword().trim();
            wrapper.and(w -> w
                .like(Role::getRoleName, keyword)
                .or().like(Role::getRoleCode, keyword)
            );
        }
        
        // 分页查询
        IPage<Role> pageResult = this.page(page, wrapper);
        
        // 转换为DTO并填充权限信息
        List<RoleDTO> roleDTOList = pageResult.getRecords().stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            
            // 查询角色的权限ID列表
            LambdaQueryWrapper<RolePermission> rolePermissionWapper = new LambdaQueryWrapper<>();
            rolePermissionWapper.eq(RolePermission::getRoleId, role.getId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionWapper);
            List<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toList());
            roleDTO.setPermissionIds(permissionIds);
            
            return roleDTO;
        }).collect(Collectors.toList());
        
        return new PageResult<>(roleDTOList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = this.getById(id);
        if (role == null) {
            return null;
        }
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        
        // 查询角色的权限ID列表
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        roleDTO.setPermissionIds(permissionIds);
        
        return roleDTO;
    }

    @Override
    public Boolean saveRole(RoleDTO roleDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        boolean result = this.save(role);
        
        // 保存权限关联
        if (result && roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }

    @Override
    public Boolean updateRole(RoleDTO roleDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        boolean result = this.updateById(role);
        
        // 更新权限关联
        if (result && roleDTO.getPermissionIds() != null) {
            assignPermissions(roleDTO.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }

    @Override
    public Boolean deleteRole(Long id) {
        // 物理删除角色权限关联（中间表不使用逻辑删除）
        rolePermissionMapper.deleteByRoleId(id);
        
        // 删除角色（使用逻辑删除）
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 物理删除原有权限关联（中间表不使用逻辑删除，避免唯一索引冲突）
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
        
        // 更新拥有该角色的所有用户的Token权限信息（不删除Token，只更新权限）
        updateUserTokenPermissionsByRoleId(roleId);
        
        // 更新Redis中的权限缓存（权限映射：API路径 -> 权限代码）
        refreshPermissionCache();
        // 手动更新TOKEN对应的权限映射
        return true;
    }


    private void updateUserTokenPermissionsByRoleId(Long roleId) {
        if (tokenService == null || userRoleMapper == null || permissionService == null || roleId == null) {
            return;
        }

        try {
            // 查询拥有该角色的所有用户ID
            List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
            
            if (userIds != null && !userIds.isEmpty()) {
                // 更新这些用户的Token权限信息
                for (Long userId : userIds) {
                    try {
                        // 从数据库获取用户的最新权限
                        List<String> latestPermissions = permissionService.getPermissionCodesByUserId(userId);
                        
                        // 更新Token中的权限信息（不删除Token，只更新权限）
                        int updateCount = tokenService.updateUserTokenPermissions(userId, latestPermissions);
                        log.info("更新用户Token权限: userId={}, tokenCount={}, permissions={}", 
                                userId, updateCount, latestPermissions);
                    } catch (Exception e) {
                        log.error("更新用户Token权限失败: userId={}", userId, e);
                        // 单个用户更新失败不影响其他用户，继续处理
                    }
                }
                log.info("已更新角色 {} 的所有用户Token权限，用户数量: {}", roleId, userIds.size());
            }
        } catch (Exception e) {
            log.error("更新用户Token权限失败: roleId={}", roleId, e);
            // 不更新Token不影响权限分配的成功，只记录错误日志
        }
    }

    /**
     * 刷新Redis中的权限缓存
     * 从数据库获取最新的权限映射并更新到Redis
     */
    private void refreshPermissionCache() {
        if (permissionCacheService == null || permissionService == null) {
            return;
        }

        try {
            // 从数据库获取最新的权限API映射
            java.util.Map<String, String> permissionMap = permissionService.getPermissionApiMapping();
            
            if (permissionMap != null && !permissionMap.isEmpty()) {
                // 更新Redis中的权限缓存
                permissionCacheService.cachePermissions(permissionMap);
                log.info("权限缓存已更新: 共 {} 个权限映射", permissionMap.size());
            } else {
                log.warn("权限映射为空，无法更新缓存");
            }
        } catch (Exception e) {
            log.error("更新权限缓存失败", e);
            // 不更新缓存不影响权限分配的成功，只记录错误日志
        }
    }
}

