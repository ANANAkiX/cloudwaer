package com.cloudwaer.common.core.constant;

/**
 * 公共常量类
 *
 * @author cloudwaer
 */
public class CommonConstants {

	/**
	 * 状态：删除
	 */
	public static final Integer STATUS_DELETE = 0;

	/**
	 * 状态：有效
	 */
	public static final Integer STATUS_VALID = 1;

	/**
	 * 状态：无效
	 */
	public static final Integer STATUS_INVALID = 2;

	/**
	 * 默认页码
	 */
	public static final Integer DEFAULT_PAGE_NUM = 1;

	/**
	 * 默认每页数量
	 */
	public static final Integer DEFAULT_PAGE_SIZE = 10;

	/**
	 * Token前缀
	 */
	public static final String TOKEN_PREFIX = "Bearer ";

	/**
	 * Token请求头
	 */
	public static final String TOKEN_HEADER = "Authorization";

	/**
	 * 用户信息Redis Key前缀
	 */
	public static final String REDIS_USER_PREFIX = "cloudwaer:user:";

	/**
	 * 权限信息Redis Key前缀
	 */
	public static final String REDIS_PERMISSION_PREFIX = "cloudwaer:permission:";

	/**
	 * 客户端标识请求头（Token 与客户端绑定校验）
	 */
	public static final String CLIENT_ID_HEADER = "X-Client-Id";

	/**
	 * 客户端：管理后台
	 */
	public static final String CLIENT_ADMIN = "admin";

	/**
	 * 客户端：博客前台
	 */
	public static final String CLIENT_BLOG = "blog";

}
