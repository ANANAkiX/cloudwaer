package com.cloudwaer.blog.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 博客服务启动类
 *
 * @author cloudwaer
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class BlogServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServeApplication.class, args);
	}

}
