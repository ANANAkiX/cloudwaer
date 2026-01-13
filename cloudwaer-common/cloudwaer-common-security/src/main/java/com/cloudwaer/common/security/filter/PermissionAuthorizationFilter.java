package com.cloudwaer.common.security.filter;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.annotation.PermitAll;
import com.cloudwaer.common.core.service.PermissionCacheService;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.PrintWriter;
import java.util.List;

/**
 * 权限授权过滤器
 * 在Spring Security过滤器链中执行，进行权限验证
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private PermissionCacheService permissionCacheService;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {
        
        // 添加日志，确认过滤器被调用
        log.debug("权限授权过滤器被调用: method={}, path={}", request.getMethod(), request.getRequestURI());
        
        // 检查是否是静态资源或排除的路径
        String path = request.getRequestURI();
        if (isExcludedPath(path)) {
            log.debug("路径在排除列表中，直接放行: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        // 检查是否有@PermitAll注解
        if (isPermitAll(request)) {
            log.debug("@PermitAll matched, skip auth check: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // 如果权限缓存服务未配置，直接放行
        if (permissionCacheService == null) {
            log.warn("PermissionCacheService未配置，跳过权限验证");
            filterChain.doFilter(request, response);
            return;
        }

        // 获取请求方法和路径
        String method = request.getMethod();
        
        // 规范化路径（去除上下文路径）
        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath) && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // 从Redis获取该接口需要的权限代码
        String requiredPermissionCode = permissionCacheService.getPermissionCode(method, path);
        
        // 如果该接口没有配置权限，直接放行（可能是新接口还未配置权限）
        if (!StringUtils.hasText(requiredPermissionCode)) {
            log.debug("接口未配置权限，直接放行: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        // 获取当前用户的权限代码列表
        List<String> userPermissions = SecurityContextUtil.getCurrentPermissions();

        // 如果用户未登录或没有权限信息，拒绝访问
        if (userPermissions == null || userPermissions.isEmpty()) {
            log.warn("用户未登录或没有权限信息: method={}, path={}", method, path);
            writeErrorResponse(response, Result.fail(ResultCode.UNAUTHORIZED.getCode(), "未登录或Token无效"));
            return;
        }

        // 检查用户是否有该权限
        if (!userPermissions.contains(requiredPermissionCode)) {
            log.warn("用户没有访问权限: method={}, path={}, requiredPermission={}, userPermissions={}",
                    method, path, requiredPermissionCode, userPermissions);
            writeErrorResponse(response, Result.fail(ResultCode.FORBIDDEN.getCode(), "没有访问权限"));
            return;
        }

        log.debug("权限验证通过: method={}, path={}, permission={}", method, path, requiredPermissionCode);
        filterChain.doFilter(request, response);
    }

    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcludedPath(String path) {
        // 排除认证接口、错误页面、Swagger等
        return path.startsWith("/auth/") ||
               path.equals("/error") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/doc.html") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico");
    }

    private boolean isPermitAll(HttpServletRequest request) {
        if (requestMappingHandlerMapping == null) {
            return false;
        }

        try {
            HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);
            if (chain == null) {
                return false;
            }
            Object handler = chain.getHandler();
            if (!(handler instanceof HandlerMethod)) {
                return false;
            }
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.hasMethodAnnotation(PermitAll.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(PermitAll.class);
        } catch (Exception e) {
            log.debug("PermitAll detection failed: {}", e.getMessage());
            return false;
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
        } catch (Exception e) {
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

