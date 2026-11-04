package com.cloudwaer.workflow.serve.engine;

import lombok.Data;

/**
 * 流程 DSL 节点模型（解析 JSON DSL 后的运行时表示）
 */
@Data
public class DslNode {

	private String nodeId;

	private String nodeName;

	private String nodeType;

	private String assigneeType;

	private String assigneeVal;

	private String multiType;

	private Integer multiCount;

	private Integer dueHours;

	private String formJson;

	private String actionJson;

	private String listenerJson;

	private String configJson;

	private Double x;

	private Double y;

}
