package com.cloudwaer.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 单体模式启动类。
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class CloudwaerBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudwaerBootApplication.class, args);
	}

}
