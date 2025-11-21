package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.service.UserService;
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
 * 用户管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@Tag(name = "用户管理", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有用户列表", description = "获取所有用户信息")
    public Result<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return Result.success(users);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表", description = "分页获取用户信息，支持复合搜索（用户名、昵称、邮箱、手机号）")
    public Result<PageResult<UserDTO>> getUserPage(@RequestParam(value = "current", defaultValue = "1") Long current,
                                                   @RequestParam(value = "size", defaultValue = "10") Long size,
                                                   @RequestParam(value = "keyword", required = false) String keyword) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setCurrent(current);
        pageDTO.setSize(size);
        pageDTO.setKeyword(keyword);
        PageResult<UserDTO> pageResult = userService.getUserPage(pageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/detail")
    @Operation(summary = "根据ID获取用户", description = "通过用户ID查询用户详细信息")
    public Result<UserDTO> getUserById(@RequestParam("id") Long id) {
        UserDTO user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping("/save")
    @Operation(summary = "新增用户", description = "创建新的用户")
    public Result<Boolean> saveUser(@RequestBody @Validated UserDTO userDTO) {
        Boolean result = userService.saveUser(userDTO);
        return Result.success(result);
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Boolean> updateUser(@RequestBody @Validated UserDTO userDTO) {
        Boolean result = userService.updateUser(userDTO);
        return Result.success(result);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Boolean> deleteUser(@RequestParam("id") Long id) {
        Boolean result = userService.deleteUser(id);
        return Result.success(result);
    }

    /**
     * 分配角色给用户
     */
    @PostMapping("/assign-roles")
    @Operation(summary = "分配角色给用户", description = "为用户分配角色")
    public Result<Boolean> assignRoles(@RequestParam("userId") Long userId,
                                       @RequestBody List<Object> roleIds) {
        // 处理前端可能发送的字符串ID（避免精度丢失）
        List<Long> longRoleIds = roleIds.stream()
                .map(id -> {
                    if (id instanceof Long) {
                        return (Long) id;
                    } else if (id instanceof Integer) {
                        return ((Integer) id).longValue();
                    } else if (id instanceof String) {
                        try {
                            return Long.parseLong((String) id);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("无效的角色ID: " + id);
                        }
                    } else if (id instanceof Number) {
                        return ((Number) id).longValue();
                    } else {
                        throw new IllegalArgumentException("无效的角色ID类型: " + id.getClass().getName());
                    }
                })
                .collect(java.util.stream.Collectors.toList());
        Boolean result = userService.assignRoles(userId, longRoleIds);
        return Result.success(result);
    }
}

