package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.RoleDTO;
import com.cloudwaer.admin.serve.service.RoleService;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@Tag(name = "角色管理", description = "角色管理接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有角色列表", description = "获取所有角色信息")
    public Result<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询角色列表", description = "分页获取角色信息，支持复合搜索（角色名称、角色代码）")
    public Result<PageResult<RoleDTO>> getRolePage(@RequestParam(value = "current", defaultValue = "1") Long current,
                                                   @RequestParam(value = "size", defaultValue = "10") Long size,
                                                   @RequestParam(value = "keyword", required = false) String keyword) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setCurrent(current);
        pageDTO.setSize(size);
        pageDTO.setKeyword(keyword);
        PageResult<RoleDTO> pageResult = roleService.getRolePage(pageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/detail")
    @Operation(summary = "根据ID获取角色", description = "通过角色ID查询角色详细信息")
    public Result<RoleDTO> getRoleById(@RequestParam("id") Long id) {
        RoleDTO role = roleService.getRoleById(id);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping("/save")
    @Operation(summary = "新增角色", description = "创建新的角色")
    public Result<Boolean> saveRole(@RequestBody @Validated RoleDTO roleDTO) {
        Boolean result = roleService.saveRole(roleDTO);
        return Result.success(result);
    }

    /**
     * 更新角色
     */
    @PutMapping("/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<Boolean> updateRole(@RequestBody @Validated RoleDTO roleDTO) {
        Boolean result = roleService.updateRole(roleDTO);
        return Result.success(result);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除角色", description = "删除指定角色")
    public Result<Boolean> deleteRole(@RequestParam("id") Long id) {
        Boolean result = roleService.deleteRole(id);
        return Result.success(result);
    }

    /**
     * 分配权限给角色
     */
    @PostMapping("/assign-permissions")
    @Operation(summary = "分配权限给角色", description = "为角色分配权限")
    public Result<Boolean> assignPermissions(@RequestParam("roleId") Long roleId,
                                             @RequestBody List<Object> permissionIds) {
        // 处理前端可能发送的字符串ID（避免精度丢失）
        List<Long> longPermissionIds = permissionIds.stream()
                .map(id -> {
                    if (id instanceof Long) {
                        return (Long) id;
                    } else if (id instanceof Integer) {
                        return ((Integer) id).longValue();
                    } else if (id instanceof String) {
                        try {
                            return Long.parseLong((String) id);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("无效的权限ID: " + id);
                        }
                    } else if (id instanceof Number) {
                        return ((Number) id).longValue();
                    } else {
                        throw new IllegalArgumentException("无效的权限ID类型: " + id.getClass().getName());
                    }
                })
                .collect(java.util.stream.Collectors.toList());
        Boolean result = roleService.assignPermissions(roleId, longPermissionIds);
        return Result.success(result);
    }
}

