package com.cloudwaer.workflow.serve.engine.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.workflow.serve.engine.action.ActionContext;
import com.cloudwaer.workflow.serve.engine.action.ActionDispatcher;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.entity.WfNode;
import com.cloudwaer.workflow.serve.mapper.WfInstanceMapper;
import com.cloudwaer.workflow.serve.mapper.WfNodeMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 节点级动作/监听器派发：事件发生时按节点 actionJson/listenerJson 执行。
 * <p>
 * 事件（node:enter / task:create / task:complete 等）→ 查节点配置 → ① 动作：ActionDispatcher
 * 分发（log/http/mq/notify/script，落 wf_action_log）； ② 监听器：按 className 调用对应监听器 Bean（webhook 附
 * url 直发）。 异常一律吞掉降级，不影响流程主链路。
 */
@Slf4j
@Component
public class NodeConfigDispatchListener implements WorkflowEventListener {

	@Autowired
	private WfInstanceMapper instanceMapper;

	@Autowired
	private WfNodeMapper nodeMapper;

	@Autowired
	private ActionDispatcher actionDispatcher;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired(required = false)
	private WebhookListener webhookListener;

	@Override
	public String getEventType() {
		return "*";
	}

	@Override
	public void onEvent(WorkflowEvent event) {
		if (event == null || event.getNodeId() == null || event.getInstanceId() == null) {
			return;
		}
		WfNode node = loadNode(event);
		if (node == null) {
			return;
		}
		dispatchActions(node, event);
		dispatchListeners(node, event);
	}

	private WfNode loadNode(WorkflowEvent event) {
		try {
			WfInstance instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, event.getInstanceId()));
			if (instance == null || instance.getDefId() == null) {
				return null;
			}
			return nodeMapper.selectOne(new LambdaQueryWrapper<WfNode>().eq(WfNode::getDefId, instance.getDefId())
				.eq(WfNode::getNodeId, event.getNodeId()));
		}
		catch (Exception e) {
			log.warn("[NodeDispatch] 加载节点配置失败: node={}, err={}", event.getNodeId(), e.getMessage());
			return null;
		}
	}

	/** 节点动作：actionJson = [{actionType, eventType, config}] */
	private void dispatchActions(WfNode node, WorkflowEvent event) {
		if (node.getActionJson() == null || node.getActionJson().isBlank()) {
			return;
		}
		try {
			JsonNode arr = objectMapper.readTree(node.getActionJson());
			for (JsonNode item : arr) {
				if (!eventMatches(item, event.getEventType())) {
					continue;
				}
				String actionType = item.hasNonNull("actionType") ? item.get("actionType").asText() : null;
				if (actionType == null || actionType.isBlank()) {
					continue;
				}
				String config = null;
				if (item.hasNonNull("config")) {
					config = item.get("config").isObject() ? item.get("config").toString()
							: item.get("config").asText();
				}
				ActionContext ctx = new ActionContext();
				ctx.setInstanceId(event.getInstanceId());
				ctx.setTaskId(event.getTaskId());
				ctx.setNodeId(event.getNodeId());
				ctx.setEventType(event.getEventType());
				ctx.setOperator(event.getOperator());
				ctx.setConfigJson(config);
				actionDispatcher.dispatch(ctx, actionType, config);
			}
		}
		catch (Exception e) {
			log.error("[NodeDispatch] 动作派发失败: node={}, err={}", node.getNodeId(), e.getMessage());
		}
	}

	/** 节点监听器：listenerJson = [{eventType, className, url?}] */
	private void dispatchListeners(WfNode node, WorkflowEvent event) {
		if (node.getListenerJson() == null || node.getListenerJson().isBlank()) {
			return;
		}
		try {
			JsonNode arr = objectMapper.readTree(node.getListenerJson());
			for (JsonNode item : arr) {
				if (!eventMatches(item, event.getEventType())) {
					continue;
				}
				String className = item.hasNonNull("className") ? item.get("className").asText() : null;
				if (className == null || className.isBlank()) {
					continue;
				}
				String url = item.hasNonNull("url") ? item.get("url").asText() : null;
				if ("webhookListener".equals(className) && url != null && !url.isBlank()) {
					if (webhookListener != null) {
						webhookListener.send(url, event);
					}
					continue;
				}
				WorkflowEventListener target = resolveListener(className);
				if (target != null && target != this) {
					target.onEvent(event);
				}
			}
		}
		catch (Exception e) {
			log.error("[NodeDispatch] 监听器派发失败: node={}, err={}", node.getNodeId(), e.getMessage());
		}
	}

	private boolean eventMatches(JsonNode item, String eventType) {
		String ev = item.hasNonNull("eventType") ? item.get("eventType").asText() : null;
		return ev == null || ev.isBlank() || "*".equals(ev) || ev.equals(eventType);
	}

	/** 按 bean 名解析监听器（目录键与 bean 名一致：auditLogListener 等） */
	private WorkflowEventListener resolveListener(String key) {
		try {
			Object bean = applicationContext.getBean(key);
			if (bean instanceof WorkflowEventListener listener && bean != this) {
				return listener;
			}
		}
		catch (Exception e) {
			log.warn("[NodeDispatch] 监听器不存在: {}", key);
		}
		return null;
	}

}
