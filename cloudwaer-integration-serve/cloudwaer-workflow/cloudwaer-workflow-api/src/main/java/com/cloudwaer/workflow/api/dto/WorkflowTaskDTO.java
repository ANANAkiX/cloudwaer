package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户任务 DTO
 */
@Data
@Schema(description = "用户任务")
public class WorkflowTaskDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "任务ID")
	private String taskId;

	@Schema(description = "流程实例ID")
	private String instanceId;

	@Schema(description = "流程定义ID")
	private Long defId;

	@Schema(description = "流程标识")
	private String defKey;

	@Schema(description = "流程名称")
	private String defName;

	@Schema(description = "节点ID")
	private String nodeId;

	@Schema(description = "节点名称")
	private String nodeName;

	@Schema(description = "任务名称")
	private String taskName;

	@Schema(description = "任务状态")
	private Integer taskStatus;

	@Schema(description = "状态描述")
	private String statusDesc;

	@Schema(description = "处理人")
	private String assignee;

	@Schema(description = "候选用户")
	private String candidateUsers;

	@Schema(description = "候选组/角色")
	private String candidateGroups;

	@Schema(description = "审批意见")
	private String comment;

	@Schema(description = "审批结果")
	private String result;

	@Schema(description = "到期时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueTime;

	@Schema(description = "认领时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime claimTime;

	@Schema(description = "完成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completeTime;

	@Schema(description = "处理耗时（毫秒）")
	private Long durationMs;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	@Schema(description = "发起人")
	private String startUser;

}
