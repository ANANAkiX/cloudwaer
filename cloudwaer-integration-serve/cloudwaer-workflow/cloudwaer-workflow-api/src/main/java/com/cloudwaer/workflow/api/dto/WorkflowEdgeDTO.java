package com.cloudwaer.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程连线（边）DTO
 */
@Data
@Schema(description = "流程连线")
public class WorkflowEdgeDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "连线ID")
	private String edgeId;

	@Schema(description = "源节点ID")
	private String sourceId;

	@Schema(description = "目标节点ID")
	private String targetId;

	@Schema(description = "连线名称")
	private String edgeName;

	@Schema(description = "条件表达式")
	private String edgeCondition;

	@Schema(description = "排序")
	private Integer sortOrder;

}
