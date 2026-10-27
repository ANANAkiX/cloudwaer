package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 微论坛话题DTO
 *
 * @author cloudwaer
 */
@Data
public class TopicDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 话题ID
	 */
	private Long id;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 正文（列表可不下发）
	 */
	private String content;

	/**
	 * 发帖用户ID
	 */
	private Long userId;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 浏览量
	 */
	private Integer viewCount;

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * 回复数
	 */
	private Integer replyCount;

	/**
	 * 是否置顶
	 */
	private Integer isTop;

	/**
	 * 是否加精
	 */
	private Integer isEssence;

	/**
	 * 最后回复时间
	 */
	private LocalDateTime lastReplyTime;

	/**
	 * 发帖时间
	 */
	private LocalDateTime createTime;

	/**
	 * 当前用户是否已点赞（登录态下发）
	 */
	private Boolean liked;

}
