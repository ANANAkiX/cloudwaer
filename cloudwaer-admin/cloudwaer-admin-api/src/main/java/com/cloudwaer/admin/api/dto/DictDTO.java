package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class DictDTO {

	private Long id;

	private String type;

	private String name;

	private Integer sort;

	private Integer status;

	private String description;

	private List<DictItemDTO> items;

}