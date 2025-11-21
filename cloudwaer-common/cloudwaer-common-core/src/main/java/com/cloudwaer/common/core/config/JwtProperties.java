package com.cloudwaer.common.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT配置属性
 *
 * @author cloudwaer
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits";

    /**
     * Token过期时间（毫秒），默认24小时
     */
    private Long expiration = 86400000L;

    /**
     * 是否允许多处登录
     * true: 允许多处登录，同一用户可以在多个设备/浏览器登录
     * false: 只允许一处登录，新登录会踢出旧登录
     */
    private boolean allowMultipleLogin = true;

    /**
     * Redis中Token存储的Key前缀
     */
    private String redisKeyPrefix = "cloudwaer:jwt:token:";

    /**
     * Redis中用户Token列表的Key前缀（用于支持单点登录时踢出旧Token）
     */
    private String userTokenListKeyPrefix = "cloudwaer:jwt:user:tokens:";
}

