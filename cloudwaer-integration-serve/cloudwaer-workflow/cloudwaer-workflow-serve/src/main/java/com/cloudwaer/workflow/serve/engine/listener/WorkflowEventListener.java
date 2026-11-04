package com.cloudwaer.workflow.serve.engine.listener;

/**
 * 领域事件监听器 SPI 接口。
 * <p>
 * 每个监听器通过 Spring Bean 注册，由 {@code EventPublisher} 统一分发。
 */
public interface WorkflowEventListener {

	/**
	 * 监听的事件类型（支持通配符 "*"）
	 */
	String getEventType();

	/**
	 * 处理事件
	 */
	void onEvent(WorkflowEvent event);

}
