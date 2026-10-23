package com.cloudwaer.common.redis.model;

import lombok.Data;

import java.util.List;

/**
 * @RedisCache SpEL 登录上下文（供 #{#auth.xxx} 使用）。
 * @author cloudwaer
 */
@Data
public class AuthCacheContext {

	private Long userId;

	private String username;

	private Long roleId;

	private List<Long> roleIds;

	private List<String> permissions;

	private String token;

}
