package com.cloudwaer.common.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 *
 * @author cloudwaer
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

}
