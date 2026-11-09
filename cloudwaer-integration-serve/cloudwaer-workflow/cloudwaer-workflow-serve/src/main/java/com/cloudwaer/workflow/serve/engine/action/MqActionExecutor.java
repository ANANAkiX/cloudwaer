package com.cloudwaer.workflow.serve.engine.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MQ 动作执行器：发送消息到队列
 * <p>
 * config: {"topic":"xxx","tag":"xxx","payload":"xxx"}
 */
@Slf4j
@Component
public class MqActionExecutor implements ActionExecutor {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String getActionType() {
		return "mq";
	}

	@Override
	public void execute(ActionContext context, String config) {
		try {
			JsonNode root = objectMapper.readTree(config);
			String topic = root.has("topic") ? root.get("topic").asText() : null;
			String tag = root.has("tag") ? root.get("tag").asText() : "";
			String payload = root.has("payload") ? root.get("payload").asText() : "";

			if (topic == null || topic.isBlank()) {
				log.warn("[MQ] topic 为空，跳过: config={}", config);
				return;
			}

			// TODO: 对接 RabbitMQ / Kafka / RocketMQ
			// 当前实现：记录日志，预留扩展口
			log.info("[MQ] 消息已记录: topic={}, tag={}, payload={}", topic, tag, payload);
		}
		catch (Exception e) {
			log.error("[MQ] 发送失败: config={}, err={}", config, e.getMessage(), e);
		}
	}

}
