package com.cloudwaer.workflow.serve.engine.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 领域事件发布器：分发事件到所有匹配的监听器
 */
@Slf4j
@Component
public class EventPublisher {

	@Autowired(required = false)
	private List<WorkflowEventListener> listeners;

	/**
	 * 发布事件（同步）
	 */
	public void publish(WorkflowEvent event) {
		if (listeners == null || event == null) {
			return;
		}
		for (WorkflowEventListener listener : listeners) {
			// 节点级监听器（审计/站内信/Webhook 等）只按节点 listenerJson 显式启用
			if (listener instanceof NodeScopedListener) {
				continue;
			}
			String type = listener.getEventType();
			if ("*".equals(type) || type.equals(event.getEventType())) {
				try {
					listener.onEvent(event);
				}
				catch (Exception e) {
					log.error("监听器执行失败: listener={}, event={}, err={}", listener.getClass().getSimpleName(),
							event.getEventType(), e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 发布事件（构建并发布）
	 */
	public void publish(String eventType, String instanceId, String taskId, String nodeId, String nodeName,
			String operator, Object payload) {
		WorkflowEvent event = new WorkflowEvent();
		event.setEventType(eventType);
		event.setInstanceId(instanceId);
		event.setTaskId(taskId);
		event.setNodeId(nodeId);
		event.setNodeName(nodeName);
		event.setOperator(operator);
		event.setPayload(payload);
		publish(event);
	}

}
