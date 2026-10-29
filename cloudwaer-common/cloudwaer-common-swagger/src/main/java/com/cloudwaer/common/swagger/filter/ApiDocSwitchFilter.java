package com.cloudwaer.common.swagger.filter;

import com.cloudwaer.common.core.service.GlobalSettingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * API 文档开关过滤器：全局设置 api-doc.enabled=false 时屏蔽 swagger/api-docs 路径（热切换，无需重启）。
 *
 * <p>
 * 未覆盖该设置时回退 {@code springdoc.api-docs.enabled}（默认开启）。
 * </p>
 *
 * @author cloudwaer
 */
public class ApiDocSwitchFilter extends OncePerRequestFilter {

	private final GlobalSettingService globalSettingService;

	private final String springdocEnabledDefault;

	public ApiDocSwitchFilter(GlobalSettingService globalSettingService, String springdocEnabledDefault) {
		this.globalSettingService = globalSettingService;
		this.springdocEnabledDefault = springdocEnabledDefault;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		boolean enabled = globalSettingService.getBool(GlobalSettingService.KEY_API_DOC_ENABLED,
				!StringUtils.hasText(springdocEnabledDefault) || Boolean.parseBoolean(springdocEnabledDefault));
		if (!enabled && isApiDocPath(request.getRequestURI())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		filterChain.doFilter(request, response);
	}

	private boolean isApiDocPath(String uri) {
		if (uri == null) {
			return false;
		}
		return uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui") || uri.equals("/swagger-ui.html");
	}

}
