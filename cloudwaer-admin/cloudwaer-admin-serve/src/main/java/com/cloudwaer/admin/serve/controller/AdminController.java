package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.service.PermissionService;
import com.cloudwaer.admin.serve.service.UserService;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin服务控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin管理", description = "用户、路由、权限管理接口")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	/**
	 * 根据用户名获取用户信息
	 */
	@GetMapping("/user/username")
	@Operation(summary = "根据用户名获取用户信息", description = "通过用户名查询用户详细信息")
	public Result<UserDTO> getUserByUsername(@RequestParam("username") String username) {
		UserDTO userDTO = userService.getUserByUsername(username);
		return Result.success(userDTO);
	}

	/**
	 * 根据邮箱获取用户信息
	 */
	@GetMapping("/user/email")
	@Operation(summary = "根据用户名获取用户信息", description = "通过用户名查询用户详细信息")
	public Result<UserDTO> getUserByEmail(@RequestParam("email") String email) {
		UserDTO userDTO = userService.getUserByEmail(email);
		return Result.success(userDTO);
	}

	/**
	 * 获取当前用户信息
	 */
	@GetMapping("/user/current")
	@Operation(summary = "获取当前用户信息", description = "通过Token获取当前登录用户的详细信息")
	public Result<UserDTO> getCurrentUser() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(401, "未登录或Token无效");
		}
		UserDTO userDTO = userService.getUserById(userId);
		return Result.success(userDTO);
	}

	/**
	 * 根据用户ID获取路由列表
	 */
	@GetMapping("/route/user")
	@Operation(summary = "根据用户ID获取路由列表", description = "通过用户ID查询用户可访问的路由列表（树形结构）")
	public Result<List<RouteDTO>> getRoutesByUserId(@RequestParam("userId") Long userId) {
		List<RouteDTO> routes = permissionService.getRoutesByUserId(userId);
		return Result.success(routes);
	}

	/**
	 * 获取当前用户的路由列表
	 */
	@GetMapping("/route/current")
	@Operation(summary = "获取当前用户的路由列表", description = "通过Token获取当前登录用户可访问的路由列表（树形结构）")
	public Result<List<RouteDTO>> getCurrentUserRoutes() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(401, "未登录或Token无效");
		}
		List<RouteDTO> routes = permissionService.getRoutesByUserId(userId);
		return Result.success(routes);
	}

	/**
	 * 根据用户ID获取权限列表
	 */
	@GetMapping("/permission/user")
	@Operation(summary = "根据用户ID获取权限列表", description = "通过用户ID查询用户拥有的权限编码列表")
	public Result<List<String>> getPermissionsByUserId(@RequestParam("userId") Long userId) {
		List<String> permissions = permissionService.getPermissionCodesByUserId(userId);
		return Result.success(permissions);
	}

	/**
	 * 获取当前用户的权限列表
	 */
	@GetMapping("/permission/current")
	@Operation(summary = "获取当前用户的权限列表", description = "通过Token获取当前登录用户拥有的权限编码列表")
	public Result<List<String>> getCurrentUserPermissions() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(401, "未登录或Token无效");
		}
		List<String> permissions = permissionService.getPermissionCodesByUserId(userId);
		return Result.success(permissions);
	}

}
