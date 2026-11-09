package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全链路历史
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_history")
public class WfHistory extends BaseEntity {

	private String instanceId;

	private String taskId;

	private String nodeId;

	private String nodeName;

	private String nodeType;

	private String action;

	private String operator;

	private String detail;

	private String result;

	private Long durationMs;

	private String extraJson;

}
