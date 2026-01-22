package com.cloudwaer.flowable.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FormFieldDTO implements Serializable {

	private String id;

	private String label;

	private String type;

	private Boolean required;

	private String placeholder;

	private Integer min;

	private Integer max;

	private Integer precision;

	private List<OptionDTO> options;

	private String tip;

}
