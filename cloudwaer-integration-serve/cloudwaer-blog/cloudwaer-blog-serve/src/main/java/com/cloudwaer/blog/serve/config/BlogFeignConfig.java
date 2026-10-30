package com.cloudwaer.blog.serve.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * blog 服务 Feign 配置：内部调用统一打 X-Internal-Call 头 （admin 侧 @PermissionCode(enable=false)
 * 接口仅放行带该头的请求）。
 *
 * @author cloudwaer
 */
@Configuration
public class BlogFeignConfig {

	@Bean
	public RequestInterceptor internalCallHeaderInterceptor() {
		return template -> template.header("X-Internal-Call", "true");
	}

}
