package com.cloudwaer.workflow.serve.engine.listener;

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
 * Webhook 监听器：流程事件发生时调用外部 HTTP 接口
 * <p>
 * 在节点 listenerJson
 * 中配置：{"eventType":"task:complete","listener":"webhookListener","url":"https://..."}
 */
@Slf4j
@Component
public class WebhookListener implements NodeScopedListener {

	@Autowired
	private ObjectMapper objectMapper;

	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

	@Override
	public String getEventType() {
		return "*";
	}

	@Override
	public void onEvent(WorkflowEvent event) {
		// Webhook 需要在 listenerJson 中配置 url，这里只处理有 payload 的场景
		Object payload = event.getPayload();
		if (payload == null)
			return;

		try {
			String json = objectMapper.writeValueAsString(event);
			log.info("[WEBHOOK] 流程事件: type={}, instance={}, payload={}", event.getEventType(), event.getInstanceId(),
					json);
			// 预留：从 payload 中读取 url 并发送
		}
		catch (Exception e) {
			log.error("[WEBHOOK] 序列化失败: {}", e.getMessage());
		}
	}

	/**
	 * 发送到指定 URL（供 ActionDispatcher 或手动调用）
	 */
	public void send(String url, WorkflowEvent event) {
		try {
			String json = objectMapper.writeValueAsString(event);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.timeout(Duration.ofSeconds(10))
				.build();
			HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("[WEBHOOK] 发送完成: url={}, status={}", url, resp.statusCode());
		}
		catch (Exception e) {
			log.error("[WEBHOOK] 发送失败: url={}, err={}", url, e.getMessage());
		}
	}

}
