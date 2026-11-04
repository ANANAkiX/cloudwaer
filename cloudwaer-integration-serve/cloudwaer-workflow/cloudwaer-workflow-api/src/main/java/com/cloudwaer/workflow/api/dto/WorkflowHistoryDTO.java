package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 历史记录 DTO
 */
@Data
@Schema(description = "历史记录")
public class WorkflowHistoryDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "流程实例ID")
	private String instanceId;

	@Schema(description = "任务ID")
	private String taskId;

	@Schema(description = "节点ID")
	private String nodeId;

	@Schema(description = "节点名称")
	private String nodeName;

	@Schema(description = "节点类型")
	private String nodeType;

	@Schema(description = "动作")
	private String action;

	@Schema(description = "操作人")
	private String operator;

	@Schema(description = "详情/意见")
	private String detail;

	@Schema(description = "结果")
	private String result;

	@Schema(description = "耗时（毫秒）")
	private Long durationMs;

	@Schema(description = "时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

}
