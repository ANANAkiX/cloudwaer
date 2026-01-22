package com.cloudwaer.codegen.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 代码生成服务启动类
 *
 * @author cloudwaer
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloudwaer")
public class CodegenServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodegenServeApplication.class, args);
	}

}
