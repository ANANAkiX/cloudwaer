package com.cloudwaer.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程节点 DTO
 */
@Data
@Schema(description = "流程节点")
public class WorkflowNodeDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "节点ID（DSL内唯一）")
	private String nodeId;

	@Schema(description = "节点名称")
	private String nodeName;

	@Schema(description = "节点类型")
	private String nodeType;

	@Schema(description = "处理人类型 user/role/dept/variable/initiator/leader")
	private String assigneeType;

	@Schema(description = "处理人值（逗号分隔）")
	private String assigneeVal;

	@Schema(description = "多人审批方式 any/countersign/or_sign")
	private String multiType;

	@Schema(description = "会签通过人数")
	private Integer multiCount;

	@Schema(description = "超时小时数")
	private Integer dueHours;

	@Schema(description = "节点级表单覆盖")
	private String formJson;

	@Schema(description = "节点动作配置")
	private String actionJson;

	@Schema(description = "监听器配置")
	private String listenerJson;

	@Schema(description = "节点扩展配置")
	private String configJson;

	@Schema(description = "排序")
	private Integer sortOrder;

	@Schema(description = "画布 X 坐标")
	private Double x;

	@Schema(description = "画布 Y 坐标")
	private Double y;

}
