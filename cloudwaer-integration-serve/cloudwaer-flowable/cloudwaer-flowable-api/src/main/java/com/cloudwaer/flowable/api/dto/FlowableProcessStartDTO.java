package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FlowableProcessStartDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String processDefinitionKey;

	private String businessKey;

	private Map<String, Object> variables;

}
