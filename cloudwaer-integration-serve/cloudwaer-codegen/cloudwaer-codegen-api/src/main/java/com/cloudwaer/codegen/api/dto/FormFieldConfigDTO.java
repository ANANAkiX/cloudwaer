package com.cloudwaer.codegen.api.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 表单字段配置DTO
 *
 * @author cloudwaer
 */
@Data
public class FormFieldConfigDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 字段名称（对应数据库列名）
	 */
	private String fieldName;

	/**
	 * 字段标签（显示名称）
	 */
	private String label;

	/**
	 * 字段类型（input, textarea, select, date, number等）
	 */
	private String fieldType;

	/**
	 * 是否必填
	 */
	private Boolean required;

	/**
	 * 是否在列表中显示
	 */
	private Boolean showInList;

	/**
	 * 是否在表单中显示
	 */
	private Boolean showInForm;

	/**
	 * 是否可搜索
	 */
	private Boolean searchable;

	/**
	 * 列表显示宽度
	 */
	private Integer listWidth;

	/**
	 * 表单占位符
	 */
	private String placeholder;

	/**
	 * 表单验证规则（JSON格式）
	 */
	private String validationRules;

	/**
	 * 字段在表单中的顺序
	 */
	private Integer formOrder;

	/**
	 * 字段在列表中的顺序
	 */
	private Integer listOrder;

	/**
	 * 字段在行中的位置（栅格布局，如：0-23）
	 */
	private Integer gridSpan;

	/**
	 * 所属行（用于布局调整）
	 */
	private Integer rowIndex;

	/**
	 * 字典类型（如果是下拉框，指定字典类型）
	 */
	private String dictType;

	/**
	 * 选项列表（JSON格式，用于下拉框等）
	 */
	private String options;

}
