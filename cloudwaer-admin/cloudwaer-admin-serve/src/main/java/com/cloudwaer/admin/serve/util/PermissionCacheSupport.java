package com.cloudwaer.admin.serve.util;

import com.cloudwaer.common.core.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 用户权限结果缓存清理，与 AdminController @RedisCache key 对齐。
 * <p>
 * key = cloudwaer:cache:permission:user:{userId}
 *
 * @author cloudwaer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCacheSupport {

	public static final String PREFIX = "cloudwaer:cache:permission:user:";

	private final RedisUtil redisUtil;

	/**
	 * 清理单个用户的权限缓存
	 */
	public void evict(Long userId) {
		if (userId == null) {
			return;
		}
		try {
			if (redisUtil != null && redisUtil.isAvailable()) {
				redisUtil.delete(PREFIX + userId);
				log.debug("已清理用户权限缓存: userId={}", userId);
			}
		}
		catch (Exception e) {
			log.warn("清理权限缓存失败 userId={}, err={}", userId, e.getMessage());
		}
	}

	/**
	 * 批量清理
	 */
	public void evictAll(Collection<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		userIds.forEach(this::evict);
	}

}
