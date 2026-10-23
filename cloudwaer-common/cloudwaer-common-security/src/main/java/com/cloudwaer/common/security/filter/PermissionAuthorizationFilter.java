package com.cloudwaer.common.security.filter;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.PrintWriter;
import java.util.List;

/**
 * 权限授权过滤器 在Spring Security过滤器链中执行，进行权限验证
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

	@Autowired(required = false)
	private ObjectMapper objectMapper;

	@Autowired(required = false)
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, java.io.IOException {

		// 添加日志，确认过滤器被调用
		log.info("权限授权过滤器被调用: method={}, path={}", request.getMethod(), request.getRequestURI());

		// 检查是否是静态资源或排除的路径
		String path = request.getRequestURI();
		if (isExcludedPath(path)) {
			log.info("路径在排除列表中，直接放行: {}", path);
			filterChain.doFilter(request, response);
			return;
		}

		// 客户端绑定：有 Token 时校验 X-Client-Id 与登录 clientId 一致
		if (!validateClientBinding(request)) {
			writeErrorResponse(response, Result.fail(ResultCode.UNAUTHORIZED.getCode(), "客户端与Token不匹配"));
			return;
		}

		// 从注解获取权限代码
		PermissionCode permissionCodeAnnotation = getPermissionCodeAnnotation(request);
		if (permissionCodeAnnotation == null) {
			// 未配置权限码时，检查是否要求"仅登录"（自服务接口）
			RequireLogin requireLogin = getRequireLoginAnnotation(request);
			if (requireLogin != null) {
				Long userId = SecurityContextUtil.getCurrentUserId();
				if (userId == null) {
					log.warn("接口要求登录但未登录或Token无效: method={}, path={}", request.getMethod(), path);
					writeErrorResponse(response, Result.fail(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token无效"));
					return;
				}
				log.debug("登录校验通过: method={}, path={}, userId={}", request.getMethod(), path, userId);
				filterChain.doFilter(request, response);
				return;
			}
			log.debug("接口未配置@PermissionCode/@RequireLogin注解，按匿名接口放行: {} {}", request.getMethod(),
					request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		// 检查是否跳过权限验证
		if (!permissionCodeAnnotation.enable()) {
			// 对于 enable=false 的接口，要求必须是内部服务调用
			if (!isInternalCall(request)) {
				log.warn("接口配置为 enable=false 但不是内部调用，拒绝访问: {} {}", request.getMethod(), request.getRequestURI());
				writeErrorResponse(response, Result.fail(ResultCode.FORBIDDEN.getCode(), "仅限内部服务调用"));
				return;
			}
			log.info("@PermissionCode(enable=false) 且是内部调用，放行: {} {}", request.getMethod(), request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		// 获取权限代码
		String requiredPermissionCode = permissionCodeAnnotation.value();
		if (requiredPermissionCode == null || requiredPermissionCode.isEmpty()) {
			log.warn("@PermissionCode注解值为空，拒绝访问: {} {}", request.getMethod(), request.getRequestURI());
			writeErrorResponse(response, Result.fail(ResultCode.FORBIDDEN.getCode(), "权限配置错误"));
			return;
		}

		// 获取当前用户的权限代码列表
		List<String> userPermissions = SecurityContextUtil.getCurrentPermissions();

		// 如果用户未登录或没有权限信息，拒绝访问
		if (userPermissions == null || userPermissions.isEmpty()) {
			log.warn("用户未登录或没有权限信息: method={}, path={}", request.getMethod(), path);
			writeErrorResponse(response, Result.fail(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token无效"));
			return;
		}

		// 检查用户是否有该权限
		if (!userPermissions.contains(requiredPermissionCode)) {
			log.warn("用户没有访问权限: method={}, path={}, requiredPermission={}, userPermissions={}", request.getMethod(),
					path, requiredPermissionCode, userPermissions);
			writeErrorResponse(response, Result.fail(ResultCode.FORBIDDEN.getCode(), "禁止访问"));
			return;
		}

		log.debug("权限验证通过: method={}, path={}, permission={}", request.getMethod(), path, requiredPermissionCode);
		filterChain.doFilter(request, response);
	}

	/**
	 * 客户端绑定校验：请求头 X-Client-Id 与 Token 登录 clientId 一致
	 */
	private boolean validateClientBinding(HttpServletRequest request) {
		String tokenUuid = SecurityContextUtil.getCurrentToken();
		if (tokenUuid == null || tokenUuid.isBlank()) {
			return true;
		}
		try {
			com.cloudwaer.common.core.service.TokenService tokenService = org.springframework.web.context.support.WebApplicationContextUtils
				.getWebApplicationContext(request.getServletContext())
				.getBean(com.cloudwaer.common.core.service.TokenService.class);
			String clientId = request.getHeader(com.cloudwaer.common.core.constant.CommonConstants.CLIENT_ID_HEADER);
			return tokenService.validateTokenClient(tokenUuid, clientId);
		}
		catch (Exception e) {
			log.warn("客户端绑定校验异常: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 检查路径是否在排除列表中
	 */
	private boolean isExcludedPath(String path) {
		// 排除认证接口、错误页面、Swagger等
		return path.startsWith("/auth/") || path.equals("/error") || path.startsWith("/swagger-ui/")
				|| path.startsWith("/v3/api-docs/") || path.equals("/doc.html") || path.startsWith("/webjars/")
				|| path.equals("/favicon.ico");
	}

	/**
	 * 检查是否是内部服务调用 只有携带 X-Internal-Call: true header 的请求才被认为是内部调用
	 */
	private boolean isInternalCall(HttpServletRequest request) {
		String internalCallHeader = request.getHeader("X-Internal-Call");
		return "true".equals(internalCallHeader);
	}

	/**
	 * 获取请求处理方法上的@PermissionCode注解
	 */
	private PermissionCode getPermissionCodeAnnotation(HttpServletRequest request) {
		HandlerMethod handlerMethod = getHandlerMethod(request);
		return handlerMethod != null ? handlerMethod.getMethodAnnotation(PermissionCode.class) : null;
	}

	/**
	 * 获取请求处理方法上的@RequireLogin注解
	 */
	private RequireLogin getRequireLoginAnnotation(HttpServletRequest request) {
		HandlerMethod handlerMethod = getHandlerMethod(request);
		return handlerMethod != null ? handlerMethod.getMethodAnnotation(RequireLogin.class) : null;
	}

	/**
	 * 解析请求对应的HandlerMethod
	 */
	private HandlerMethod getHandlerMethod(HttpServletRequest request) {
		if (requestMappingHandlerMapping == null) {
			return null;
		}

		try {
			HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);
			if (chain == null) {
				return null;
			}
			Object handler = chain.getHandler();
			if (!(handler instanceof HandlerMethod)) {
				return null;
			}
			return (HandlerMethod) handler;
		}
		catch (Exception e) {
			log.debug("解析HandlerMethod失败: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 写入错误响应
	 */
	private void writeErrorResponse(HttpServletResponse response, Result<?> result) {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			String json = getObjectMapper().writeValueAsString(result);
			writer.write(json);
			writer.flush();
		}
		catch (Exception e) {
			log.error("写入错误响应失败", e);
		}
	}

	/**
	 * 获取ObjectMapper（如果未注入，创建默认实例）
	 */
	private ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			return new ObjectMapper();
		}
		return objectMapper;
	}

}
