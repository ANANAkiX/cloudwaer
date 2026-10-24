package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞切换结果DTO
 *
 * @author cloudwaer
 */
@Data
public class LikeToggleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前是否已点赞
	 */
	private Boolean liked;

	/**
	 * 最新点赞数
	 */
	private Integer likeCount;

}
