package com.cloudwaer.workflow.serve.engine;

import lombok.Data;

/**
 * 流程 DSL 连线模型
 */
@Data
public class DslEdge {

	private String edgeId;

	private String sourceId;

	private String targetId;

	private String edgeName;

	private String condition;

	private Integer sortOrder;

}
