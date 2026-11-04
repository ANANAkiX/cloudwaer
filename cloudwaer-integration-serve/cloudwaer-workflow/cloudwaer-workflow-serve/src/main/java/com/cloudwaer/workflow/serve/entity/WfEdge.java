package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程连线（边）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_edge")
public class WfEdge extends BaseEntity {

	private Long defId;

	private String defKey;

	private Integer defVersion;

	private String edgeId;

	private String sourceId;

	private String targetId;

	private String edgeName;

	private String edgeCondition;

	private Integer sortOrder;

}
