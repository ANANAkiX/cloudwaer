package com.cloudwaer.codegen.serve.service.impl;

import com.cloudwaer.codegen.api.dto.ColumnMetadataDTO;
import com.cloudwaer.codegen.api.dto.TableMetadataDTO;
import com.cloudwaer.codegen.serve.service.DatabaseConnectionService;
import com.cloudwaer.codegen.serve.service.MetadataService;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据库元数据服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class MetadataServiceImpl implements MetadataService {

	@Autowired
	private DatabaseConnectionService databaseConnectionService;

	@Override
	public List<String> getTableList(Long connectionId) {
		Connection connection = null;
		try {
			connection = databaseConnectionService.getConnection(connectionId);
			DatabaseMetaData metaData = connection.getMetaData();
			String catalog = connection.getCatalog();
			String schema = getSchema(connection);

			List<String> tableNames = new ArrayList<>();
			try (ResultSet tables = metaData.getTables(catalog, schema, null, new String[] { "TABLE", "VIEW" })) {
				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					tableNames.add(tableName);
				}
			}
			return tableNames;
		}
		catch (SQLException e) {
			log.error("获取数据库表列表失败", e);
			throw new BusinessException("获取数据库表列表失败: " + e.getMessage());
		}
		finally {
			if (connection != null) {
				databaseConnectionService.closeConnection(connection);
			}
		}
	}

	@Override
	public TableMetadataDTO getTableMetadata(Long connectionId, String tableName) {
		Connection connection = null;
		try {
			connection = databaseConnectionService.getConnection(connectionId);
			return getTableMetadata(connection, tableName);
		}
		catch (SQLException e) {
			log.error("获取表结构元数据失败: tableName={}", tableName, e);
			throw new BusinessException("获取表结构元数据失败: " + e.getMessage());
		}
		finally {
			if (connection != null) {
				databaseConnectionService.closeConnection(connection);
			}
		}
	}

	@Override
	public List<TableMetadataDTO> getTableMetadataList(Long connectionId, List<String> tableNames) {
		Connection connection = null;
		try {
			connection = databaseConnectionService.getConnection(connectionId);
			List<TableMetadataDTO> result = new ArrayList<>();
			for (String tableName : tableNames) {
				try {
					TableMetadataDTO metadata = getTableMetadata(connection, tableName);
					result.add(metadata);
				}
				catch (Exception e) {
					log.warn("获取表结构元数据失败: tableName={}", tableName, e);
				}
			}
			return result;
		}
		finally {
			if (connection != null) {
				databaseConnectionService.closeConnection(connection);
			}
		}
	}

	/**
	 * 获取表结构元数据
	 * @param connection 数据库连接
	 * @param tableName 表名
	 * @return 表结构元数据
	 */
	private TableMetadataDTO getTableMetadata(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		String catalog = connection.getCatalog();
		String schema = getSchema(connection);

		TableMetadataDTO tableMetadata = new TableMetadataDTO();
		tableMetadata.setTableName(tableName);
		tableMetadata.setDatabaseName(catalog);

		// 获取表基本信息
		try (ResultSet tables = metaData.getTables(catalog, schema, tableName, null)) {
			if (tables.next()) {
				tableMetadata.setTableType(tables.getString("TABLE_TYPE"));
				tableMetadata.setTableComment(tables.getString("REMARKS"));
			}
		}

		// 获取主键信息
		Set<String> primaryKeys = new HashSet<>();
		try (ResultSet pkResultSet = metaData.getPrimaryKeys(catalog, schema, tableName)) {
			while (pkResultSet.next()) {
				String pkColumnName = pkResultSet.getString("COLUMN_NAME");
				primaryKeys.add(pkColumnName);
			}
		}
		tableMetadata.setPrimaryKeys(new ArrayList<>(primaryKeys));

		// 获取列信息
		List<ColumnMetadataDTO> columns = new ArrayList<>();
		try (ResultSet columnsResultSet = metaData.getColumns(catalog, schema, tableName, null)) {
			while (columnsResultSet.next()) {
				ColumnMetadataDTO column = new ColumnMetadataDTO();
				column.setColumnName(columnsResultSet.getString("COLUMN_NAME"));
				column.setDataType(columnsResultSet.getString("TYPE_NAME"));
				column.setJavaType(mapToJavaType(column.getDataType(), columnsResultSet.getInt("DATA_TYPE")));
				column.setColumnComment(columnsResultSet.getString("REMARKS"));
				column.setNullable(columnsResultSet.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
				column.setColumnSize(columnsResultSet.getLong("COLUMN_SIZE"));
				column.setDecimalDigits(columnsResultSet.getInt("DECIMAL_DIGITS"));
				column.setDefaultValue(columnsResultSet.getString("COLUMN_DEF"));
				column.setOrdinalPosition(columnsResultSet.getInt("ORDINAL_POSITION"));

				// 判断是否为主键
				column.setPrimaryKey(primaryKeys.contains(column.getColumnName()));

				// 判断是否自增（MySQL使用AUTO_INCREMENT，其他数据库可能不同）
				String isAutoIncrement = columnsResultSet.getString("IS_AUTOINCREMENT");
				column.setAutoIncrement("YES".equalsIgnoreCase(isAutoIncrement));

				columns.add(column);
			}
		}

		// 按顺序排序
		columns.sort((a, b) -> {
			if (a.getOrdinalPosition() == null)
				return 1;
			if (b.getOrdinalPosition() == null)
				return -1;
			return a.getOrdinalPosition().compareTo(b.getOrdinalPosition());
		});

		tableMetadata.setColumns(columns);

		return tableMetadata;
	}

	/**
	 * 获取数据库Schema（不同数据库可能不同）
	 */
	private String getSchema(Connection connection) throws SQLException {
		String dbProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
		// MySQL通常不需要schema，使用null
		if (dbProductName.contains("mysql")) {
			return null;
		}
		// PostgreSQL和Oracle使用用户名作为schema
		// 这里可以根据实际需求调整
		return connection.getMetaData().getUserName().toUpperCase();
	}

	/**
	 * 将数据库类型映射为Java类型
	 */
	private String mapToJavaType(String typeName, int sqlType) {
		if (typeName == null) {
			return mapSqlTypeToJavaType(sqlType);
		}

		String upperTypeName = typeName.toUpperCase();

		// 根据数据库类型名称判断
		if (upperTypeName.contains("VARCHAR") || upperTypeName.contains("CHAR") || upperTypeName.contains("TEXT")
				|| upperTypeName.contains("CLOB")) {
			return "String";
		}
		else if (upperTypeName.contains("INT") && !upperTypeName.contains("BIGINT")) {
			return "Integer";
		}
		else if (upperTypeName.contains("BIGINT")) {
			return "Long";
		}
		else if (upperTypeName.contains("DECIMAL") || upperTypeName.contains("NUMERIC")) {
			return "java.math.BigDecimal";
		}
		else if (upperTypeName.contains("DOUBLE") || upperTypeName.contains("FLOAT")) {
			return "Double";
		}
		else if (upperTypeName.contains("DATE") || upperTypeName.contains("TIME")
				|| upperTypeName.contains("TIMESTAMP")) {
			return "java.time.LocalDateTime";
		}
		else if (upperTypeName.contains("BOOLEAN")
				|| upperTypeName.contains("BIT") && !upperTypeName.contains("VARBINARY")) {
			return "Boolean";
		}
		else if (upperTypeName.contains("BLOB") || upperTypeName.contains("BINARY")) {
			return "byte[]";
		}

		// 如果无法通过类型名称判断，使用SQL类型映射
		return mapSqlTypeToJavaType(sqlType);
	}

	/**
	 * 根据SQL类型映射为Java类型
	 */
	private String mapSqlTypeToJavaType(int sqlType) {
		switch (sqlType) {
			case Types.VARCHAR:
			case Types.CHAR:
			case Types.LONGVARCHAR:
			case Types.NVARCHAR:
			case Types.NCHAR:
			case Types.LONGNVARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
				return "String";
			case Types.INTEGER:
				return "Integer";
			case Types.BIGINT:
				return "Long";
			case Types.SMALLINT:
			case Types.TINYINT:
				return "Integer";
			case Types.DECIMAL:
			case Types.NUMERIC:
				return "java.math.BigDecimal";
			case Types.DOUBLE:
			case Types.FLOAT:
				return "Double";
			case Types.REAL:
				return "Float";
			case Types.DATE:
				return "java.time.LocalDate";
			case Types.TIME:
				return "java.time.LocalTime";
			case Types.TIMESTAMP:
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return "java.time.LocalDateTime";
			case Types.BOOLEAN:
			case Types.BIT:
				return "Boolean";
			case Types.BLOB:
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return "byte[]";
			default:
				return "String";
		}
	}

}
