package com.cloudwaer.wiki.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WikiSpaceSaveDTO {

	private Long id;

	@NotBlank(message = "空间名称不能为空")
	@Size(max = 100)
	private String name;

	@NotBlank(message = "别名不能为空")
	@Size(max = 100)
	private String slug;

	@Size(max = 500)
	private String description;

	private Integer publicRead;

	private Integer sort;

}
