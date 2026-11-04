package com.cloudwaer.workflow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 启动流程请求
 */
@Data
@Schema(description = "启动流程请求")
public class ProcessStartDTO {

	@NotBlank(message = "流程标识不能为空")
	@Schema(description = "流程标识 defKey", requiredMode = Schema.RequiredMode.REQUIRED)
	private String defKey;

	@Schema(description = "业务键")
	private String businessKey;

	@Schema(description = "流程标题")
	private String title;

	@Schema(description = "表单数据（JSON）")
	private String formJson;

	@Schema(description = "优先级 low/normal/high/urgent")
	private String priority;

	@Schema(description = "流程变量")
	private Map<String, Object> variables;

}
