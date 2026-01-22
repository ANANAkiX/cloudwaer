package com.cloudwaer.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author cloudwaer
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

	/**
	 * 成功
	 */
	SUCCESS(200, "操作成功"),

	/**
	 * 失败
	 */
	FAIL(500, "操作失败"),

	/**
	 * 未授权
	 */
	UNAUTHORIZED(401, "未授权，请先登录"),

	/**
	 * 无权限
	 */
	FORBIDDEN(403, "无权限访问"),
	/**
	 * 无权限
	 */
	NOT_CONFIGURED(405, "未配置接口权限,请联系管理员"),

	/**
	 * 参数错误
	 */
	PARAM_ERROR(400, "参数错误"),

	/**
	 * 数据不存在
	 */
	NOT_FOUND(404, "数据不存在"),

	/**
	 * 服务未找到
	 */
	NOT_SERVE_FOUND(503, "服务未找到"),

	/**
	 * 服务未找到
	 */
	FLOWABLE_ERROR(503, "审批出错"),

	/**
	 * 用户名或密码错误
	 */
	LOGIN_ERROR_ACCOUNT(1001, "用户名或密码错误"),
	/**
	 * 邮箱或密码错误
	 */
	LOGIN_ERROR_EMAIL(1001, "邮箱或密码错误"),

	/**
	 * Token过期
	 */
	TOKEN_EXPIRED(1002, "Token已过期"),

	/**
	 * Token无效
	 */
	TOKEN_INVALID(1003, "Token无效");

	private final Integer code;

	private final String message;

}
