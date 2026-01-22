package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_model")
public class WfModel extends BaseEntity {

	private String modelKey;

	private String modelName;

	private String category;

	private Integer version;

	private Integer modelStatus;

	@TableField("bpmn_xml")
	private String bpmnXml;

	@TableField("form_json")
	private String formJson;

	@TableField("node_actions_json")
	private String nodeActionsJson;

	private String remark;

	/**
	 * 模型到期时间（用于流程申请默认到期时间/后续超时处理）
	 */
	@TableField("end_time")
	private LocalDateTime endTime;

}
