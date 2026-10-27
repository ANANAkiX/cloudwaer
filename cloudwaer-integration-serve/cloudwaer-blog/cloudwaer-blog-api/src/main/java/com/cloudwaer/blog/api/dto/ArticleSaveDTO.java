package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 博客文章保存/更新DTO
 *
 * @author cloudwaer
 */
@Data
public class ArticleSaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 文章ID（新增时为空，更新时必填）
	 */
	private Long id;

	/**
	 * 文章标题
	 */
	@NotBlank(message = "标题不能为空")
	private String title;

	/**
	 * 文章摘要
	 */
	private String summary;

	/**
	 * 正文（Markdown）
	 */
	@NotBlank(message = "正文不能为空")
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
	 * 合集ID（可空；传 null 或 0 表示移出合集）
	 */
	private Long collectionId;

	/**
	 * 合集内排序（可空，空则按发布时间）
	 */
	private Integer collectionSort;

	/**
	 * 标签ID列表
	 */
	private List<Long> tagIds;

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

}
