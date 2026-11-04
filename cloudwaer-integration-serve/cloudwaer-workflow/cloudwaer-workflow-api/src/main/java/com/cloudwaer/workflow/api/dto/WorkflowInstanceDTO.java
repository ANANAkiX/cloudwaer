package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例 DTO
 */
@Data
@Schema(description = "流程实例")
public class WorkflowInstanceDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "流程实例ID")
	private String instanceId;

	@Schema(description = "流程定义ID")
	private Long defId;

	@Schema(description = "流程标识")
	private String defKey;

	@Schema(description = "流程版本")
	private Integer defVersion;

	@Schema(description = "流程名称")
	private String defName;

	@Schema(description = "业务键")
	private String businessKey;

	@Schema(description = "流程标题")
	private String title;

	@Schema(description = "表单数据")
	private String formJson;

	@Schema(description = "状态")
	private Integer instStatus;

	@Schema(description = "状态描述")
	private String statusDesc;

	@Schema(description = "优先级")
	private String priority;

	@Schema(description = "预计完成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueTime;

	@Schema(description = "发起人")
	private String startUser;

	@Schema(description = "发起时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@Schema(description = "结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	@Schema(description = "当前活动节点")
	private String currentNodes;

	@Schema(description = "流程变量")
	private String varsJson;

}
