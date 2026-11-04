package com.cloudwaer.workflow.serve.engine.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 监听器注册表：按名称查找预注册监听器
 */
@Component
public class ListenerRegistry {

	@Autowired(required = false)
	private List<WorkflowEventListener> listeners;

	private volatile Map<String, WorkflowEventListener> byName;

	// 可供设计器下拉选择的监听器列表
	private static final Map<String, String> AVAILABLE_LISTENERS = new LinkedHashMap<>();

	static {
		AVAILABLE_LISTENERS.put("auditLogListener", "审计日志");
		AVAILABLE_LISTENERS.put("notifyTaskListener", "站内信通知");
		AVAILABLE_LISTENERS.put("webhookListener", "Webhook 回调");
	}

	private void ensureInit() {
		if (byName == null) {
			synchronized (this) {
				if (byName == null) {
					byName = listeners != null ? listeners.stream()
						.collect(Collectors.toMap(l -> toBeanName(l.getClass().getSimpleName()), Function.identity()))
							: Map.of();
				}
			}
		}
	}

	private String toBeanName(String className) {
		return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	}

	public WorkflowEventListener getByName(String name) {
		ensureInit();
		return name != null ? byName.get(name) : null;
	}

	/**
	 * 返回可用监听器列表（供设计器下拉）
	 */
	public Map<String, String> getAvailableListeners() {
		return AVAILABLE_LISTENERS;
	}

}
