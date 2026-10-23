package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.PermissionDTO;
import com.cloudwaer.admin.api.dto.PermissionIdQueryDTO;
import com.cloudwaer.admin.serve.service.PermissionService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/permission")
@Tag(name = "权限管理", description = "权限管理接口")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	/**
	 * 获取权限树
	 */
	@GetMapping("/tree")
	@PermissionCode("admin:permission:view")
	@Operation(summary = "获取权限树", description = "获取所有权限的树形结构，支持复合搜索（权限名称、权限代码、权限描述），搜索结果默认展开")
	public Result<List<PermissionDTO>> getPermissionTree(
			@RequestParam(value = "keyword", required = false) String keyword) {
		List<PermissionDTO> tree = permissionService.getPermissionTree(keyword);
		return Result.success(tree);
	}

	/**
	 * 根据ID获取权限
	 */
	@GetMapping("/detail")
	@PermissionCode("admin:permission:view")
	@Operation(summary = "根据ID获取权限", description = "通过权限ID查询权限详细信息")
	public Result<PermissionDTO> getPermissionById(@RequestParam Long id) {
		PermissionDTO permission = permissionService.getPermissionById(id);
		return Result.success(permission);
	}

	/**
	 * 新增权限
	 */
	@PostMapping("/save")
	@PermissionCode("admin:permission:add")
	@Operation(summary = "新增权限", description = "创建新的权限")
	public Result<Boolean> savePermission(@RequestBody @Validated PermissionDTO permissionDTO) {
		Boolean result = permissionService.savePermission(permissionDTO);
		return Result.success(result);
	}

	/**
	 * 更新权限
	 */
	@PutMapping("/update")
	@PermissionCode("admin:permission:edit")
	@Operation(summary = "更新权限", description = "更新权限信息")
	public Result<Boolean> updatePermission(@RequestBody @Validated PermissionDTO permissionDTO) {
		Boolean result = permissionService.updatePermission(permissionDTO);
		return Result.success(result);
	}

	/**
	 * 删除权限
	 */
	@DeleteMapping("/delete")
	@PermissionCode("admin:permission:delete")
	@Operation(summary = "删除权限", description = "删除指定权限")
	public Result<Boolean> deletePermission(@RequestBody @Validated PermissionIdQueryDTO queryDTO) {
		Boolean result = permissionService.deletePermission(queryDTO.getId());
		return Result.success(result);
	}

}
