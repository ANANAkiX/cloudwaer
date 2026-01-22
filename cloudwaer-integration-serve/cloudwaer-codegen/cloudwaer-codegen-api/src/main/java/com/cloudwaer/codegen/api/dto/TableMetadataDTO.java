package com.cloudwaer.codegen.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 数据库表结构元数据DTO
 *
 * @author cloudwaer
 */
@Data
public class TableMetadataDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 表注释
	 */
	private String tableComment;

	/**
	 * 表类型（TABLE, VIEW等）
	 */
	private String tableType;

	/**
	 * 数据库名称
	 */
	private String databaseName;

	/**
	 * 表的主键列名列表
	 */
	private List<String> primaryKeys;

	/**
	 * 表的列信息列表
	 */
	private List<ColumnMetadataDTO> columns;

}
