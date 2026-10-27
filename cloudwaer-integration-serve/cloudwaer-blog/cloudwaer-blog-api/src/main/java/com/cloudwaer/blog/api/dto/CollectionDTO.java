package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客合集DTO
 *
 * @author cloudwaer
 */
@Data
public class CollectionDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 合集ID
	 */
	private Long id;

	/**
	 * 合集名称
	 */
	private String name;

	/**
	 * 合集简介
	 */
	private String description;

	/**
	 * 封面图URL
	 */
	private String coverUrl;

	/**
	 * 作者ID
	 */
	private Long authorId;

	/**
	 * 作者昵称
	 */
	private String authorName;

	/**
	 * 文章数（已发布）
	 */
	private Integer articleCount;

	/**
	 * 展示排序
	 */
	private Integer sort;

	/**
	 * 是否精选
	 */
	private Integer isFeatured;

	/**
	 * 可见性：0-私密，1-公开
	 */
	private Integer visibility;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

}
