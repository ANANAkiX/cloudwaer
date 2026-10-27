package com.cloudwaer.wiki.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WikiDocSaveDTO {

	private Long id;

	@NotNull(message = "空间ID不能为空")
	private Long spaceId;

	private Long parentId;

	@NotBlank(message = "标题不能为空")
	@Size(max = 200)
	private String title;

	@Size(max = 500000)
	private String content;

	@Size(max = 500)
	private String summary;

	private Integer sort;

	/** NULL=跟随空间 */
	private Integer publicRead;

}
