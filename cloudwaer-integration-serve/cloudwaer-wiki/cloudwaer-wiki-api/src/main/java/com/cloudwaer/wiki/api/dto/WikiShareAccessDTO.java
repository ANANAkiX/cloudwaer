package com.cloudwaer.wiki.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class WikiShareAccessDTO {

	private String token;

	private String targetType;

	private Long targetId;

	private Integer accessLevel;

	private String title;

	private String content;

	private List<WikiDocDTO> docs;

	/** true=需要先输入访问密码（未带密码时的探测响应） */
	private Boolean needPassword;

}
