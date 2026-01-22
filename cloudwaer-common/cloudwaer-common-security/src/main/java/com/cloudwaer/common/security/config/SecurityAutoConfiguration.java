package com.cloudwaer.common.security.config;

import com.cloudwaer.common.security.filter.PermissionAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security自动配置 提供权限验证过滤器配置
 *
 * @author cloudwaer
 */
@AutoConfiguration
@ConditionalOnClass({ HttpSecurity.class, SecurityFilterChain.class })
@EnableWebSecurity
public class SecurityAutoConfiguration {

	@Autowired(required = false)
	private PermissionAuthorizationFilter permissionAuthorizationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll() // 允许所有请求通过，权限验证由自定义过滤器处理
			);

		// 添加权限授权过滤器到Spring Security过滤器链中
		// 在UsernamePasswordAuthenticationFilter之后执行
		if (permissionAuthorizationFilter != null) {
			http.addFilterAfter(permissionAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		}

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
