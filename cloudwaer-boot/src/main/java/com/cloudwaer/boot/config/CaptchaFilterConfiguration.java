package com.cloudwaer.boot.config;

import com.cloudwaer.boot.filter.CaptchaValidationFilter;
import com.cloudwaer.common.core.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class CaptchaFilterConfiguration {

	@Bean
	public CaptchaValidationFilter captchaValidationFilter(CaptchaProperties props, RedisUtil redisUtil,
			ObjectMapper objectMapper) {
		return new CaptchaValidationFilter(props, redisUtil, objectMapper);
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
