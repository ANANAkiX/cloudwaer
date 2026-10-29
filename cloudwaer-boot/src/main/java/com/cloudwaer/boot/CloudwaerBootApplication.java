package com.cloudwaer.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 单体模式启动类。
 * <p>
 * 显式 @MapperScan 聚合各业务模块 Mapper：boot 聚合模式下 mybatis-plus 自动扫描只覆盖主类包
 * （com.cloudwaer.boot），其他模块 Mapper 不会被注册（表现为启动报 No qualifying bean of type
 * XxxMapper），须在此显式声明各模块 mapper 包。
 */
@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
@MapperScan(basePackages = { "com.cloudwaer.admin.serve.mapper", "com.cloudwaer.flowable.serve.mapper",
		"com.cloudwaer.codegen.serve.mapper", "com.cloudwaer.blog.serve.mapper", "com.cloudwaer.wiki.serve.mapper" })
public class CloudwaerBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudwaerBootApplication.class, args);
	}

}
