package com.cloudwaer.codegen.serve.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.codegen.api.dto.DatabaseConnectionDTO;
import com.cloudwaer.codegen.serve.entity.DatabaseConnection;
import com.cloudwaer.codegen.serve.service.DatabaseConnectionService;
import com.cloudwaer.codegen.serve.service.impl.DatabaseConnectionServiceImpl;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库连接管理控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/codegen/database-connection")
@Tag(name = "数据库连接管理", description = "数据库连接配置管理接口")
public class DatabaseConnectionController {

	@Autowired
	private DatabaseConnectionService databaseConnectionService;

	@Autowired
	private DatabaseConnectionServiceImpl databaseConnectionServiceImpl;

	/**
	 * 获取所有数据库连接列表
	 */
	@GetMapping("/list")
	@Operation(summary = "获取所有数据库连接列表", description = "获取所有启用的数据库连接配置")
	public Result<List<DatabaseConnectionDTO>> getAllConnections() {
		LambdaQueryWrapper<DatabaseConnection> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DatabaseConnection::getEnabled, true)
			.eq(DatabaseConnection::getStatus, 1)
			.orderByDesc(DatabaseConnection::getCreateTime);
		List<DatabaseConnection> connections = databaseConnectionService.list(wrapper);
		List<DatabaseConnectionDTO> connectionDTOs = connections.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());
		return Result.success(connectionDTOs);
	}

	/**
	 * 分页查询数据库连接列表
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询数据库连接列表", description = "分页获取数据库连接配置，支持关键词搜索")
	public Result<PageResult<DatabaseConnectionDTO>> getConnectionPage(@Validated PageDTO pageDTO) {
		LambdaQueryWrapper<DatabaseConnection> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DatabaseConnection::getStatus, 1);

		if (StringUtils.hasText(pageDTO.getKeyword())) {
			wrapper.and(w -> w.like(DatabaseConnection::getName, pageDTO.getKeyword())
				.or()
				.like(DatabaseConnection::getHost, pageDTO.getKeyword())
				.or()
				.like(DatabaseConnection::getDatabase, pageDTO.getKeyword())
				.or()
				.like(DatabaseConnection::getDbType, pageDTO.getKeyword()));
		}

		wrapper.orderByDesc(DatabaseConnection::getCreateTime);

		Page<DatabaseConnection> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		IPage<DatabaseConnection> iPage = databaseConnectionService.page(page, wrapper);

		List<DatabaseConnectionDTO> records = iPage.getRecords()
			.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());

		return Result.success(new PageResult<>(records, iPage.getTotal(), iPage.getCurrent(), iPage.getSize()));
	}

	/**
	 * 根据ID获取数据库连接详情
	 */
	@GetMapping("/detail")
	@Operation(summary = "根据ID获取数据库连接详情", description = "通过连接ID查询数据库连接详细信息")
	public Result<DatabaseConnectionDTO> getConnectionById(@RequestParam Long id) {
		DatabaseConnection connection = databaseConnectionService.getById(id);
		if (connection == null) {
			return Result.fail("数据库连接配置不存在");
		}
		DatabaseConnectionDTO connectionDTO = convertToDTO(connection);
		// 密码不返回给前端（安全考虑）
		connectionDTO.setPassword("******");
		return Result.success(connectionDTO);
	}

	/**
	 * 测试数据库连接
	 */
	@PostMapping("/test")
	@Operation(summary = "测试数据库连接", description = "测试数据库连接配置是否有效")
	public Result<Boolean> testConnection(@RequestBody @Validated DatabaseConnectionDTO connectionDTO) {
		try {
			// 如果提供了ID且密码为空，从数据库重新获取完整配置（包含密码）
			if (connectionDTO.getId() != null && (connectionDTO.getPassword() == null
					|| connectionDTO.getPassword().isEmpty() || connectionDTO.getPassword().equals("******"))) {
				DatabaseConnection connection = databaseConnectionService.getById(connectionDTO.getId());
				if (connection != null) {
					// 使用数据库中的密码替换
					connectionDTO.setPassword(connection.getPassword());
				}
			}

			Boolean result = databaseConnectionService.testConnection(connectionDTO);
			return Result.success(result);
		}
		catch (Exception e) {
			return Result.fail("测试连接失败: " + e.getMessage());
		}
	}

	/**
	 * 新增数据库连接配置
	 */
	@PostMapping("/save")
	@Operation(summary = "新增数据库连接配置", description = "创建新的数据库连接配置")
	public Result<Boolean> saveConnection(@RequestBody @Validated DatabaseConnectionDTO connectionDTO) {
		try {
			Boolean result = databaseConnectionServiceImpl.saveOrUpdateConnection(connectionDTO);
			return Result.success(result);
		}
		catch (Exception e) {
			return Result.fail("保存失败: " + e.getMessage());
		}
	}

	/**
	 * 更新数据库连接配置
	 */
	@PutMapping("/update")
	@Operation(summary = "更新数据库连接配置", description = "更新数据库连接配置信息")
	public Result<Boolean> updateConnection(@RequestBody @Validated DatabaseConnectionDTO connectionDTO) {
		try {
			if (connectionDTO.getId() == null) {
				return Result.fail("连接ID不能为空");
			}
			Boolean result = databaseConnectionServiceImpl.saveOrUpdateConnection(connectionDTO);
			return Result.success(result);
		}
		catch (Exception e) {
			return Result.fail("更新失败: " + e.getMessage());
		}
	}

	/**
	 * 删除数据库连接配置
	 */
	@DeleteMapping("/delete")
	@Operation(summary = "删除数据库连接配置", description = "删除指定数据库连接配置")
	public Result<Boolean> deleteConnection(@RequestParam Long id) {
		Boolean result = databaseConnectionService.removeById(id);
		return Result.success(result);
	}

	/**
	 * 启用/禁用数据库连接配置
	 */
	@PutMapping("/toggle-enabled")
	@Operation(summary = "启用/禁用数据库连接配置", description = "切换数据库连接配置的启用状态")
	public Result<Boolean> toggleEnabled(@RequestParam Long id, @RequestParam Boolean enabled) {
		DatabaseConnection connection = databaseConnectionService.getById(id);
		if (connection == null) {
			return Result.fail("数据库连接配置不存在");
		}
		connection.setEnabled(enabled);
		Boolean result = databaseConnectionService.updateById(connection);
		return Result.success(result);
	}

	/**
	 * 将实体转换为DTO
	 */
	private DatabaseConnectionDTO convertToDTO(DatabaseConnection connection) {
		DatabaseConnectionDTO connectionDTO = new DatabaseConnectionDTO();
		BeanUtils.copyProperties(connection, connectionDTO);
		// 密码不返回给前端（安全考虑）
		connectionDTO.setPassword(null);
		return connectionDTO;
	}

}
