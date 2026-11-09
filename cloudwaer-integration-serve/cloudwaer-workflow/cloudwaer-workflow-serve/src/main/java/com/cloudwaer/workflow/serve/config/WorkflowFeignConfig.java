package com.cloudwaer.workflow.serve.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * workflow 服务 Feign 配置：内部调用统一打 X-Internal-Call 头。
 * <p>
 * bean 名与 blog 侧区分，避免 boot 聚合模式下同名覆盖。
 */
@Configuration
public class WorkflowFeignConfig {

	@Bean
	public RequestInterceptor workflowInternalCallHeaderInterceptor() {
		return template -> template.header("X-Internal-Call", "true");
	}

}
