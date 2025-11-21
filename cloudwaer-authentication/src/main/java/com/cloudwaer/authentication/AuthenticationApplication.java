package com.cloudwaer.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 *
 * @author cloudwaer
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}


