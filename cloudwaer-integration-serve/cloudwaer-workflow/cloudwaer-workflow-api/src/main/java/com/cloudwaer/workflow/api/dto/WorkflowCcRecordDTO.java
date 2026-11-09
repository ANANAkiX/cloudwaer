package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抄送记录 DTO
 */
@Data
@Schema(description = "抄送记录")
public class WorkflowCcRecordDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "流程实例ID")
	private String instanceId;

	@Schema(description = "流程标识")
	private String defKey;

	@Schema(description = "流程名称")
	private String defName;

	@Schema(description = "流程标题")
	private String title;

	@Schema(description = "节点ID")
	private String nodeId;

	@Schema(description = "抄送类型")
	private String ccType;

	@Schema(description = "抄送目标")
	private String ccTarget;

	@Schema(description = "抄送人")
	private String ccBy;

	@Schema(description = "抄送时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	@Schema(description = "已读状态 0未读 1已读")
	private Integer readStatus;

	@Schema(description = "已读时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime readTime;

	@Schema(description = "实例状态")
	private Integer instStatus;

	@Schema(description = "实例状态描述")
	private String statusDesc;

}
