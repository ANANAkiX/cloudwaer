package com.cloudwaer.boot.feign;

import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 单体模式：让 Feign 走本机 HTTP，避免依赖注册发现。
 */
@Configuration
@ConditionalOnClass(Client.class)
public class MonolithAdminFeignClientConfig {

	@Bean
	public feign.Client feignClient() {
		return new OkHttpClient();
	}

}
