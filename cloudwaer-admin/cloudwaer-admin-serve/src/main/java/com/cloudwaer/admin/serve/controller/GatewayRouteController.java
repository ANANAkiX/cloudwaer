package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.admin.api.dto.PermissionIdQueryDTO;
import com.cloudwaer.admin.serve.service.GatewayRouteService;
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
 * 网关路由管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/gateway-route")
@Tag(name = "网关路由管理", description = "网关路由管理接口")
public class GatewayRouteController {

	@Autowired
	private GatewayRouteService gatewayRouteService;

	/**
	 * 获取所有网关路由（用于网关服务加载路由，优先从Redis加载）
	 */
	@GetMapping("/list")
	@Operation(summary = "获取所有网关路由", description = "获取所有有效的网关路由列表（用于网关服务，优先从Redis加载）")
	public Result<List<GatewayRouteDTO>> getAllRoutes() {
		List<GatewayRouteDTO> routes = gatewayRouteService.getAllRoutes();
		return Result.success(routes);
	}

	/**
	 * 分页查询网关路由列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询网关路由", description = "分页查询网关路由列表，支持关键词搜索")
	public Result<PageResult<GatewayRouteDTO>> getRoutesByPage(PageDTO pageDTO) {
		PageResult<GatewayRouteDTO> pageResult = gatewayRouteService.getRoutesByPage(pageDTO);
		return Result.success(pageResult);
	}

	/**
	 * 根据ID获取网关路由
	 */
	@GetMapping("/detail")
	@Operation(summary = "根据ID获取网关路由", description = "通过路由ID查询网关路由详细信息")
	public Result<GatewayRouteDTO> getRouteById(@RequestParam Long id) {
		GatewayRouteDTO route = gatewayRouteService.getRouteById(id);
		return Result.success(route);
	}

	/**
	 * 新增网关路由
	 */
	@PostMapping("/save")
	@Operation(summary = "新增网关路由", description = "创建新的网关路由")
	public Result<Boolean> saveRoute(@RequestBody @Validated GatewayRouteDTO routeDTO) {
		Boolean result = gatewayRouteService.saveRoute(routeDTO);
		return Result.success(result);
	}

	/**
	 * 更新网关路由
	 */
	@PutMapping("/update")
	@Operation(summary = "更新网关路由", description = "更新网关路由信息")
	public Result<Boolean> updateRoute(@RequestBody @Validated GatewayRouteDTO routeDTO) {
		Boolean result = gatewayRouteService.updateRoute(routeDTO);
		return Result.success(result);
	}

	/**
	 * 删除网关路由
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "删除网关路由", description = "删除指定网关路由")
	public Result<Boolean> deleteRoute(@RequestBody @Validated PermissionIdQueryDTO queryDTO) {
		Boolean result = gatewayRouteService.deleteRoute(queryDTO.getId());
		return Result.success(result);
	}

	/**
	 * 刷新网关路由
	 */
	@PostMapping("/refresh")
	@Operation(summary = "刷新网关路由", description = "通知网关服务重新加载路由配置")
	public Result<Boolean> refreshRoutes() {
		Boolean result = gatewayRouteService.refreshRoutes();
		return Result.success(result);
	}

}
