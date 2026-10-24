package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 微论坛话题实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_topic")
public class BlogTopic extends BaseEntity {

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 正文
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

}
