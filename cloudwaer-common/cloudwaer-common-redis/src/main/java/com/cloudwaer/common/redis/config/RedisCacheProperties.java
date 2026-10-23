package com.cloudwaer.common.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 缓存模块配置。
 *
 * @author cloudwaer
 */
@Data
@ConfigurationProperties(prefix = "cloudwaer.redis.cache")
public class RedisCacheProperties {

	/**
	 * 是否启用 @RedisCache 方法缓存
	 */
	private boolean enabled = true;

	/**
	 * 默认 key 前缀（注解未覆盖时）
	 */
	private String defaultPrefix = "cloudwaer:cache:";

	/**
	 * 默认过期秒数（注解未覆盖时），24 小时
	 */
	private long defaultTimeSeconds = 86400L;

}
