package com.cloudwaer.flowable.serve.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 服务 Feign 配置：内部调用标识。
 */
@Configuration
public class FlowableFeignConfig {

	@Bean
	public RequestInterceptor flowableInternalCallInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				template.header("X-Internal-Call", "true");
			}
		};
	}

}
