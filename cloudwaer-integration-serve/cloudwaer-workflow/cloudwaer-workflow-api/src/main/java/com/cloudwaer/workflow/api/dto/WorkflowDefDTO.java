package com.cloudwaer.workflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程定义 DTO
 */
@Data
@Schema(description = "流程定义")
public class WorkflowDefDTO {

	@Schema(description = "ID")
	private Long id;

	@Schema(description = "流程标识")
	private String defKey;

	@Schema(description = "流程名称")
	private String defName;

	@Schema(description = "分类")
	private String category;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "状态 0草稿 1已发布 2已归档 3已下线")
	private Integer defStatus;

	@Schema(description = "流程 DSL（JSON）")
	private String dslJson;

	@Schema(description = "关联表单ID")
	private Long formId;

	@Schema(description = "关联表单标识")
	private String formKey;

	@Schema(description = "流程到期时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "未发布草稿版本ID（主列表聚合标记）")
	private Long draftId;

	@Schema(description = "未发布草稿版本号（主列表聚合标记）")
	private Integer draftVersion;

	@Schema(description = "节点列表")
	private List<WorkflowNodeDTO> nodes;

	@Schema(description = "连线列表")
	private List<WorkflowEdgeDTO> edges;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	@Schema(description = "创建人")
	private String createUser;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;

}
