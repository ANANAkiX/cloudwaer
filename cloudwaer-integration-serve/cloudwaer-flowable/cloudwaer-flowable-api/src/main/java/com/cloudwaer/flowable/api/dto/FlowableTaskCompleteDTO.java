package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FlowableTaskCompleteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String taskId;

	private String comment;

	private Map<String, Object> variables;

}
