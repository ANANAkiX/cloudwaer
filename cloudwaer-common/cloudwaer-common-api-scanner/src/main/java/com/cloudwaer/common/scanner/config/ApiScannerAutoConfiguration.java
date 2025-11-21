package com.cloudwaer.common.scanner.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * API扫描器自动配置
 *
 * @author cloudwaer
 */
@Configuration
@EnableConfigurationProperties(ApiScannerProperties.class)
@ConditionalOnProperty(prefix = "cloudwaer.api-scanner", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.cloudwaer.common.scanner")
public class ApiScannerAutoConfiguration {
}

