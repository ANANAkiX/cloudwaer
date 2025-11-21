package com.cloudwaer.gateway.filter;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.gateway.config.CaptchaProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaGlobalFilter implements GlobalFilter, Ordered {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String HEADER_CAPTCHA_ID = "X-Captcha-Id";
    private static final String HEADER_CAPTCHA_CODE = "X-Captcha-Code";
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    private final CaptchaProperties props;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!props.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // 仅在登录接口生效
        if (!LOGIN_PATH.equals(path)) {
            return chain.filter(exchange);
        }

        // 允许从 header 或 query 读取
        String captchaId = request.getHeaders().getFirst(HEADER_CAPTCHA_ID);
        String captchaCode = request.getHeaders().getFirst(HEADER_CAPTCHA_CODE);
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captchaCode)) {
            MultiValueMap<String, String> query = request.getQueryParams();
            if (!StringUtils.hasText(captchaId)) captchaId = query.getFirst("captchaId");
            if (!StringUtils.hasText(captchaCode)) captchaCode = query.getFirst("captchaCode");
        }

        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captchaCode)) {
            return captchaError(exchange, 400, "缺少验证码参数");
        }

        String key = CAPTCHA_KEY_PREFIX + captchaId;
        String expect = redis.opsForValue().get(key);
        if (!StringUtils.hasText(expect)) {
            return captchaError(exchange, 400, "验证码已过期，请重新获取");
        }

        if (!expect.equalsIgnoreCase(captchaCode)) {
            return captchaError(exchange, 400, "验证码错误");
        }

        // 校验通过后即刻删除，防止重复使用
        try {
            redis.delete(key);
        } catch (Exception ignore) {
        }
        return chain.filter(exchange);
    }

    private Mono<Void> captchaError(ServerWebExchange exchange, int code, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        Result<?> result = Result.fail(code, msg);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Captcha error response serialize failed", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        // 在 AuthGlobalFilter(-100) 之前执行，保证先校验验证码
        return -200;
    }
}
