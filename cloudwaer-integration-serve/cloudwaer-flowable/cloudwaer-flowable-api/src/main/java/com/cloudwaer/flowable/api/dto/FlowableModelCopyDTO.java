package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowableModelCopyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long sourceId;

	private String newModelKey;

	private String newModelName;

}
