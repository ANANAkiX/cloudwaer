package com.cloudwaer.workflow.serve.engine.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP 动作执行器：调用外部 REST API
 * <p>
 * config: {"method":"POST","url":"xxx","headers":{"k":"v"},"body":"xxx"}
 */
@Slf4j
@Component
public class HttpActionExecutor implements ActionExecutor {

	@Autowired
	private ObjectMapper objectMapper;

	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

	@Override
	public String getActionType() {
		return "http";
	}

	@Override
	public void execute(ActionContext context, String config) {
		try {
			JsonNode root = objectMapper.readTree(config);
			String method = root.has("method") ? root.get("method").asText("GET") : "GET";
			String url = root.has("url") ? root.get("url").asText() : null;
			if (url == null || url.isBlank()) {
				log.warn("[HTTP] URL 为空，跳过: config={}", config);
				return;
			}

			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(30));

			// 设置 headers
			if (root.has("headers") && root.get("headers").isObject()) {
				root.get("headers").fields().forEachRemaining(e -> builder.header(e.getKey(), e.getValue().asText()));
			}

			// 设置 body
			String body = root.has("body") ? root.get("body").asText() : null;
			if (body != null && ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))) {
				builder.header("Content-Type", "application/json");
				builder.method(method, HttpRequest.BodyPublishers.ofString(body));
			}
			else {
				builder.method(method, HttpRequest.BodyPublishers.noBody());
			}

			HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			log.info("[HTTP] 调用完成: {} {}, status={}", method, url, response.statusCode());
		}
		catch (Exception e) {
			log.error("[HTTP] 调用失败: config={}, err={}", config, e.getMessage(), e);
			throw new RuntimeException("HTTP 动作执行失败: " + e.getMessage(), e);
		}
	}

}
