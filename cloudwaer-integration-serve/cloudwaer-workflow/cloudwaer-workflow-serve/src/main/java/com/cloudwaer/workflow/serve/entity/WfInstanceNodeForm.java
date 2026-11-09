package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实例节点表单关联表：存储每个节点的表单 schema + 提交数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_instance_node_form")
public class WfInstanceNodeForm extends BaseEntity {

	/** 流程实例ID */
	private String instanceId;

	/** 节点ID（DSL） */
	private String nodeId;

	/** 节点主键（wf_node.id） */
	private Long nodePk;

	/** 关联表单ID */
	private Long formId;

	/** 表单 schema JSON */
	private String formSchema;

	/** 表单数据 JSON（审批提交后写入，驳回清空） */
	private String formData;

}
