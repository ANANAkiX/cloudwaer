package com.cloudwaer.common.scanner.service;

import com.cloudwaer.common.scanner.dto.ApiInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * API注册服务（使用Redis存储）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class ApiRegistryService {

	private static final String REDIS_KEY_PREFIX = "cloudwaer:api-scanner:services:";

	private static final long TTL_HOURS = 24; // 24小时过期，服务启动时会自动续期

	@Autowired(required = false)
	private StringRedisTemplate redisTemplate;

	@Autowired(required = false)
	private ObjectMapper objectMapper;

	/**
	 * 注册服务的API列表
	 * @param serviceId 服务ID
	 * @param apis API列表
	 */
	public void registerServiceApis(String serviceId, List<ApiInfo> apis) {
		if (redisTemplate == null) {
			log.warn("Redis未配置，无法注册服务API: {}", serviceId);
			return;
		}

		if (!StringUtils.hasText(serviceId)) {
			log.error("服务ID不能为空");
			return;
		}

		try {
			String redisKey = REDIS_KEY_PREFIX + serviceId;
			String jsonValue = getObjectMapper().writeValueAsString(apis);

			// 存储到Redis，设置24小时过期
			redisTemplate.opsForValue().set(redisKey, jsonValue, TTL_HOURS, TimeUnit.HOURS);

			log.info("服务API注册成功: serviceId={}, apiCount={}", serviceId, apis.size());
		}
		catch (Exception e) {
			log.error("注册服务API失败: serviceId={}", serviceId, e);
		}
	}

	/**
	 * 获取所有服务的API列表
	 * @return Map<服务ID, API列表>
	 */
	public Map<String, List<ApiInfo>> getAllServiceApis() {
		Map<String, List<ApiInfo>> result = new HashMap<>();

		if (redisTemplate == null) {
			log.warn("Redis未配置，无法获取服务API列表");
			return result;
		}

		try {
			// 获取所有以 REDIS_KEY_PREFIX 开头的key
			Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

			if (keys == null || keys.isEmpty()) {
				return result;
			}

			ObjectMapper mapper = getObjectMapper();
			for (String key : keys) {
				String serviceId = key.substring(REDIS_KEY_PREFIX.length());
				String jsonValue = redisTemplate.opsForValue().get(key);

				if (StringUtils.hasText(jsonValue)) {
					try {
						List<ApiInfo> apis = mapper.readValue(jsonValue, new TypeReference<List<ApiInfo>>() {
						});
						result.put(serviceId, apis);
					}
					catch (Exception e) {
						log.error("解析服务API失败: serviceId={}", serviceId, e);
					}
				}
			}

			log.debug("获取所有服务API列表成功: serviceCount={}", result.size());
		}
		catch (Exception e) {
			log.error("获取所有服务API列表失败", e);
		}

		return result;
	}

	/**
	 * 获取指定服务的API列表
	 * @param serviceId 服务ID
	 * @return API列表
	 */
	public List<ApiInfo> getServiceApis(String serviceId) {
		if (redisTemplate == null) {
			log.warn("Redis未配置，无法获取服务API: {}", serviceId);
			return new ArrayList<>();
		}

		if (!StringUtils.hasText(serviceId)) {
			return new ArrayList<>();
		}

		try {
			String redisKey = REDIS_KEY_PREFIX + serviceId;
			String jsonValue = redisTemplate.opsForValue().get(redisKey);

			if (StringUtils.hasText(jsonValue)) {
				return getObjectMapper().readValue(jsonValue, new TypeReference<List<ApiInfo>>() {
				});
			}
		}
		catch (Exception e) {
			log.error("获取服务API失败: serviceId={}", serviceId, e);
		}

		return new ArrayList<>();
	}

	/**
	 * 续期服务API（服务启动时调用，保持数据有效）
	 * @param serviceId 服务ID
	 */
	public void renewServiceApis(String serviceId) {
		if (redisTemplate == null) {
			return;
		}

		if (!StringUtils.hasText(serviceId)) {
			return;
		}

		try {
			String redisKey = REDIS_KEY_PREFIX + serviceId;
			if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
				// 如果key存在，续期24小时
				redisTemplate.expire(redisKey, TTL_HOURS, TimeUnit.HOURS);
				log.debug("服务API续期成功: serviceId={}", serviceId);
			}
		}
		catch (Exception e) {
			log.error("续期服务API失败: serviceId={}", serviceId, e);
		}
	}

	/**
	 * 注销服务API（服务关闭时调用）
	 * @param serviceId 服务ID
	 */
	public void unregisterServiceApis(String serviceId) {
		if (redisTemplate == null) {
			return;
		}

		if (!StringUtils.hasText(serviceId)) {
			return;
		}

		try {
			String redisKey = REDIS_KEY_PREFIX + serviceId;
			redisTemplate.delete(redisKey);
			log.info("服务API注销成功: serviceId={}", serviceId);
		}
		catch (Exception e) {
			log.error("注销服务API失败: serviceId={}", serviceId, e);
		}
	}

	/**
	 * 获取ObjectMapper（如果未注入，创建默认实例）
	 */
	private ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			return new ObjectMapper();
		}
		return objectMapper;
	}

}
