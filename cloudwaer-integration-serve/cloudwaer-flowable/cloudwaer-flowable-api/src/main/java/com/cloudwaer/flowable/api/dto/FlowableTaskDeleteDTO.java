package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowableTaskDeleteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String taskId;

}
