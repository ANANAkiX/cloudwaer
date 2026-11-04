package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程节点配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_node")
public class WfNode extends BaseEntity {

	private Long defId;

	private String defKey;

	private Integer defVersion;

	private String nodeId;

	private String nodeName;

	private String nodeType;

	private String assigneeType;

	private String assigneeVal;

	private String multiType;

	private Integer multiCount;

	private Integer dueHours;

	private String formJson;

	private String actionJson;

	private String listenerJson;

	private String configJson;

	private Integer sortOrder;

}
