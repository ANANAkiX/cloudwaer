package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FlowableProcessInstanceDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String processDefinitionKey;

	private String processDefinitionName;

	private String businessKey;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String status;

	/**
	 * 发起人
	 */
	private String starter;

	/**
	 * 预期结束时间（流程申请时填写）
	 */
	private LocalDateTime dueTime;

}
