package com.cloudwaer.codegen.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单配置实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_form_config")
public class FormConfig extends BaseEntity {

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 数据库连接ID
	 */
	private Long connectionId;

	/**
	 * 模块名称
	 */
	private String moduleName;

	/**
	 * 包名
	 */
	private String packageName;

	/**
	 * 实体类名称
	 */
	private String entityName;

	/**
	 * 实体类注释
	 */
	private String entityComment;

	/**
	 * 作者
	 */
	private String author;

	/**
	 * 表单字段配置（JSON格式）
	 */
	private String formFields;

	/**
	 * 查询字段列表（JSON格式）
	 */
	private String queryFields;

	/**
	 * 主键字段名
	 */
	private String primaryKeyField;

	/**
	 * 是否启用分页查询
	 */
	private Boolean enablePagination;

	/**
	 * 是否启用逻辑删除
	 */
	private Boolean enableLogicDelete;

}
