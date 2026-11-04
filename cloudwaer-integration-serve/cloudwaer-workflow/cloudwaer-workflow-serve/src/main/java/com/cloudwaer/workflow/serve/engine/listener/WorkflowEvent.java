package com.cloudwaer.workflow.serve.engine.listener;

import lombok.Data;

/**
 * 领域事件
 */
@Data
public class WorkflowEvent {

	/**
	 * 事件类型: process:start / process:end / task:create / task:complete / task:reject /
	 * node:enter / node:exit
	 */
	private String eventType;

	private String instanceId;

	private String taskId;

	private String nodeId;

	private String nodeName;

	private String operator;

	private Object payload;

}
