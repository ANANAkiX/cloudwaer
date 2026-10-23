package com.cloudwaer.boot.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 单体多数据源：按请求路径在整次请求期间固定路由键。
 *
 * <p>
 * primary=flowable，因此 Flowable 引擎在无上下文时天然安全。 admin / codegen 必须由本过滤器显式 push，否则会误连
 * flowable 库。
 * </p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DynamicDataSourceRouteFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String ds = resolve(request.getRequestURI());
		DynamicDataSourceContextHolder.push(ds);
		try {
			filterChain.doFilter(request, response);
		}
		finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

	/**
	 * 所有 HTTP 请求都必须落到明确库，避免依赖 primary。
	 */
	private String resolve(String path) {
		if (path != null && path.contains("/flowable/")) {
			return "flowable";
		}
		if (path != null && path.contains("/codegen/")) {
			return "codegen";
		}
		// /admin/**、/auth/** 及其它一律 admin
		return "admin";
	}

}
