package com.cloudwaer.codegen.serve.util;

import com.cloudwaer.codegen.api.dto.DatabaseConnectionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class DatabaseConnectionUtil {

	/**
	 * 获取数据库连接
	 * @param connectionDTO 连接配置
	 * @return 数据库连接
	 * @throws SQLException SQL异常
	 */
	public Connection getConnection(DatabaseConnectionDTO connectionDTO) throws SQLException {
		if (connectionDTO == null) {
			throw new SQLException("数据库连接配置不能为空");
		}

		String url = buildConnectionUrl(connectionDTO);
		String driverClass = getDriverClass(connectionDTO.getDbType());
		String username = connectionDTO.getUsername();
		String password = connectionDTO.getPassword();

		// 检查必要的参数
		if (username == null || username.trim().isEmpty()) {
			throw new SQLException("用户名不能为空");
		}

		// 密码处理：如果为null，设置为空字符串；如果为空字符串，保持为空字符串
		if (password == null) {
			log.warn("密码字段为null，将使用空密码进行连接");
			password = "";
		}

		// 记录连接信息（不记录实际密码）
		log.info("尝试连接数据库: url={}, username={}, password={}", url, username, password.isEmpty() ? "[空密码]" : "[已设置密码]");

		try {
			// 加载驱动
			Class.forName(driverClass);

			// 创建连接
			Connection connection = DriverManager.getConnection(url, username, password);
			log.info("数据库连接成功: url={}, username={}", url, username);
			return connection;
		}
		catch (ClassNotFoundException e) {
			log.error("数据库驱动类未找到: {}", driverClass, e);
			throw new SQLException("数据库驱动类未找到: " + driverClass, e);
		}
		catch (SQLException e) {
			log.error("数据库连接失败: url={}, username={}, error={}", url, username, e.getMessage());
			throw e;
		}
	}

	/**
	 * 构建数据库连接URL
	 * @param connectionDTO 连接配置
	 * @return 连接URL
	 */
	private String buildConnectionUrl(DatabaseConnectionDTO connectionDTO) {
		// 如果提供了URL，直接使用
		if (connectionDTO.getUrl() != null && !connectionDTO.getUrl().isEmpty()) {
			return connectionDTO.getUrl();
		}

		// 根据数据库类型构建URL
		String dbType = connectionDTO.getDbType().toLowerCase();
		String host = connectionDTO.getHost();
		Integer port = connectionDTO.getPort();
		String database = connectionDTO.getDatabase();

		switch (dbType) {
			case "mysql":
				return String.format(
						"jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
						host, port, database);
			case "postgresql":
				return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
			case "oracle":
				return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
			case "sqlserver":
				return String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, database);
			default:
				throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
		}
	}

	/**
	 * 获取数据库驱动类名
	 * @param dbType 数据库类型
	 * @return 驱动类名
	 */
	private String getDriverClass(String dbType) {
		if (dbType == null) {
			throw new IllegalArgumentException("数据库类型不能为空");
		}

		String type = dbType.toLowerCase();
		switch (type) {
			case "mysql":
				return "com.mysql.cj.jdbc.Driver";
			case "postgresql":
				return "org.postgresql.Driver";
			case "oracle":
				return "oracle.jdbc.driver.OracleDriver";
			case "sqlserver":
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			default:
				throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
		}
	}

}
