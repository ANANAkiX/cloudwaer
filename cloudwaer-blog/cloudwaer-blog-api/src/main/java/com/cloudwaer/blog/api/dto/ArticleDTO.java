package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章DTO
 *
 * @author cloudwaer
 */
@Data
public class ArticleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 文章ID（序列化为字符串，避免前端精度丢失）
	 */
	private Long id;

	/**
	 * 文章标题
	 */
	private String title;

	/**
	 * 文章摘要
	 */
	private String summary;

	/**
	 * 正文（Markdown，列表查询时为空）
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
	 * 作者ID
	 */
	private Long authorId;

	/**
	 * 作者昵称
	 */
	private String authorName;

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

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 标签列表
	 */
	private List<ArticleTagRefDTO> tags;

}
