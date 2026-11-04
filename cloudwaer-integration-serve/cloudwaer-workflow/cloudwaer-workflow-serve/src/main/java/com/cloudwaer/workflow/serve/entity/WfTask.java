package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户任务
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_task")
public class WfTask extends BaseEntity {

	private String taskId;

	private String instanceId;

	private Long defId;

	private String defKey;

	private String nodeId;

	private String nodeName;

	private String taskName;

	private Integer taskStatus;

	private String assignee;

	private String candidateUsers;

	private String candidateGroups;

	private String multiType;

	private String formJson;

	private String comment;

	private String result;

	private LocalDateTime dueTime;

	private LocalDateTime claimTime;

	private LocalDateTime completeTime;

	private Long durationMs;

}
