package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程表单定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_form")
public class WfForm extends BaseEntity {

	private String formKey;

	private String formName;

	private String category;

	private Integer version;

	private Integer formStatus;

	private String schemaJson;

	private String remark;

}
