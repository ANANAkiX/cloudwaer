package com.cloudwaer.wiki.api.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WikiSpaceDTO {

	private Long id;

	private String name;

	private String slug;

	private String description;

	private Integer publicRead;

	private Integer sort;

	private Integer docCount;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
