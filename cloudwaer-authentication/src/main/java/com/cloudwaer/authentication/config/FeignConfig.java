package com.cloudwaer.authentication.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置
 *
 * @author cloudwaer
 */
@Configuration
@EnableFeignClients(basePackages = "com.cloudwaer.admin.api.feign")
public class FeignConfig {

	/**
	 * Feign 请求拦截器 为所有 Feign 调用添加内部调用标识
	 */
	@Bean
	public RequestInterceptor internalCallInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				// 添加内部调用标识
				template.header("X-Internal-Call", "true");
			}
		};
	}

}
