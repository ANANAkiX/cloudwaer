package com.cloudwaer.common.redis;

import com.cloudwaer.common.core.util.RedisUtil;
import com.cloudwaer.common.redis.aspect.RedisCacheAspect;
import com.cloudwaer.common.redis.config.RedisCacheProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 方法缓存自动配置。
 *
 * <p>
 * 依赖 classpath 上存在 Redis（StringRedisTemplate）；开关：{@code cloudwaer.redis.cache.enabled}（默认
 * true）。
 * </p>
 *
 * @author cloudwaer
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@ConditionalOnClass({ StringRedisTemplate.class, RedisUtil.class })
@EnableConfigurationProperties(RedisCacheProperties.class)
@ConditionalOnProperty(prefix = "cloudwaer.redis.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RedisCacheAutoConfiguration {

	@Bean
	@ConditionalOnBean(StringRedisTemplate.class)
	@ConditionalOnMissingBean
	public RedisCacheAspect redisCacheAspect(RedisUtil redisUtil, ObjectProvider<ObjectMapper> objectMapperProvider) {
		return new RedisCacheAspect(redisUtil, objectMapperProvider);
	}

}
