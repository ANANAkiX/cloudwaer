package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_node_action")
public class WfNodeAction extends BaseEntity {

	private Long modelId;

	private String modelKey;

	private Integer modelVersion;

	private String nodeId;

	private String nodeName;

	private String eventType;

	private String actionType;

	@TableField("action_config")
	private String actionConfig;

	private Integer enabled;

}
