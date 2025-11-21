package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.admin.api.dto.PermissionDTO;
import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.RouteMeta;
import com.cloudwaer.admin.serve.entity.Permission;
import com.cloudwaer.admin.serve.entity.RolePermission;
import com.cloudwaer.admin.serve.entity.UserRole;
import com.cloudwaer.admin.serve.mapper.PermissionMapper;
import com.cloudwaer.admin.serve.mapper.RolePermissionMapper;
import com.cloudwaer.admin.serve.mapper.UserRoleMapper;
import com.cloudwaer.admin.serve.service.PermissionService;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.service.PermissionCacheService;
import com.cloudwaer.common.core.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired(required = false)
    private PermissionCacheService permissionCacheService;

    @Autowired(required = false)
    private TokenService tokenService;

    @Override
    public List<RouteDTO> getRoutesByUserId(Long userId) {
        // 1. 查询用户的角色ID列表
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 根据角色ID查询权限ID列表
        LambdaQueryWrapper<RolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionWrapper);
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 查询用户拥有的所有权限（不限制类型，包括菜单、页面、操作）
        LambdaQueryWrapper<Permission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.in(Permission::getId, permissionIds);
        List<Permission> userPermissions = this.list(permissionWrapper);

        // 4. 获取所有权限的映射（用于查找父权限）
        List<Permission> allPermissions = this.list();
        Map<Long, Permission> allPermissionMap = allPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));

        // 5. 收集所有菜单权限ID（包括用户直接拥有的菜单权限和通过子权限继承的父菜单权限）
        Set<Long> menuPermissionIds = new HashSet<>();
        
        // 5.1 首先添加用户直接拥有的菜单权限
        for (Permission permission : userPermissions) {
            if (permission.getPermissionType() != null && permission.getPermissionType() == 1) {
                menuPermissionIds.add(permission.getId());
            }
        }
        
        // 5.2 对于用户拥有的每个权限，递归查找所有父级菜单权限
        for (Permission permission : userPermissions) {
            addParentMenuPermissionIds(permission.getParentId(), allPermissionMap, menuPermissionIds);
        }

        if (menuPermissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 6. 查询所有菜单权限（permission_type = 1）
        List<Permission> menuPermissions = allPermissions.stream()
                .filter(p -> menuPermissionIds.contains(p.getId()))
                .filter(p -> p.getPermissionType() != null && p.getPermissionType() == 1)
                .sorted(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // 7. 构建权限ID到Permission的映射（只包含菜单权限）
        Map<Long, Permission> permissionMap = menuPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));

        // 8. 转换为RouteDTO列表，同时建立权限ID到RouteDTO的映射
        Map<Long, RouteDTO> routeMap = new java.util.HashMap<>();
        List<RouteDTO> routeList = new ArrayList<>();
        
        for (Permission permission : menuPermissions) {
            RouteDTO routeDTO = convertToRouteDTO(permission);
            routeMap.put(permission.getId(), routeDTO);
            routeList.add(routeDTO);
        }

        // 9. 构建树形结构
        return buildTree(routeList, routeMap, permissionMap);
    }

    /**
     * 递归添加父级菜单权限ID
     * 只添加菜单类型（permission_type = 1）的权限
     *
     * @param parentId 父权限ID
     * @param allPermissionMap 所有权限的映射
     * @param menuPermissionIds 收集菜单权限ID的集合
     */
    private void addParentMenuPermissionIds(Long parentId, Map<Long, Permission> allPermissionMap, Set<Long> menuPermissionIds) {
        if (parentId == null || parentId == 0) {
            return;
        }

        // 如果已经添加过，避免循环引用
        if (menuPermissionIds.contains(parentId)) {
            return;
        }

        Permission parentPermission = allPermissionMap.get(parentId);
        if (parentPermission != null) {
            // 如果是菜单类型（permission_type = 1），添加到集合中
            if (parentPermission.getPermissionType() != null && parentPermission.getPermissionType() == 1) {
                menuPermissionIds.add(parentId);
            }
            // 递归查找父权限的父权限
            addParentMenuPermissionIds(parentPermission.getParentId(), allPermissionMap, menuPermissionIds);
        }
    }

    /**
     * 将Permission转换为RouteDTO
     */
    private RouteDTO convertToRouteDTO(Permission permission) {
        RouteDTO routeDTO = new RouteDTO();
        
        // 设置路径：如果有route_path则使用，否则使用权限编码生成虚拟路径（用于父级菜单）
        if (permission.getRoutePath() != null && !permission.getRoutePath().isEmpty()) {
            routeDTO.setPath(permission.getRoutePath());
        } else {
            // 父级菜单没有route_path时，使用虚拟路径（不会实际访问，仅用于菜单结构）
            // 权限编码格式：模块:模块:操作，转换为路径时使用 - 分隔
            String virtualPath = permission.getPermissionCode().toLowerCase().replace(":", "-");
            routeDTO.setPath("/" + virtualPath);
        }
        
        routeDTO.setName(permission.getPermissionCode());
        routeDTO.setParentId(permission.getParentId());
        routeDTO.setApiUrl(permission.getApiUrl());

        // 设置组件路径（只有有实际路由路径的才设置组件）
        // 前端使用 @/views${route.component}，所以component应该是类似 "/dashboard" 或 "/admin/Permission" 的路径
        // 需要将路径转换为组件路径：/admin/permission -> /admin/Permission
        if (permission.getRoutePath() != null && !permission.getRoutePath().isEmpty()) {
            String componentPath = permission.getRoutePath();
            // 确保路径以 / 开头
            if (!componentPath.startsWith("/")) {
                componentPath = "/" + componentPath;
            }
            // 将路径转换为组件路径：将最后一段路径的首字母大写
            // 例如：/admin/permission -> /admin/Permission
            String[] pathParts = componentPath.split("/");
            if (pathParts.length > 0) {
                String lastPart = pathParts[pathParts.length - 1];
                if (!lastPart.isEmpty()) {
                    // 首字母大写
                    lastPart = lastPart.substring(0, 1).toUpperCase() + lastPart.substring(1);
                    pathParts[pathParts.length - 1] = lastPart;
                    componentPath = String.join("/", pathParts);
                }
            }
            routeDTO.setComponent(componentPath);
        }
        // 如果没有route_path，不设置component（父级菜单不需要组件）

        // 设置路由元信息
        RouteMeta meta = new RouteMeta();
        meta.setTitle(permission.getPermissionName());
        meta.setIcon(permission.getIcon());
        // 如果没有route_path，标记为隐藏（不显示在路由中，只显示在菜单中）
        meta.setHidden(permission.getRoutePath() == null || permission.getRoutePath().isEmpty());
        meta.setRequiresAuth(true);
        routeDTO.setMeta(meta);

        return routeDTO;
    }

    /**
     * 构建树形结构
     */
    private List<RouteDTO> buildTree(List<RouteDTO> routeList, Map<Long, RouteDTO> routeMap, Map<Long, Permission> permissionMap) {
        if (routeList == null || routeList.isEmpty()) {
            return new ArrayList<>();
        }

        List<RouteDTO> rootRoutes = new ArrayList<>();

        // 构建树形结构
        for (RouteDTO route : routeList) {
            if (route.getParentId() == null || route.getParentId() == 0) {
                // 根节点
                rootRoutes.add(route);
            } else {
                // 查找父节点（通过parentId在routeMap中查找）
                RouteDTO parentRoute = routeMap.get(route.getParentId());
                if (parentRoute != null) {
                    if (parentRoute.getChildren() == null) {
                        parentRoute.setChildren(new ArrayList<>());
                    }
                    parentRoute.getChildren().add(route);
                } else {
                    // 父节点不在当前列表中，作为根节点
                    rootRoutes.add(route);
                }
            }
        }

        // 递归排序
        sortRoutes(rootRoutes, permissionMap);

        return rootRoutes;
    }

    /**
     * 递归排序路由
     */
    private void sortRoutes(List<RouteDTO> routes, Map<Long, Permission> permissionMap) {
        if (routes == null || routes.isEmpty()) {
            return;
        }
        
        // 这里可以根据需要实现排序逻辑
        // 暂时保持原顺序
        
        // 递归排序子节点
        for (RouteDTO route : routes) {
            if (route.getChildren() != null && !route.getChildren().isEmpty()) {
                sortRoutes(route.getChildren(), permissionMap);
            }
        }
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        return getPermissionTree(null);
    }

    @Override
    public List<PermissionDTO> getPermissionTree(String keyword) {
        // 查询所有权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            // 复合搜索：权限名称、权限代码、权限描述
            wrapper.and(w -> w
                .like(Permission::getPermissionName, searchKeyword)
                .or().like(Permission::getPermissionCode, searchKeyword)
                .or().like(Permission::getDescription, searchKeyword)
            );
        }
        wrapper.orderByAsc(Permission::getSort);
        List<Permission> permissions = this.list(wrapper);

        // 转换为DTO
        List<PermissionDTO> permissionDTOList = permissions.stream()
                .map(this::convertToPermissionDTO)
                .collect(Collectors.toList());

        // 如果有搜索关键词，需要包含匹配节点的所有父节点和子节点
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 获取所有权限（用于查找父节点）
            List<Permission> allPermissions = this.list();
            Map<Long, PermissionDTO> allPermissionMap = allPermissions.stream()
                    .map(this::convertToPermissionDTO)
                    .collect(Collectors.toMap(PermissionDTO::getId, p -> p));

            // 找到所有匹配节点的ID
            Set<Long> matchedIds = permissionDTOList.stream()
                    .map(PermissionDTO::getId)
                    .collect(Collectors.toSet());

            // 找到所有需要包含的节点（匹配节点 + 父节点 + 子节点）
            Set<Long> includeIds = new HashSet<>(matchedIds);
            
            // 添加所有父节点
            for (PermissionDTO dto : permissionDTOList) {
                Long parentId = dto.getParentId();
                while (parentId != null && parentId != 0) {
                    if (!includeIds.contains(parentId) && allPermissionMap.containsKey(parentId)) {
                        includeIds.add(parentId);
                        PermissionDTO parent = allPermissionMap.get(parentId);
                        parentId = parent.getParentId();
                    } else {
                        break;
                    }
                }
            }

            // 添加所有子节点（递归查找）
            Set<Long> childIds = new HashSet<>();
            for (Long matchedId : matchedIds) {
                findChildren(matchedId, allPermissionMap, childIds);
            }
            includeIds.addAll(childIds);

            // 过滤出需要包含的权限
            permissionDTOList = allPermissionMap.values().stream()
                    .filter(p -> includeIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        // 构建树形结构
        List<PermissionDTO> tree = buildPermissionTree(permissionDTOList);
        
        // 如果有搜索关键词，标记所有节点为展开状态
        if (keyword != null && !keyword.trim().isEmpty()) {
            markAllExpanded(tree);
        }
        
        return tree;
    }

    /**
     * 递归查找所有子节点
     */
    private void findChildren(Long parentId, Map<Long, PermissionDTO> allPermissionMap, Set<Long> childIds) {
        for (PermissionDTO dto : allPermissionMap.values()) {
            if (parentId.equals(dto.getParentId())) {
                childIds.add(dto.getId());
                findChildren(dto.getId(), allPermissionMap, childIds);
            }
        }
    }

    /**
     * 标记所有节点为展开状态（用于搜索结果显示）
     */
    private void markAllExpanded(List<PermissionDTO> tree) {
        for (PermissionDTO dto : tree) {
            // 这里可以添加一个expanded字段到DTO，或者在前端处理
            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                markAllExpanded(dto.getChildren());
            }
        }
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = this.getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return convertToPermissionDTO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean savePermission(PermissionDTO permissionDTO) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionDTO.getPermissionCode());
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        // ID的自动生成已在MetaObjectHandler中统一处理，无需在此判断
        boolean result = this.save(permission);
        
        // 如果保存成功，更新权限缓存和用户Token权限信息
        if (result) {
            // 更新权限映射缓存（如果是操作类型权限）
            if (permissionDTO.getPermissionType() != null && permissionDTO.getPermissionType() == 3) {
                refreshPermissionCache();
            }
            // 注意：新增权限后，如果该权限还没有被分配给任何角色，则不需要更新用户Token
            // 只有当权限被分配给角色后，才会通过分配权限的逻辑更新用户Token
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePermission(PermissionDTO permissionDTO) {
        if (permissionDTO.getId() == null) {
            throw new BusinessException("权限ID不能为空");
        }

        Permission permission = this.getById(permissionDTO.getId());
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否已被其他权限使用
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionDTO.getPermissionCode())
                .ne(Permission::getId, permissionDTO.getId());
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("权限编码已被使用");
        }

        // 保存旧的权限编码，用于判断是否需要更新用户Token
        String oldPermissionCode = permission.getPermissionCode();
        boolean permissionCodeChanged = !oldPermissionCode.equals(permissionDTO.getPermissionCode());
        
        BeanUtils.copyProperties(permissionDTO, permission);
        boolean result = this.updateById(permission);
        
        // 如果更新成功，更新权限缓存和用户Token权限信息
        if (result) {
            // 更新权限映射缓存（因为可能修改了API地址或请求方法）
            refreshPermissionCache();
            
            // 如果权限编码改变了，需要更新所有拥有该权限的用户的Token权限信息
            // 因为权限编码改变会影响用户的权限代码列表
            if (permissionCodeChanged) {
                updateUserTokensByPermissionId(permissionDTO.getId());
            }
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermission(Long id) {
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子权限，无法删除");
        }

        // 检查是否被角色使用
        LambdaQueryWrapper<RolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.eq(RolePermission::getPermissionId, id);
        long rolePermissionCount = rolePermissionMapper.selectCount(rolePermissionWrapper);
        if (rolePermissionCount > 0) {
            throw new BusinessException("权限已被角色使用，无法删除");
        }

        // 在删除前，先更新所有拥有该权限的用户的Token权限信息
        // 因为删除权限后，这些用户应该失去该权限
        updateUserTokensByPermissionId(id);
        
        boolean result = this.removeById(id);
        
        // 如果删除成功，更新权限缓存
        if (result) {
            refreshPermissionCache();
        }
        
        return result;
    }

    /**
     * 将Permission转换为PermissionDTO
     */
    private PermissionDTO convertToPermissionDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }

    /**
     * 构建权限树
     */
    private List<PermissionDTO> buildPermissionTree(List<PermissionDTO> permissionList) {
        if (permissionList == null || permissionList.isEmpty()) {
            return new ArrayList<>();
        }

        // 建立ID到DTO的映射
        Map<Long, PermissionDTO> permissionMap = permissionList.stream()
                .collect(Collectors.toMap(PermissionDTO::getId, dto -> dto));

        List<PermissionDTO> rootList = new ArrayList<>();

        // 构建树形结构
        for (PermissionDTO dto : permissionList) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                // 根节点
                rootList.add(dto);
            } else {
                // 查找父节点
                PermissionDTO parent = permissionMap.get(dto.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dto);
                } else {
                    // 找不到父节点，作为根节点
                    rootList.add(dto);
                }
            }
        }

        return rootList;
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        // 1. 查询用户的角色ID列表
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 根据角色ID查询权限ID列表
        LambdaQueryWrapper<RolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionWrapper);
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 查询权限列表，获取权限代码
        LambdaQueryWrapper<Permission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.in(Permission::getId, permissionIds);
        List<Permission> permissions = this.list(permissionWrapper);

        // 4. 获取所有权限的映射（用于查找父权限）
        List<Permission> allPermissions = this.list();
        Map<Long, Permission> permissionMap = allPermissions.stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));

        // 5. 收集所有权限ID（包括直接权限和所有父权限）
        Set<Long> allPermissionIds = new HashSet<>(permissionIds);
        for (Permission permission : permissions) {
            // 递归添加所有父权限ID
            addParentPermissionIds(permission.getParentId(), permissionMap, allPermissionIds);
        }

        // 6. 查询所有权限（包括父权限），获取权限代码
        List<String> permissionCodes = new ArrayList<>();
        for (Long pid : allPermissionIds) {
            Permission p = permissionMap.get(pid);
            if (p != null && p.getPermissionCode() != null && !p.getPermissionCode().isEmpty()) {
                permissionCodes.add(p.getPermissionCode());
            }
        }

        // 7. 去重并返回
        return permissionCodes.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 递归添加父权限ID
     *
     * @param parentId 父权限ID
     * @param permissionMap 所有权限的映射
     * @param allPermissionIds 收集所有权限ID的集合
     */
    private void addParentPermissionIds(Long parentId, Map<Long, Permission> permissionMap, Set<Long> allPermissionIds) {
        if (parentId == null || parentId == 0) {
            return;
        }

        // 如果已经添加过，避免循环引用
        if (allPermissionIds.contains(parentId)) {
            return;
        }

        Permission parentPermission = permissionMap.get(parentId);
        if (parentPermission != null) {
            // 添加父权限ID
            allPermissionIds.add(parentId);
            // 递归添加父权限的父权限
            addParentPermissionIds(parentPermission.getParentId(), permissionMap, allPermissionIds);
        }
    }

    @Override
    public Map<String, String> getPermissionApiMapping() {
        // 查询所有权限类型为"操作"（permissionType=3）且配置了httpMethod和apiUrl的权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionType, 3) // 3-操作
                .isNotNull(Permission::getHttpMethod)
                .isNotNull(Permission::getApiUrl)
                .ne(Permission::getHttpMethod, "")
                .ne(Permission::getApiUrl, "");
        
        List<Permission> permissions = this.list(wrapper);
        
        // 构建权限映射：Map<"GET /api/user/list", "admin:user:list">
        Map<String, String> permissionMap = new HashMap<>();
        for (Permission permission : permissions) {
            String httpMethod = permission.getHttpMethod();
            String apiUrl = permission.getApiUrl();
            String permissionCode = permission.getPermissionCode();
            
            if (httpMethod != null && apiUrl != null && permissionCode != null) {
                // 规范化API URL（去除末尾的斜杠）
                String normalizedApiUrl = apiUrl.trim();
                if (normalizedApiUrl.endsWith("/") && normalizedApiUrl.length() > 1) {
                    normalizedApiUrl = normalizedApiUrl.substring(0, normalizedApiUrl.length() - 1);
                }
                
                // 构建Key：GET /api/user/list
                String key = httpMethod.toUpperCase() + " " + normalizedApiUrl;
                permissionMap.put(key, permissionCode);
            }
        }
        
        return permissionMap;
    }

    /**
     * 刷新Redis中的权限缓存
     * 从数据库获取最新的权限映射并更新到Redis
     */
    private void refreshPermissionCache() {
        if (permissionCacheService == null) {
            return;
        }

        try {
            // 从数据库获取最新的权限API映射
            Map<String, String> permissionMap = getPermissionApiMapping();
            
            if (permissionMap != null && !permissionMap.isEmpty()) {
                // 更新Redis中的权限缓存
                permissionCacheService.cachePermissions(permissionMap);
                log.info("权限缓存已更新: 共 {} 个权限映射", permissionMap.size());
            } else {
                log.warn("权限映射为空，无法更新缓存");
            }
        } catch (Exception e) {
            log.error("更新权限缓存失败", e);
            // 不更新缓存不影响操作的成功，只记录错误日志
        }
    }

    /**
     * 更新拥有指定权限的所有用户的Token权限信息
     * 通过权限ID -> 角色ID -> 用户ID 的关联关系，找到所有相关用户并更新其Token权限
     *
     * @param permissionId 权限ID
     */
    private void updateUserTokensByPermissionId(Long permissionId) {
        if (tokenService == null || rolePermissionMapper == null || userRoleMapper == null || permissionId == null) {
            return;
        }

        try {
            // 1. 查询拥有该权限的所有角色ID
            List<Long> roleIds = rolePermissionMapper.selectRoleIdsByPermissionId(permissionId);
            
            if (roleIds == null || roleIds.isEmpty()) {
                log.debug("权限 {} 没有被分配给任何角色，无需更新用户Token", permissionId);
                return;
            }

            // 2. 查询拥有这些角色的所有用户ID（去重）
            Set<Long> userIds = new HashSet<>();
            for (Long roleId : roleIds) {
                List<Long> roleUserIds = userRoleMapper.selectUserIdsByRoleId(roleId);
                if (roleUserIds != null) {
                    userIds.addAll(roleUserIds);
                }
            }

            if (userIds.isEmpty()) {
                log.debug("权限 {} 关联的角色下没有用户，无需更新用户Token", permissionId);
                return;
            }

            // 3. 更新这些用户的Token权限信息
            for (Long userId : userIds) {
                try {
                    // 从数据库获取用户的最新权限
                    List<String> latestPermissions = getPermissionCodesByUserId(userId);
                    
                    // 更新Token中的权限信息（不删除Token，只更新权限）
                    int updateCount = tokenService.updateUserTokenPermissions(userId, latestPermissions);
                    log.debug("更新用户Token权限: userId={}, tokenCount={}, permissions={}", 
                            userId, updateCount, latestPermissions);
                } catch (Exception e) {
                    log.error("更新用户Token权限失败: userId={}", userId, e);
                    // 单个用户更新失败不影响其他用户，继续处理
                }
            }
            
            log.info("已更新权限 {} 的所有用户Token权限，用户数量: {}", permissionId, userIds.size());
        } catch (Exception e) {
            log.error("更新用户Token权限失败: permissionId={}", permissionId, e);
            // 不更新Token不影响操作的成功，只记录错误日志
        }
    }
}

