package com.cloudwaer.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 动态路由基础配置
 * 可以从cloudwaer.gateway.dynamic-route配置覆盖以下配置
 *
 * @author cloudwaer
 */
@Data
@Component
@ConfigurationProperties(prefix = "cloudwaer.gateway.dynamic-route")
public class DynamicRouteProperties {
    private String adminServiceName = "cloudwaer-admin-serve";
    private String adminScheme = "http";
    private String routeListPath = "/admin/gateway-route/list";
    private String redisRoutesKey = "cloudwaer:gateway:route:cache";
    private long redisCacheSeconds = 86400;
    private long cacheDurationMs = 30000;
}
