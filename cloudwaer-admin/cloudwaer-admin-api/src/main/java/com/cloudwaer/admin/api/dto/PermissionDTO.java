package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 权限DTO
 *
 * @author cloudwaer
 */
@Data
public class PermissionDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 权限ID（序列化为字符串，避免前端精度丢失）
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;

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
	 * 父权限ID（序列化为字符串，避免前端精度丢失）
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
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

	/**
	 * 子权限列表
	 */
	private List<PermissionDTO> children;

}
