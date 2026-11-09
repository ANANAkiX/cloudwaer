package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 流程定义（JSON DSL）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_definition")
public class WfDefinition extends BaseEntity {

	private String defKey;

	private String defName;

	private String category;

	private Integer version;

	private Integer defStatus;

	private String dslJson;

	private Long formId;

	private String formKey;

	private LocalDateTime endTime;

	private String remark;

}
