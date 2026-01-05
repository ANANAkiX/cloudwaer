package com.cloudwaer.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudwaer.gateway.dynamic-route")
public class DynamicRouteProperties {
    private String adminServiceName;
    private String adminScheme;
    private String routeListPath;
    private String redisRoutesKey;
    private long redisCacheSeconds;
    private long cacheDurationMs;
}
