package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FlowableTaskDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String taskDefinitionKey;

	private String processInstanceId;

	private String processDefinitionKey;

	private String processDefinitionName;

	private String businessKey;

	/**
	 * 流程申请时填写的任务优先级
	 */
	private String priority;

	/**
	 * 流程申请时填写的预期结束时间
	 */
	private LocalDateTime dueTime;

	private String assignee;

	private LocalDateTime createTime;

	private LocalDateTime endTime;

	private String status;

}
