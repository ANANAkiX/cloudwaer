package com.cloudwaer.flowable.serve.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一监听器注册表：按名称暴露任务/执行/引擎事件监听器，供设计器枚举与 BPMN delegateExpression 引用。
 * <p>
 * 懒加载：避免在 Bean 构造阶段 getBeansOfType 触发 Flowable/Actuator 过早初始化导致启动/销毁异常。
 */
@Component
public class EventListenerRegistry {

	@Autowired
	private ApplicationContext context;

	private final Map<String, TaskListener> taskListeners = new LinkedHashMap<>();

	private final Map<String, ExecutionListener> executionListeners = new LinkedHashMap<>();

	private final Map<String, FlowableEventListener> engineEventListeners = new LinkedHashMap<>();

	private volatile boolean initialized = false;

	private void ensureInitialized() {
		if (initialized) {
			return;
		}
		synchronized (this) {
			if (initialized) {
				return;
			}
			Map<String, TaskListener> taskBeans = context.getBeansOfType(TaskListener.class);
			taskBeans.forEach(taskListeners::putIfAbsent);
			Map<String, ExecutionListener> executionBeans = context.getBeansOfType(ExecutionListener.class);
			executionBeans.forEach(executionListeners::putIfAbsent);
			Map<String, FlowableEventListener> engineBeans = context.getBeansOfType(FlowableEventListener.class);
			engineBeans.forEach(engineEventListeners::putIfAbsent);
			initialized = true;
		}
	}

	public TaskListener getTaskListener(String name) {
		ensureInitialized();
		return name == null ? null : taskListeners.get(name);
	}

	public ExecutionListener getExecutionListener(String name) {
		ensureInitialized();
		return name == null ? null : executionListeners.get(name);
	}

	public FlowableEventListener getEngineEventListener(String name) {
		ensureInitialized();
		return name == null ? null : engineEventListeners.get(name);
	}

	public List<String> listTaskListenerNames() {
		ensureInitialized();
		return List.copyOf(taskListeners.keySet());
	}

	public List<String> listExecutionListenerNames() {
		ensureInitialized();
		return List.copyOf(executionListeners.keySet());
	}

	public List<String> listEngineEventListenerNames() {
		ensureInitialized();
		return List.copyOf(engineEventListeners.keySet());
	}

	public Map<String, TaskListener> taskListeners() {
		ensureInitialized();
		return Collections.unmodifiableMap(taskListeners);
	}

}
