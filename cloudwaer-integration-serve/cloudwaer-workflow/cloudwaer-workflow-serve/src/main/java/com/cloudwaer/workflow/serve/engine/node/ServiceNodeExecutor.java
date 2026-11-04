package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import com.cloudwaer.workflow.serve.engine.action.ActionContext;
import com.cloudwaer.workflow.serve.engine.action.ActionDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务任务节点执行器：调用外部服务（HTTP/MQ/脚本）
 * <p>
 * config:
 * {"serviceType":"http|mq|script","method":"GET","url":"xxx","headers":{},"body":"xxx"}
 * {"serviceType":"mq","topic":"xxx","payload":"xxx"}
 * {"serviceType":"script","language":"groovy","script":"xxx"}
 */
@Slf4j
@Component
public class ServiceNodeExecutor implements NodeExecutor {

	@Autowired
	private ActionDispatcher actionDispatcher;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String getNodeType() {
		return "service";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		String config = node.getConfigJson();
		if (config == null || config.isBlank()) {
			log.warn("[SERVICE] 无配置，跳过: nodeId={}", node.getNodeId());
			return NodeResult.auto(null);
		}

		try {
			JsonNode root = objectMapper.readTree(config);
			String serviceType = root.has("serviceType") ? root.get("serviceType").asText("http") : "http";

			// 构建动作上下文，复用 ActionDispatcher
			ActionContext actionContext = new ActionContext();
			actionContext.setInstanceId(context.getInstanceId());
			actionContext.setTaskId(context.getCurrentTaskId());
			actionContext.setNodeId(node.getNodeId());
			actionContext.setEventType("node:enter");
			actionContext.setOperator(context.getOperator());
			actionContext.setConfigJson(config);

			switch (serviceType) {
				case "http":
					actionDispatcher.dispatch(actionContext, "http", config);
					break;
				case "mq":
					actionDispatcher.dispatch(actionContext, "mq", config);
					break;
				case "script":
					actionDispatcher.dispatch(actionContext, "script", config);
					break;
				default:
					log.warn("[SERVICE] 未知服务类型: {}", serviceType);
			}
		}
		catch (Exception e) {
			log.error("[SERVICE] 执行失败: nodeId={}, err={}", node.getNodeId(), e.getMessage(), e);
		}

		return NodeResult.auto(null);
	}

}
