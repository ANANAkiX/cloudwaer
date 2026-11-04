package com.cloudwaer.workflow.serve.engine.action;

import lombok.Data;

/**
 * 动作执行上下文
 */
@Data
public class ActionContext {

	private String instanceId;

	private String taskId;

	private String nodeId;

	private String eventType;

	private String operator;

	private String configJson;

}
