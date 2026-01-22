package com.cloudwaer.common.core.util;

import com.cloudwaer.common.core.result.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * WebFlux response helpers.
 */
@Slf4j
public final class WebFluxResponseUtil {

	private WebFluxResponseUtil() {
	}

	public static Mono<Void> writeJson(ServerHttpResponse response, HttpStatus status, Result<?> body,
			ObjectMapper objectMapper) {
		response.setStatusCode(status);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		try {
			String json = objectMapper.writeValueAsString(body);
			DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
			return response.writeWith(Mono.just(buffer));
		}
		catch (JsonProcessingException e) {
			log.error("响应序列化失败", e);
			return response.setComplete();
		}
	}

}
