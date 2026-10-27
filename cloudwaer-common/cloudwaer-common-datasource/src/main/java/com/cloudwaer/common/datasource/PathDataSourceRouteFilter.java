package com.cloudwaer.common.datasource;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 单体多数据源：按请求路径在整次请求期间固定路由键。
 *
 * <p>
 * 约定：{@code /<key>/...} → {@code <key>}；auth/actuator 等保留段回落 defaultKey。可用
 * {@code cloudwaer.datasource.route.path-prefixes} 覆盖。
 * </p>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PathDataSourceRouteFilter extends OncePerRequestFilter {

	private final DataSourceRouteProperties props;

	public PathDataSourceRouteFilter(DataSourceRouteProperties props) {
		this.props = props;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String ds = DataSourceRouteResolver.resolveFromPath(request.getRequestURI(), props);
		DynamicDataSourceContextHolder.push(ds);
		try {
			filterChain.doFilter(request, response);
		}
		finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

}
