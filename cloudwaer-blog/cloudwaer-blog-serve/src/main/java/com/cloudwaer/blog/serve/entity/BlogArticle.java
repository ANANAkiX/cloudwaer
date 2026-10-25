package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 博客文章实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_article")
public class BlogArticle extends BaseEntity {

	/**
	 * 文章标题
	 */
	private String title;

	/**
	 * 文章摘要
	 */
	private String summary;

	/**
	 * 正文（Markdown）
	 */
	private String content;

	/**
	 * 封面图URL
	 */
	private String coverUrl;

	/**
	 * 分类ID
	 */
	private Long categoryId;

	/**
	 * 合集ID（blog_collection.id）
	 */
	private Long collectionId;

	/**
	 * 合集内排序，NULL=按发布时间
	 */
	private Integer collectionSort;

	/**
	 * 作者ID（sys_user.id）
	 */
	private Long authorId;

	/**
	 * 作者昵称（冗余）
	 */
	private String authorName;

	/**
	 * author avatar
	 */
	private String authorAvatar;

	/**
	 * 发布状态：0-草稿，1-已发布，2-已下线
	 */
	private Integer publishStatus;

	/**
	 * 是否置顶：0-否，1-是
	 */
	private Integer isTop;

	/**
	 * 是否推荐：0-否，1-是
	 */
	private Integer isRecommend;

	/**
	 * 浏览量
	 */
	private Integer viewCount;

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * 评论数
	 */
	private Integer commentCount;

	/**
	 * 热度分
	 */
	private Double heatScore;

	/**
	 * 发布时间
	 */
	private LocalDateTime publishTime;

}
