package com.cloudwaer.boot.config;

import com.cloudwaer.boot.filter.CaptchaValidationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;

@Configuration
public class CaptchaFilterConfiguration {

	@Bean
	public CaptchaValidationFilter captchaValidationFilter(CaptchaProperties props, StringRedisTemplate redis,
			ObjectMapper objectMapper) {
		return new CaptchaValidationFilter(props, redis, objectMapper);
	}

	@Bean
	public FilterRegistrationBean<CaptchaValidationFilter> captchaValidationFilterRegistration(
			CaptchaValidationFilter filter) {
		FilterRegistrationBean<CaptchaValidationFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setUrlPatterns(Collections.singletonList("/auth/login"));
		registration.setOrder(-200);
		return registration;
	}

}
