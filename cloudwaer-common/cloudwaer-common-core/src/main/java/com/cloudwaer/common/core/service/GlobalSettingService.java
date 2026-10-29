package com.cloudwaer.common.core.service;

import com.cloudwaer.common.core.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全局动态设置服务（读取侧）。
 *
 * <p>
 * 发布侧（admin）把「已保存且生效」的键值打包写入 Redis 快照并广播；本服务在各实例内存中维护同一份快照 （启动加载 + 收到广播刷新，无 TTL
 * 轮询），读取顺序：<b>快照值 → 调用方传入的 yml/代码兜底值</b>。 快照无该键（或值为空）即视为「未覆盖」，自动回退配置文件，无需迁移即保持原行为。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class GlobalSettingService {

	/**
	 * 已发布设置快照的 Redis key（JSON 对象：key -&gt; 字符串值）
	 */
	public static final String SNAPSHOT_KEY = "sys:setting:snapshot";

	/**
	 * 设置发布通知频道（消息体为发布时间戳，收到后重新加载快照）
	 */
	public static final String NOTIFY_CHANNEL = "sys:setting:notify";

	/**
	 * 快照内「已启用第三方登录（脱敏）」的派生键，供登录页渲染按钮
	 */
	public static final String OAUTH_PUBLIC_KEY = "oauth.public-list";

	/**
	 * 快照发布时间戳的派生键
	 */
	public static final String PUBLISH_TIME_KEY = "meta.publish-time";

	// ---- 已接线的配置键（与 sys_setting 种子 / admin 注册表一致）----

	public static final String KEY_CAPTCHA_ENABLED = "captcha.enabled";

	public static final String KEY_CAPTCHA_EXPIRE = "captcha.expire-seconds";

	public static final String KEY_JWT_EXPIRATION = "jwt.expiration";

	public static final String KEY_JWT_ALLOW_MULTI = "jwt.allow-multiple-login";

	public static final String KEY_REGISTER_ENABLED = "auth.register.enabled";

	public static final String KEY_CAPTCHA_LENGTH = "captcha.length";

	public static final String KEY_CAPTCHA_WIDTH = "captcha.width";

	public static final String KEY_CAPTCHA_HEIGHT = "captcha.height";

	public static final String KEY_LOG_ENABLED = "log.enabled";

	public static final String KEY_LOG_EXCLUDE_PATHS = "log.exclude-paths";

	public static final String KEY_API_DOC_ENABLED = "api-doc.enabled";

	public static final String KEY_REDIS_CACHE_ENABLED = "redis.cache.enabled";

	public static final String KEY_REDIS_CACHE_DEFAULT_SECONDS = "redis.cache.default-seconds";

	private final AtomicReference<Map<String, String>> snapshot = new AtomicReference<>(Collections.emptyMap());

	@Autowired(required = false)
	private RedisUtil redisUtil;

	@Autowired(required = false)
	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		refresh();
	}

	/**
	 * 从 Redis 重新加载已发布快照到本地（启动、收到发布广播、手动刷新时调用）。
	 * @return 是否加载成功（Redis 不可用返回 false，保留当前快照）
	 */
	public synchronized boolean refresh() {
		if (redisUtil == null || !redisUtil.isAvailable()) {
			log.debug("GlobalSetting refresh skipped: Redis unavailable");
			return false;
		}
		try {
			String json = redisUtil.get(SNAPSHOT_KEY);
			if (!StringUtils.hasText(json)) {
				snapshot.set(Collections.emptyMap());
				return true;
			}
			Map<String, String> map = getObjectMapper().readValue(json, new TypeReference<Map<String, String>>() {
			});
			snapshot.set(map == null ? Collections.emptyMap() : map);
			log.info("GlobalSetting 快照已加载: keys={}", snapshot.get().size());
			return true;
		}
		catch (Exception e) {
			log.error("GlobalSetting 快照加载失败", e);
			return false;
		}
	}

	/**
	 * 发布设置：写入 Redis 快照并广播各实例立即刷新（发布节点自身也同步刷新）。
	 * @param values 已生效的键值（键值均可为空集合，表示全部回退兜底）
	 * @return 是否发布成功
	 */
	public synchronized boolean publish(Map<String, String> values) {
		Map<String, String> map = values == null ? new HashMap<>() : new HashMap<>(values);
		map.put(PUBLISH_TIME_KEY, String.valueOf(System.currentTimeMillis()));
		String json;
		try {
			json = getObjectMapper().writeValueAsString(map);
		}
		catch (Exception e) {
			log.error("GlobalSetting 快照序列化失败", e);
			return false;
		}
		snapshot.set(Collections.unmodifiableMap(map));
		boolean ok = redisUtil != null && redisUtil.set(SNAPSHOT_KEY, json);
		if (redisUtil != null) {
			redisUtil.publish(NOTIFY_CHANNEL, map.get(PUBLISH_TIME_KEY));
		}
		log.info("GlobalSetting 已发布: keys={}, redisWritten={}", map.size(), ok);
		return ok;
	}

	/**
	 * 当前本地快照（含 meta.* 派生键）
	 */
	public Map<String, String> currentSnapshot() {
		return snapshot.get();
	}

	/**
	 * 快照中的原始值；未覆盖返回 null
	 */
	public String getRaw(String key) {
		if (key == null) {
			return null;
		}
		String value = snapshot.get().get(key);
		return StringUtils.hasText(value) ? value : null;
	}

	/**
	 * 字符串配置：快照有值用快照，否则用兜底（yml/代码默认）
	 */
	public String getString(String key, String fallback) {
		String value = getRaw(key);
		return value != null ? value : fallback;
	}

	/**
	 * 布尔配置：true/false/1/0/yes/no/on/off；解析失败回退兜底值
	 */
	public boolean getBool(String key, boolean fallback) {
		String value = getRaw(key);
		if (value == null) {
			return fallback;
		}
		String v = value.trim().toLowerCase();
		if ("true".equals(v) || "1".equals(v) || "yes".equals(v) || "on".equals(v)) {
			return true;
		}
		if ("false".equals(v) || "0".equals(v) || "no".equals(v) || "off".equals(v)) {
			return false;
		}
		log.warn("GlobalSetting 布尔值解析失败，使用兜底: key={}, value={}", key, value);
		return fallback;
	}

	/**
	 * 整型配置；解析失败回退兜底值
	 */
	public int getInt(String key, int fallback) {
		return (int) getLong(key, fallback);
	}

	/**
	 * 长整型配置；解析失败回退兜底值
	 */
	public long getLong(String key, long fallback) {
		String value = getRaw(key);
		if (value == null) {
			return fallback;
		}
		try {
			return Long.parseLong(value.trim());
		}
		catch (NumberFormatException e) {
			log.warn("GlobalSetting 数值解析失败，使用兜底: key={}, value={}", key, value);
			return fallback;
		}
	}

	/**
	 * JSON 配置；解析失败回退兜底值
	 */
	public <T> T getJson(String key, TypeReference<T> type, T fallback) {
		String value = getRaw(key);
		if (value == null) {
			return fallback;
		}
		try {
			return getObjectMapper().readValue(value, type);
		}
		catch (Exception e) {
			log.warn("GlobalSetting JSON 解析失败，使用兜底: key={}", key, e);
			return fallback;
		}
	}

	private ObjectMapper getObjectMapper() {
		return objectMapper != null ? objectMapper : new ObjectMapper();
	}

}
