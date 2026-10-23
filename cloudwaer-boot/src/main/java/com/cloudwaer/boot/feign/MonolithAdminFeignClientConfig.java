package com.cloudwaer.boot.feign;

import feign.Client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
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

	/**
	 * 单体模式 Feign 走本机 HTTP，同样携带内部调用标识，保证 enable=false 接口可被调用。
	 * @return 请求拦截器
	 */
	@Bean
	public RequestInterceptor internalCallInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				template.header("X-Internal-Call", "true");
			}
		};
	}

}
