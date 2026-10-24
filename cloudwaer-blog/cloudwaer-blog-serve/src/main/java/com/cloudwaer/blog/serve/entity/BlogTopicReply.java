package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微论坛回复实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_topic_reply")
public class BlogTopicReply extends BaseEntity {

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
	 * 头像
	 */
	private String avatar;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * IP
	 */
	private String ip;

}
