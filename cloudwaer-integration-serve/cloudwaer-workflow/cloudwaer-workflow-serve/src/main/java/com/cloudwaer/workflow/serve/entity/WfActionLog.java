package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 动作执行日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_action_log")
public class WfActionLog extends BaseEntity {

	private String instanceId;

	private String taskId;

	private String nodeId;

	private String eventType;

	private String actionType;

	private String actionConfig;

	private Integer execStatus;

	private String execResult;

	private LocalDateTime execTime;

	private Long durationMs;

	private Integer retryCount;

}
