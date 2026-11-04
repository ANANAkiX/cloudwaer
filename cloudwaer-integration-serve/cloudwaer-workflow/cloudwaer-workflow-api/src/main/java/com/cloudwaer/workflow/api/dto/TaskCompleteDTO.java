package com.cloudwaer.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 任务操作请求
 */
@Data
@Schema(description = "任务操作请求")
public class TaskCompleteDTO {

	@NotBlank(message = "任务ID不能为空")
	@Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
	private String taskId;

	@Schema(description = "操作类型 approve/reject/transfer/return/countersign/cc")
	private String action;

	@Schema(description = "审批意见")
	private String comment;

	@Schema(description = "转办/加签目标人")
	private String targetUser;

	@Schema(description = "表单数据（JSON）")
	private String formJson;

	@Schema(description = "流程变量")
	private Map<String, Object> variables;

}
