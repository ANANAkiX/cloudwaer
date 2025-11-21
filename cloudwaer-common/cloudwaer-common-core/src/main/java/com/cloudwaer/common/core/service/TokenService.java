package com.cloudwaer.common.core.service;

import com.cloudwaer.common.core.config.JwtProperties;
import com.cloudwaer.common.core.util.JwtUtil;
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
 * Token管理服务（Redis存储）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class TokenService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 生成并存储Token
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param roleIds     角色ID列表
     * @param permissions 权限代码列表
     * @return Token UUID（包装后的Token标识）
     */
    public String generateAndStoreToken(Long userId, String username, List<Long> roleIds, List<String> permissions) {
        if (redisTemplate == null) {
            log.warn("Redis未配置，无法存储Token");
            return null;
        }

        // 生成UUID作为Token标识
        String tokenUuid = UUID.randomUUID().toString().replace("-", "");

        // 构建JWT Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("userId", userId);
        claims.put("tokenUuid", tokenUuid);
        if (roleIds != null && !roleIds.isEmpty()) {
            claims.put("roleIds", roleIds);
            claims.put("roleId", roleIds.get(0)); // 兼容旧代码
        }
        if (permissions != null && !permissions.isEmpty()) {
            claims.put("permissions", permissions);
        }

        // 生成JWT Token
        String jwtToken = jwtUtil.generateToken(claims);

        // 构建存储到Redis的数据
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("jwtToken", jwtToken);
        tokenData.put("userId", userId);
        tokenData.put("username", username);
        tokenData.put("roleIds", roleIds);
        tokenData.put("permissions", permissions);
        tokenData.put("createTime", System.currentTimeMillis());

        try {
            String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
            String userTokenListKey = jwtProperties.getUserTokenListKeyPrefix() + userId;

            // 如果不允许多处登录，清除该用户的所有旧Token
            if (!jwtProperties.isAllowMultipleLogin()) {
                clearUserTokens(userId);
            }

            // 存储Token数据到Redis
            String tokenDataJson = getObjectMapper().writeValueAsString(tokenData);
            long expirationSeconds = jwtProperties.getExpiration() / 1000;
            redisTemplate.opsForValue().set(tokenKey, tokenDataJson, expirationSeconds, TimeUnit.SECONDS);

            // 将Token UUID添加到用户的Token列表中
            redisTemplate.opsForSet().add(userTokenListKey, tokenUuid);
            redisTemplate.expire(userTokenListKey, expirationSeconds, TimeUnit.SECONDS);

            log.info("Token生成并存储成功: userId={}, tokenUuid={}, allowMultipleLogin={}", 
                    userId, tokenUuid, jwtProperties.isAllowMultipleLogin());
            
            return tokenUuid;
        } catch (Exception e) {
            log.error("存储Token失败: userId={}", userId, e);
            return null;
        }
    }

    /**
     * 验证Token是否有效（检查Redis中是否存在）
     *
     * @param tokenUuid Token UUID
     * @return 是否有效
     */
    public boolean validateToken(String tokenUuid) {
        if (redisTemplate == null) {
            log.warn("Redis未配置，无法验证Token");
            return false;
        }

        if (!StringUtils.hasText(tokenUuid)) {
            return false;
        }

        try {
            String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
            return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
        } catch (Exception e) {
            log.error("验证Token失败: tokenUuid={}", tokenUuid, e);
            return false;
        }
    }

    /**
     * 从Token UUID获取JWT Token
     *
     * @param tokenUuid Token UUID
     * @return JWT Token
     */
    public String getJwtToken(String tokenUuid) {
        if (redisTemplate == null || !StringUtils.hasText(tokenUuid)) {
            return null;
        }

        try {
            String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
            String tokenDataJson = redisTemplate.opsForValue().get(tokenKey);
            
            if (StringUtils.hasText(tokenDataJson)) {
                Map<String, Object> tokenData = getObjectMapper().readValue(tokenDataJson, new TypeReference<Map<String, Object>>() {});
                return (String) tokenData.get("jwtToken");
            }
        } catch (Exception e) {
            log.error("获取JWT Token失败: tokenUuid={}", tokenUuid, e);
        }

        return null;
    }

    /**
     * 从Token UUID获取用户信息
     *
     * @param tokenUuid Token UUID
     * @return 用户信息Map
     */
    public Map<String, Object> getTokenData(String tokenUuid) {
        if (redisTemplate == null || !StringUtils.hasText(tokenUuid)) {
            return null;
        }

        try {
            String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
            String tokenDataJson = redisTemplate.opsForValue().get(tokenKey);
            
            if (StringUtils.hasText(tokenDataJson)) {
                return getObjectMapper().readValue(tokenDataJson, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.error("获取Token数据失败: tokenUuid={}", tokenUuid, e);
        }

        return null;
    }

    /**
     * 删除Token（登出）
     *
     * @param tokenUuid Token UUID
     */
    public void deleteToken(String tokenUuid) {
        if (redisTemplate == null || !StringUtils.hasText(tokenUuid)) {
            return;
        }

        try {
            String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
            Map<String, Object> tokenData = getTokenData(tokenUuid);
            
            if (tokenData != null) {
                // 删除Token
                redisTemplate.delete(tokenKey);
                
                // 从用户Token列表中移除
                Long userId = extractLongValue(tokenData.get("userId"));
                if (userId != null) {
                    String userTokenListKey = jwtProperties.getUserTokenListKeyPrefix() + userId;
                    redisTemplate.opsForSet().remove(userTokenListKey, tokenUuid);
                }
                
                log.info("Token删除成功: tokenUuid={}", tokenUuid);
            }
        } catch (Exception e) {
            log.error("删除Token失败: tokenUuid={}", tokenUuid, e);
        }
    }

    /**
     * 清除用户的所有Token（用于单点登录时踢出旧登录）
     *
     * @param userId 用户ID
     */
    public void clearUserTokens(Long userId) {
        if (redisTemplate == null || userId == null) {
            return;
        }

        try {
            String userTokenListKey = jwtProperties.getUserTokenListKeyPrefix() + userId;
            Set<String> tokenUuids = redisTemplate.opsForSet().members(userTokenListKey);
            
            if (tokenUuids != null && !tokenUuids.isEmpty()) {
                for (String tokenUuid : tokenUuids) {
                    String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
                    redisTemplate.delete(tokenKey);
                }
                redisTemplate.delete(userTokenListKey);
                log.info("清除用户所有Token成功: userId={}, tokenCount={}", userId, tokenUuids.size());
            }
        } catch (Exception e) {
            log.error("清除用户Token失败: userId={}", userId, e);
        }
    }

    /**
     * 更新用户Token中的权限信息（不改变Token UUID）
     * 从数据库获取最新权限，更新Redis中的Token数据和JWT Token
     * 支持多处登录：会更新该用户的所有Token（所有登录设备/会话）
     *
     * @param userId 用户ID
     * @param newPermissions 新的权限代码列表（如果为null，则从数据库获取）
     * @return 更新的Token数量
     */
    public int updateUserTokenPermissions(Long userId, List<String> newPermissions) {
        if (redisTemplate == null || userId == null) {
            return 0;
        }

        try {
            String userTokenListKey = jwtProperties.getUserTokenListKeyPrefix() + userId;
            Set<String> tokenUuids = redisTemplate.opsForSet().members(userTokenListKey);
            
            if (tokenUuids == null || tokenUuids.isEmpty()) {
                log.debug("用户没有活跃的Token: userId={}", userId);
                return 0;
            }

            log.info("开始更新用户所有Token权限: userId={}, tokenCount={}, newPermissions={}", 
                    userId, tokenUuids.size(), newPermissions);

            int updateCount = 0;
            int skipCount = 0;
            int errorCount = 0;
            
            for (String tokenUuid : tokenUuids) {
                try {
                    // 获取当前Token数据
                    Map<String, Object> tokenData = getTokenData(tokenUuid);
                    if (tokenData == null) {
                        // Token已过期或不存在，从Set中移除
                        redisTemplate.opsForSet().remove(userTokenListKey, tokenUuid);
                        skipCount++;
                        log.debug("Token已过期，跳过更新: userId={}, tokenUuid={}", userId, tokenUuid);
                        continue;
                    }

                    // 获取用户信息
                    Long tokenUserId = extractLongValue(tokenData.get("userId"));
                    String username = (String) tokenData.get("username");
                    @SuppressWarnings("unchecked")
                    List<Long> roleIds = (List<Long>) tokenData.get("roleIds");
                    
                    if (tokenUserId == null || username == null) {
                        skipCount++;
                        log.warn("Token数据不完整，跳过更新: userId={}, tokenUuid={}", userId, tokenUuid);
                        continue;
                    }

                    // 验证Token属于该用户
                    if (!tokenUserId.equals(userId)) {
                        skipCount++;
                        log.warn("Token用户ID不匹配，跳过更新: expectedUserId={}, tokenUserId={}, tokenUuid={}", 
                                userId, tokenUserId, tokenUuid);
                        continue;
                    }

                    // 使用传入的新权限，如果没有则保持原权限（这种情况不应该发生，但为了安全）
                    List<String> permissions = newPermissions != null ? newPermissions : 
                        (List<String>) tokenData.get("permissions");

                    // 构建新的JWT Claims
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("sub", username);
                    claims.put("userId", tokenUserId);
                    claims.put("tokenUuid", tokenUuid);
                    if (roleIds != null && !roleIds.isEmpty()) {
                        claims.put("roleIds", roleIds);
                        claims.put("roleId", roleIds.get(0)); // 兼容旧代码
                    }
                    if (permissions != null && !permissions.isEmpty()) {
                        claims.put("permissions", permissions);
                    }

                    // 生成新的JWT Token
                    String newJwtToken = jwtUtil.generateToken(claims);

                    // 更新Token数据
                    Map<String, Object> updatedTokenData = new HashMap<>();
                    updatedTokenData.put("jwtToken", newJwtToken);
                    updatedTokenData.put("userId", tokenUserId);
                    updatedTokenData.put("username", username);
                    updatedTokenData.put("roleIds", roleIds);
                    updatedTokenData.put("permissions", permissions);
                    updatedTokenData.put("createTime", tokenData.get("createTime")); // 保持原创建时间

                    // 更新Redis中的Token数据
                    String tokenKey = jwtProperties.getRedisKeyPrefix() + tokenUuid;
                    String tokenDataJson = getObjectMapper().writeValueAsString(updatedTokenData);
                    long expirationSeconds = jwtProperties.getExpiration() / 1000;
                    redisTemplate.opsForValue().set(tokenKey, tokenDataJson, expirationSeconds, TimeUnit.SECONDS);

                    updateCount++;
                    log.debug("更新Token权限成功: userId={}, tokenUuid={}, permissions={}", 
                            tokenUserId, tokenUuid, permissions);
                } catch (Exception e) {
                    errorCount++;
                    log.error("更新Token权限失败: userId={}, tokenUuid={}", userId, tokenUuid, e);
                }
            }

            log.info("更新用户Token权限完成: userId={}, 总Token数={}, 成功更新={}, 跳过={}, 失败={}, permissions={}", 
                    userId, tokenUuids.size(), updateCount, skipCount, errorCount, newPermissions);
            
            return updateCount;
        } catch (Exception e) {
            log.error("更新用户Token权限失败: userId={}", userId, e);
            return 0;
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

    /**
     * 从对象中提取Long值
     * 支持多种类型：Number（Integer、Long等）、String、null
     *
     * @param value 原始值
     * @return Long值，如果无法转换则返回null
     */
    private Long extractLongValue(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("无法将字符串转换为Long: {}", value);
                return null;
            }
        }
        
        log.warn("无法提取Long值，类型不支持: {}, class={}", value, value.getClass().getName());
        return null;
    }
}

