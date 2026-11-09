package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 延时器节点执行器：等待指定时间后自动流转
 * <p>
 * 当前实现：返回 TIMER 状态，由 TimerScheduler 调度唤醒。 configJson
 * 中配置延时参数：{"delayType":"fixed","delayValue":30,"delayUnit":"minutes"}
 */
@Slf4j
@Component
public class TimerNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "timer";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		log.info("延时器节点触发: nodeId={}, config={}", node.getNodeId(), node.getConfigJson());
		// 返回 TIMER，由调度器接管
		// 调度器到期后调用 ProcessEngine.resume 推进到下一个节点
		return NodeResult.timer(null);
	}

	/**
	 * 计算延时毫秒数（供 TimerScheduler 调用）
	 */
	public long resolveDelayMs(DslNode node) {
		String config = node.getConfigJson();
		if (config == null || config.isBlank()) {
			return 60_000; // 默认 1 分钟
		}
		try {
			com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = om.readTree(config);
			long value = root.has("delayValue") ? root.get("delayValue").asLong(30) : 30;
			String unit = root.has("delayUnit") ? root.get("delayUnit").asText("minutes") : "minutes";
			switch (unit) {
				case "seconds":
					return value * 1000;
				case "minutes":
					return value * 60 * 1000;
				case "hours":
					return value * 60 * 60 * 1000;
				case "days":
					return value * 24 * 60 * 60 * 1000;
				default:
					return value * 60 * 1000;
			}
		}
		catch (Exception e) {
			log.warn("解析延时配置失败: {}", e.getMessage());
			return 60_000;
		}
	}

}
