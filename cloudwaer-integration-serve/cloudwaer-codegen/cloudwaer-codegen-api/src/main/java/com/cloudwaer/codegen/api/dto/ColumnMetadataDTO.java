package com.cloudwaer.codegen.api.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 数据库列元数据DTO
 *
 * @author cloudwaer
 */
@Data
public class ColumnMetadataDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 列名
	 */
	private String columnName;

	/**
	 * 数据类型（如：VARCHAR, INT, BIGINT等）
	 */
	private String dataType;

	/**
	 * Java类型（如：String, Integer, Long等）
	 */
	private String javaType;

	/**
	 * 列注释
	 */
	private String columnComment;

	/**
	 * 是否为主键
	 */
	private Boolean primaryKey;

	/**
	 * 是否可为空
	 */
	private Boolean nullable;

	/**
	 * 是否自增
	 */
	private Boolean autoIncrement;

	/**
	 * 默认值
	 */
	private String defaultValue;

	/**
	 * 字段长度
	 */
	private Long columnSize;

	/**
	 * 小数位数（用于DECIMAL类型）
	 */
	private Integer decimalDigits;

	/**
	 * 字段在表中的顺序
	 */
	private Integer ordinalPosition;

}
