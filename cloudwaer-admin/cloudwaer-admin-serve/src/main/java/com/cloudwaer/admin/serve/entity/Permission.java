package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

	/**
	 * 权限名称
	 */
	private String permissionName;

	/**
	 * 权限编码
	 */
	private String permissionCode;

	/**
	 * 权限类型（1-菜单，2-页面，3-操作）
	 */
	private Integer permissionType;

	/**
	 * 路由路径（菜单权限需要）
	 */
	private String routePath;

	/**
	 * 父权限ID
	 */
	private Long parentId;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 权限描述
	 */
	private String description;

	/**
	 * API请求地址
	 */
	private String apiUrl;

	/**
	 * 请求类型（GET, POST, PUT, DELETE）
	 */
	private String httpMethod;

}
