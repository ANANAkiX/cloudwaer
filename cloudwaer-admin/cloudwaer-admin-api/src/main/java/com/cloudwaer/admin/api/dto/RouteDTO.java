package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由DTO（用于前端动态路由）
 *
 * @author cloudwaer
 */
@Data
public class RouteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 路由路径
	 */
	private String path;

	/**
	 * 组件路径
	 */
	private String component;

	/**
	 * 路由名称
	 */
	private String name;

	/**
	 * 路由元信息
	 */
	private RouteMeta meta;

	/**
	 * 父权限ID（序列化为字符串，避免前端精度丢失）
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long parentId;

	/**
	 * API请求地址
	 */
	private String apiUrl;

	/**
	 * 子路由
	 */
	private List<RouteDTO> children;

}
