package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 博客分类DTO
 *
 * @author cloudwaer
 */
@Data
public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分类ID
	 */
	private Long id;

	/**
	 * 分类名称
	 */
	private String name;

	/**
	 * 别名
	 */
	private String slug;

	/**
	 * 分类描述
	 */
	private String description;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 文章数（已发布文章数，侧栏筛选用）
	 */
	private Long articleCount;

}
