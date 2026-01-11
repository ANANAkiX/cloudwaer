package com.cloudwaer.gateway.filter;

import com.cloudwaer.common.core.constant.CommonConstants;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证全局过滤器
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 不需要认证的路径
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/**",
            "/actuator/**"
    );


    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单路径直接放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Token UUID
        String tokenUuid = getToken(request);
        if (!StringUtils.hasText(tokenUuid)) {
            return unauthorizedResponse(exchange);
        }

        // 验证Token UUID（检查Redis中是否存在）
        if (tokenService == null) {
            log.warn("TokenService未配置，无法验证Token");
            return unauthorizedResponse(exchange);
        }

        if (!tokenService.validateToken(tokenUuid)) {
            log.warn("Token验证失败: tokenUuid={}", tokenUuid);
            return unauthorizedResponse(exchange);
        }

        // Token验证通过，直接放行（用户信息从Token UUID对应的Redis数据中获取）
        return chain.filter(exchange);
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(CommonConstants.TOKEN_HEADER);
        if (StringUtils.hasText(authorization) && authorization.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return authorization.substring(CommonConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        Result<?> result = Result.fail(ResultCode.UNAUTHORIZED);
        try {
            String json = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("响应序列化失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}



