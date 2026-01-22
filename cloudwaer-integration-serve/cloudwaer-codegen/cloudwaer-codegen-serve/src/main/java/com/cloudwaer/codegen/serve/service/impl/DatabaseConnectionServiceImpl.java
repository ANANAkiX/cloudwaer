package com.cloudwaer.codegen.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.codegen.api.dto.DatabaseConnectionDTO;
import com.cloudwaer.codegen.serve.entity.DatabaseConnection;
import com.cloudwaer.codegen.serve.mapper.DatabaseConnectionMapper;
import com.cloudwaer.codegen.serve.service.DatabaseConnectionService;
import com.cloudwaer.codegen.serve.util.DatabaseConnectionUtil;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;

/**
 * 数据库连接服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class DatabaseConnectionServiceImpl extends ServiceImpl<DatabaseConnectionMapper, DatabaseConnection>
		implements DatabaseConnectionService {

	@Autowired
	private DatabaseConnectionUtil databaseConnectionUtil;

	@Override
	public Boolean testConnection(DatabaseConnectionDTO connectionDTO) {
		try {
			Connection connection = getConnection(connectionDTO);
			if (connection != null && !connection.isClosed()) {
				connection.close();
				return true;
			}
			return false;
		}
		catch (Exception e) {
			log.error("测试数据库连接失败", e);
			throw new BusinessException("测试连接失败: " + e.getMessage());
		}
	}

	@Override
	public Connection getConnection(Long connectionId) {
		DatabaseConnection connection = this.getById(connectionId);
		if (connection == null) {
			throw new BusinessException("数据库连接配置不存在");
		}
		if (Boolean.FALSE.equals(connection.getEnabled())) {
			throw new BusinessException("数据库连接配置未启用");
		}

		DatabaseConnectionDTO connectionDTO = new DatabaseConnectionDTO();
		BeanUtils.copyProperties(connection, connectionDTO);
		return getConnection(connectionDTO);
	}

	@Override
	public Connection getConnection(DatabaseConnectionDTO connectionDTO) {
		try {
			return databaseConnectionUtil.getConnection(connectionDTO);
		}
		catch (Exception e) {
			log.error("获取数据库连接失败", e);
			throw new BusinessException("获取数据库连接失败: " + e.getMessage());
		}
	}

	@Override
	public void closeConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		catch (Exception e) {
			log.error("关闭数据库连接失败", e);
		}
	}

	/**
	 * 保存或更新数据库连接配置
	 * @param connectionDTO 连接配置DTO
	 * @return 是否成功
	 */
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveOrUpdateConnection(DatabaseConnectionDTO connectionDTO) {
		// 先测试连接
		if (!testConnection(connectionDTO)) {
			throw new BusinessException("数据库连接测试失败，请检查配置");
		}

		DatabaseConnection connection = new DatabaseConnection();
		BeanUtils.copyProperties(connectionDTO, connection);

		// 密码加密存储（这里先简单处理，实际应该使用加密算法）

		if (connectionDTO.getId() != null) {
			return this.updateById(connection);
		}
		else {
			// 检查连接名称是否已存在
			LambdaQueryWrapper<DatabaseConnection> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(DatabaseConnection::getName, connectionDTO.getName());
			long count = this.count(wrapper);
			if (count > 0) {
				throw new BusinessException("连接名称已存在");
			}
			return this.save(connection);
		}
	}

}
