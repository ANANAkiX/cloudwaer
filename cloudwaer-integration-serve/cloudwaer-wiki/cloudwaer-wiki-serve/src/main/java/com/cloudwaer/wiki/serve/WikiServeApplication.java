package com.cloudwaer.wiki.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 知识库服务启动类（微服务独立运行入口；单体由 cloudwaer-boot 聚合，无需启动本类）。
 *
 * @author cloudwaer
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
public class WikiServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WikiServeApplication.class, args);
	}

}
