package com.cloudwaer.admin.serve.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudwaer.gateway.refresh")
public class GatewayRefreshProperties {
    private String serviceId;
    private String scheme;
    private String path;
}
