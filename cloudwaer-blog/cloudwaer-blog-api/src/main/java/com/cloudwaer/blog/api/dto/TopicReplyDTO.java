package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 微论坛回复DTO
 *
 * @author cloudwaer
 */
@Data
public class TopicReplyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 回复ID
	 */
	private Long id;

	/**
	 * 话题ID
	 */
	private Long topicId;

	/**
	 * 父回复ID
	 */
	private Long parentId;

	/**
	 * 根回复ID
	 */
	private Long rootId;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * 是否已点赞
	 */
	private Boolean liked;

	/**
	 * 时间
	 */
	private LocalDateTime createTime;

	/**
	 * 楼中楼
	 */
	private List<TopicReplyDTO> replies = new ArrayList<>();

}
