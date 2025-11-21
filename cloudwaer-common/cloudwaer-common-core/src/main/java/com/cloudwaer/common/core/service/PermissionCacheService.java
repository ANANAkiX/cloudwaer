package com.cloudwaer.common.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存服务
 * 将权限信息缓存到Redis，格式：Map<(GET/PUT/DELETE/POST) + 接口地址, 权限代码>
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class PermissionCacheService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    /**
     * Redis Key前缀
     */
    private static final String PERMISSION_CACHE_KEY = "cloudwaer:permission:cache";

    /**
     * 缓存权限映射到Redis
     *
     * @param permissionMap 权限映射，Key格式：GET /api/user/list，Value：权限代码
     */
    public void cachePermissions(Map<String, String> permissionMap) {
        if (redisTemplate == null) {
            log.warn("Redis未配置，无法缓存权限信息");
            return;
        }

        if (permissionMap == null || permissionMap.isEmpty()) {
            log.warn("权限映射为空，无法缓存");
            return;
        }

        try {
            // 删除旧的缓存
            redisTemplate.delete(PERMISSION_CACHE_KEY);

            // 将权限映射存储到Redis Hash中
            redisTemplate.opsForHash().putAll(PERMISSION_CACHE_KEY, new HashMap<>(permissionMap));

            // 设置过期时间为7天（权限信息一般不会频繁变化）
            redisTemplate.expire(PERMISSION_CACHE_KEY, 7, TimeUnit.DAYS);

            log.info("权限缓存成功: 缓存了 {} 个权限映射", permissionMap.size());
        } catch (Exception e) {
            log.error("缓存权限信息失败", e);
        }
    }

    /**
     * 从Redis获取权限代码
     *
     * @param method 请求方法（GET, POST, PUT, DELETE）
     * @param path   接口路径
     * @return 权限代码，如果不存在则返回null
     */
    public String getPermissionCode(String method, String path) {
        if (redisTemplate == null || !StringUtils.hasText(method) || !StringUtils.hasText(path)) {
            return null;
        }

        try {
            String key = buildPermissionKey(method, path);
            Object permissionCode = redisTemplate.opsForHash().get(PERMISSION_CACHE_KEY, key);
            return permissionCode != null ? permissionCode.toString() : null;
        } catch (Exception e) {
            log.error("从Redis获取权限代码失败: method={}, path={}", method, path, e);
            return null;
        }
    }

    /**
     * 构建权限Key
     *
     * @param method 请求方法
     * @param path   接口路径
     * @return 权限Key，格式：GET /api/user/list
     */
    public String buildPermissionKey(String method, String path) {
        if (!StringUtils.hasText(method) || !StringUtils.hasText(path)) {
            return null;
        }
        // 规范化路径（去除末尾的斜杠，统一格式）
        String normalizedPath = path.trim();
        if (normalizedPath.endsWith("/") && normalizedPath.length() > 1) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return method.toUpperCase() + " " + normalizedPath;
    }

    /**
     * 清除权限缓存
     */
    public void clearCache() {
        if (redisTemplate != null) {
            try {
                redisTemplate.delete(PERMISSION_CACHE_KEY);
                log.info("权限缓存已清除");
            } catch (Exception e) {
                log.error("清除权限缓存失败", e);
            }
        }
    }
}

