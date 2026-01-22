package com.cloudwaer.admin.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户管理服务启动类
 *
 * @author cloudwaer
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class AdminServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServeApplication.class, args);
	}

}
