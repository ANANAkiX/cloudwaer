package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客合集保存DTO
 *
 * @author cloudwaer
 */
@Data
public class CollectionSaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 合集ID（新增为空，更新必填）
	 */
	private Long id;

	/**
	 * 合集名称
	 */
	@NotBlank(message = "合集名称不能为空")
	@Size(max = 100, message = "合集名称最长100字符")
	private String name;

	/**
	 * 合集简介
	 */
	@Size(max = 500, message = "简介最长500字符")
	private String description;

	/**
	 * 封面图URL
	 */
	private String coverUrl;

	/**
	 * 展示排序
	 */
	private Integer sort;

	/**
	 * 是否精选：0-否，1-是
	 */
	private Integer isFeatured;

	/**
	 * 可见性：0-私密，1-公开
	 */
	private Integer visibility;

	/**
	 * 扩展JSON
	 */
	private String extra;

}
