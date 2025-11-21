package com.cloudwaer.common.log.aspect;

import com.cloudwaer.common.log.config.LogProperties;
import com.cloudwaer.common.log.model.LogRecord;
import com.cloudwaer.common.log.service.LogSaver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.Executor;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class RequestLogAspect {

    private final LogProperties properties;
    private final LogSaver saver;
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final Environment environment;
    private final Executor executor;

    @Around("(@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)) && (execution(* *(..)) && (@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping)))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        if (!properties.isEnabled()) {
            return pjp.proceed();
        }
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (!(ra instanceof ServletRequestAttributes attrs)) {
            return pjp.proceed();
        }
        HttpServletRequest request = attrs.getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 排除规则
        if (shouldExclude(uri, method, pjp)) {
            return pjp.proceed();
        }

        boolean success = true;
        String errorMsg = null;
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            success = false;
            errorMsg = ex.getClass().getSimpleName() + ":" + (ex.getMessage() == null ? "" : ex.getMessage());
            throw ex;
        } finally {
            MethodSignature ms = (MethodSignature) pjp.getSignature();
            Method m = ms.getMethod();
            LogRecord rec = new LogRecord();
            rec.setTimestamp(Instant.now().toEpochMilli());
            rec.setService(environment.getProperty("spring.application.name", "unknown"));
            rec.setIp(getClientIp(request));
            rec.setHttpMethod(method);
            rec.setUri(uri);
            rec.setClassName(ms.getDeclaringTypeName());
            rec.setMethodName(m.getName());
            rec.setSuccess(success);
            rec.setError(errorMsg);
            executor.execute(() -> {
                try {
                    saver.save(rec);
                } catch (Exception e) {
                    log.warn("LogSaver save failed: {}", e.getMessage());
                }
            });
        }
    }

    private boolean shouldExclude(String uri, String httpMethod, ProceedingJoinPoint pjp) {
        // 路径排除（支持Ant匹配）
        if (properties.getExcludePaths() != null) {
            for (String pattern : properties.getExcludePaths()) {
                if (StringUtils.hasText(pattern) && matcher.match(pattern, uri)) return true;
            }
        }
        // 方法排除（如 GET/POST）
        if (properties.getExcludeHttpMethods() != null && properties.getExcludeHttpMethods().contains(httpMethod)) {
            return true;
        }
        // 类名/方法名排除
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String className = ms.getDeclaringTypeName();
        String methodName = ms.getMethod().getName();
        if (properties.getExcludeClassNames() != null && properties.getExcludeClassNames().contains(className)) {
            return true;
        }
        if (properties.getExcludeMethodNames() != null && properties.getExcludeMethodNames().contains(methodName)) {
            return true;
        }
        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        for (String h : headers) {
            String ip = request.getHeader(h);
            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 可能有逗号分隔的多个IP，取第一个
                int idx = ip.indexOf(',');
                return idx > 0 ? ip.substring(0, idx).trim() : ip.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
