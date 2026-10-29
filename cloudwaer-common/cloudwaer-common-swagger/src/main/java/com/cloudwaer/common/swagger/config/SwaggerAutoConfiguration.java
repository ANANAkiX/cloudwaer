package com.cloudwaer.common.swagger.config;

import com.cloudwaer.common.core.service.GlobalSettingService;
import com.cloudwaer.common.swagger.filter.ApiDocSwitchFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Collections;

/**
 * Swagger自动配置类
 *
 * @author cloudwaer
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

	private final SwaggerProperties swaggerProperties;

	public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
		this.swaggerProperties = swaggerProperties;
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title(swaggerProperties.getTitle())
			.version(swaggerProperties.getVersion())
			.description(swaggerProperties.getDescription())
			.contact(new Contact().name(swaggerProperties.getContactName()).email(swaggerProperties.getContactEmail()))
			.license(new License().name(swaggerProperties.getLicenseName()).url(swaggerProperties.getLicenseUrl())));
	}

	/**
	 * API 文档开关（全局设置 api-doc.enabled 热切换；未覆盖回退 springdoc.api-docs.enabled）
	 */
	@Bean
	public FilterRegistrationBean<ApiDocSwitchFilter> apiDocSwitchFilter(GlobalSettingService globalSettingService,
			Environment environment) {
		String springdocDefault = environment.getProperty("springdoc.api-docs.enabled");
		FilterRegistrationBean<ApiDocSwitchFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ApiDocSwitchFilter(globalSettingService, springdocDefault));
		registration.setUrlPatterns(Collections.singletonList("/*"));
		registration.setOrder(-150);
		return registration;
	}

}
