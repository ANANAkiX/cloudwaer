package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网关路由实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_gateway_route")
public class GatewayRoute extends BaseEntity {

	/**
	 * 路由ID（唯一标识）
	 */
	private String routeId;

	/**
	 * 路由URI（如：lb://service-name）
	 */
	private String uri;

	/**
	 * 路由断言（JSON格式）
	 */
	private String predicates;

	/**
	 * 路由过滤器（JSON格式）
	 */
	private String filters;

	/**
	 * 路由顺序（数字越小优先级越高）
	 */
	@TableField("`order`")
	private Integer order;

	/**
	 * 路由描述
	 */
	private String description;

}
