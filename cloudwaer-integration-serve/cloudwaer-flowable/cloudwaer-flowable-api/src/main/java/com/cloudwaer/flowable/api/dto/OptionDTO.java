package com.cloudwaer.flowable.api.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OptionDTO implements Serializable {

	private String label;

	private Object value;

}
