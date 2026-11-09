package com.cloudwaer.workflow.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 流程服务启动类（微服务模式）
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class WorkflowServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowServeApplication.class, args);
	}

}
