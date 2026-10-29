package com.cloudwaer.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 全局 Redis 操作工具（Spring Bean 注入）。
 *
 * <p>
 * 封装 {@link StringRedisTemplate}：字符串 / 对象(JSON) / Set / 过期。Redis 未配置时方法安全降级，不抛异常。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class RedisUtil {

	@Autowired(required = false)
	private StringRedisTemplate stringRedisTemplate;

	@Autowired(required = false)
	private ObjectMapper objectMapper;

	// ---------- 基础：String / 生命周期 ----------

	/**
	 * 发布消息到频道（Redis pub/sub）。Redis 未配置时安全降级。
	 */
	public boolean publish(String channel, String message) {
		if (stringRedisTemplate == null || channel == null) {
			return false;
		}
		try {
			stringRedisTemplate.convertAndSend(channel, message == null ? "" : message);
			return true;
		}
		catch (Exception e) {
			log.error("Redis publish failed: channel={}", channel, e);
			return false;
		}
	}

	/**
	 * Redis 是否可用
	 */
	public boolean isAvailable() {
		return stringRedisTemplate != null;
	}

	/**
	 * 设置字符串值（不过期）
	 */
	public boolean set(String key, String value) {
		if (!check(key, value)) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, value);
			return true;
		}
		catch (Exception e) {
			log.error("Redis set failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 设置字符串值（指定秒过期）
	 */
	public boolean set(String key, String value, long seconds) {
		if (!check(key, value)) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
			return true;
		}
		catch (Exception e) {
			log.error("Redis set failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 设置字符串值（Duration 过期）
	 */
	public boolean set(String key, String value, Duration duration) {
		if (!check(key, value) || duration == null) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, value, duration);
			return true;
		}
		catch (Exception e) {
			log.error("Redis set failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 获取字符串值
	 */
	public String get(String key) {
		if (key == null || !isAvailable()) {
			return null;
		}
		try {
			return stringRedisTemplate.opsForValue().get(key);
		}
		catch (Exception e) {
			log.error("Redis get failed: key={}", key, e);
			return null;
		}
	}

	/**
	 * 删除 key
	 */
	public boolean delete(String key) {
		if (key == null || !isAvailable()) {
			return false;
		}
		try {
			Boolean ok = stringRedisTemplate.delete(key);
			return Boolean.TRUE.equals(ok);
		}
		catch (Exception e) {
			log.error("Redis delete failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 批量删除
	 */
	public long delete(Collection<String> keys) {
		if (keys == null || keys.isEmpty() || !isAvailable()) {
			return 0L;
		}
		try {
			Long n = stringRedisTemplate.delete(keys);
			return n == null ? 0L : n;
		}
		catch (Exception e) {
			log.error("Redis batch delete failed: size={}", keys.size(), e);
			return 0L;
		}
	}

	/**
	 * 是否存在 key
	 */
	public boolean hasKey(String key) {
		if (key == null || !isAvailable()) {
			return false;
		}
		try {
			Boolean b = stringRedisTemplate.hasKey(key);
			return Boolean.TRUE.equals(b);
		}
		catch (Exception e) {
			log.error("Redis hasKey failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 设置过期时间（秒）
	 */
	public boolean expire(String key, long seconds) {
		if (key == null || !isAvailable()) {
			return false;
		}
		try {
			return Boolean.TRUE.equals(stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS));
		}
		catch (Exception e) {
			log.error("Redis expire failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 设置过期时间（Duration）
	 */
	public boolean expire(String key, Duration duration) {
		if (key == null || duration == null || !isAvailable()) {
			return false;
		}
		try {
			return Boolean.TRUE.equals(stringRedisTemplate.expire(key, duration));
		}
		catch (Exception e) {
			log.error("Redis expire failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 剩余过期秒数，无过期或失败返回 -1
	 */
	public long getExpire(String key) {
		if (key == null || !isAvailable()) {
			return -1L;
		}
		try {
			Long v = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
			return v == null ? -1L : v;
		}
		catch (Exception e) {
			log.error("Redis getExpire failed: key={}", key, e);
			return -1L;
		}
	}

	// ---------- 对象（JSON） ----------

	/**
	 * 写入对象（JSON 序列化，不过期）
	 */
	public boolean setObject(String key, Object value) {
		if (!check(key, value)) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, toJson(value));
			return true;
		}
		catch (Exception e) {
			log.error("Redis setObject failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 写入对象（JSON，指定秒过期）
	 */
	public boolean setObject(String key, Object value, long seconds) {
		if (!check(key, value)) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, toJson(value), seconds, TimeUnit.SECONDS);
			return true;
		}
		catch (Exception e) {
			log.error("Redis setObject failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 写入对象（JSON，指定时间单位过期）
	 */
	public boolean setObject(String key, Object value, long time, TimeUnit timeUnit) {
		if (!check(key, value) || timeUnit == null) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, toJson(value), time, timeUnit);
			return true;
		}
		catch (Exception e) {
			log.error("Redis setObject failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 写入对象（JSON，Duration 过期）
	 */
	public boolean setObject(String key, Object value, Duration duration) {
		if (!check(key, value) || duration == null) {
			return false;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, toJson(value), duration);
			return true;
		}
		catch (Exception e) {
			log.error("Redis setObject failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * 读取对象（JSON 反序列化）
	 */
	public <T> T getObject(String key, Class<T> clazz) {
		String json = get(key);
		if (json == null || clazz == null) {
			return null;
		}
		try {
			return mapper().readValue(json, clazz);
		}
		catch (Exception e) {
			log.error("Redis getObject failed: key={}", key, e);
			return null;
		}
	}

	/**
	 * 读取对象（泛型集合等，如 List / Map）
	 */
	public <T> T getObject(String key, TypeReference<T> typeReference) {
		String json = get(key);
		if (json == null || typeReference == null) {
			return null;
		}
		try {
			return mapper().readValue(json, typeReference);
		}
		catch (Exception e) {
			log.error("Redis getObject failed: key={}", key, e);
			return null;
		}
	}

	// ---------- Set ----------

	/**
	 * Set 添加成员
	 */
	public long setAdd(String key, String... values) {
		if (key == null || values == null || values.length == 0 || !isAvailable()) {
			return 0L;
		}
		try {
			Long n = stringRedisTemplate.opsForSet().add(key, values);
			return n == null ? 0L : n;
		}
		catch (Exception e) {
			log.error("Redis setAdd failed: key={}", key, e);
			return 0L;
		}
	}

	/**
	 * Set 全部成员
	 */
	public Set<String> setMembers(String key) {
		if (key == null || !isAvailable()) {
			return null;
		}
		try {
			return stringRedisTemplate.opsForSet().members(key);
		}
		catch (Exception e) {
			log.error("Redis setMembers failed: key={}", key, e);
			return null;
		}
	}

	/**
	 * Set 移除成员
	 */
	public long setRemove(String key, String... values) {
		if (key == null || values == null || values.length == 0 || !isAvailable()) {
			return 0L;
		}
		try {
			Long n = stringRedisTemplate.opsForSet().remove(key, values);
			return n == null ? 0L : n;
		}
		catch (Exception e) {
			log.error("Redis setRemove failed: key={}", key, e);
			return 0L;
		}
	}

	// ---------- List ----------

	/**
	 * List 左侧推入
	 */
	public long listLeftPush(String key, String value) {
		if (key == null || value == null || !isAvailable()) {
			return 0L;
		}
		try {
			Long n = stringRedisTemplate.opsForList().leftPush(key, value);
			return n == null ? 0L : n;
		}
		catch (Exception e) {
			log.error("Redis listLeftPush failed: key={}", key, e);
			return 0L;
		}
	}

	/**
	 * List 右侧弹出
	 */
	public String listRightPop(String key) {
		if (key == null || !isAvailable()) {
			return null;
		}
		try {
			return stringRedisTemplate.opsForList().rightPop(key);
		}
		catch (Exception e) {
			log.error("Redis listRightPop failed: key={}", key, e);
			return null;
		}
	}

	/**
	 * List 范围
	 */
	public List<String> listRange(String key, long start, long end) {
		if (key == null || !isAvailable()) {
			return null;
		}
		try {
			return stringRedisTemplate.opsForList().range(key, start, end);
		}
		catch (Exception e) {
			log.error("Redis listRange failed: key={}", key, e);
			return null;
		}
	}

	// ---------- Hash ----------

	/**
	 * Hash 写入字段
	 */
	public boolean hashPut(String key, String hashKey, String value) {
		if (key == null || hashKey == null || value == null || !isAvailable()) {
			return false;
		}
		try {
			stringRedisTemplate.opsForHash().put(key, hashKey, value);
			return true;
		}
		catch (Exception e) {
			log.error("Redis hashPut failed: key={}", key, e);
			return false;
		}
	}

	/**
	 * Hash 读取字段
	 */
	public String hashGet(String key, String hashKey) {
		if (key == null || hashKey == null || !isAvailable()) {
			return null;
		}
		try {
			Object v = stringRedisTemplate.opsForHash().get(key, hashKey);
			return v == null ? null : String.valueOf(v);
		}
		catch (Exception e) {
			log.error("Redis hashGet failed: key={}", key, e);
			return null;
		}
	}

	/**
	 * Hash 删除字段
	 */
	public long hashDelete(String key, Object... hashKeys) {
		if (key == null || hashKeys == null || hashKeys.length == 0 || !isAvailable()) {
			return 0L;
		}
		try {
			Long n = stringRedisTemplate.opsForHash().delete(key, hashKeys);
			return n == null ? 0L : n;
		}
		catch (Exception e) {
			log.error("Redis hashDelete failed: key={}", key, e);
			return 0L;
		}
	}

	// ---------- internal ----------

	private boolean check(String key, Object value) {
		if (key == null || value == null) {
			return false;
		}
		return isAvailable();
	}

	private String toJson(Object value) throws Exception {
		return mapper().writeValueAsString(value);
	}

	private ObjectMapper mapper() {
		if (objectMapper == null) {
			return new ObjectMapper();
		}
		return objectMapper;
	}

}
