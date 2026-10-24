package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 博客标签DTO
 *
 * @author cloudwaer
 */
@Data
public class TagDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标签ID
	 */
	private Long id;

	/**
	 * 标签名
	 */
	private String name;

	/**
	 * 别名
	 */
	private String slug;

	/**
	 * 使用数（已发布文章引用数，标签云用）
	 */
	private Long useCount;

}
