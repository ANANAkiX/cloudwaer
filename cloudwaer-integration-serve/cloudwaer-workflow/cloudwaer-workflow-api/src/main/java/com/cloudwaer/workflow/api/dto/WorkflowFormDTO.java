package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表单定义 DTO
 */
@Data
@Schema(description = "表单定义")
public class WorkflowFormDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "表单标识")
	private String formKey;

	@Schema(description = "表单名称")
	private String formName;

	@Schema(description = "分类")
	private String category;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "状态 0草稿 1启用 2停用")
	private Integer formStatus;

	@Schema(description = "表单 JSON Schema")
	private String schemaJson;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;

}
