package com.cloudwaer.wiki.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class WikiDocDTO {

	private Long id;

	private Long spaceId;

	private Long parentId;

	private String title;

	private String content;

	private String summary;

	private Integer sort;

	private Integer publicRead;

	private Long authorId;

	private String authorName;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	private List<WikiDocDTO> children;

}
