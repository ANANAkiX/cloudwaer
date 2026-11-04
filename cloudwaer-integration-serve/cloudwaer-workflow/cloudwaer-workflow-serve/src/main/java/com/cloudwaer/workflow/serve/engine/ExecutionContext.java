package com.cloudwaer.workflow.serve.engine;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 节点执行上下文
 */
@Data
public class ExecutionContext {

	private String instanceId;

	private Long defId;

	private String defKey;

	private Integer defVersion;

	private String currentNodeId;

	private String currentTaskId;

	private String operator;

	private Map<String, Object> variables = new HashMap<>();

	/** 并行汇聚计数器：nodeId → 已到达的分支数 */
	private Map<String, Integer> arrivalCount = new HashMap<>();

	public void setVariable(String key, Object value) {
		this.variables.put(key, value);
	}

	public Object getVariable(String key) {
		return variables.get(key);
	}

}
