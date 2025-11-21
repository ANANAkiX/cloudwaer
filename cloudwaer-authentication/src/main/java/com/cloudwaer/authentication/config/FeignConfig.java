package com.cloudwaer.authentication.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置
 *
 * @author cloudwaer
 */
@Configuration
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class FeignConfig {
}




