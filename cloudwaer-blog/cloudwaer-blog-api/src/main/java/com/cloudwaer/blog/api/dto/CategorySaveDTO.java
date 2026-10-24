package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客分类保存/更新DTO
 *
 * @author cloudwaer
 */
@Data
public class CategorySaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 分类ID（新增时为空，更新时必填）
	 */
	private Long id;

	/**
	 * 分类名称
	 */
	@NotBlank(message = "分类名称不能为空")
	@Size(max = 64, message = "分类名称最长64字符")
	private String name;

	/**
	 * 别名
	 */
	@Size(max = 64, message = "别名最长64字符")
	private String slug;

	/**
	 * 分类描述
	 */
	@Size(max = 255, message = "描述最长255字符")
	private String description;

	/**
	 * 排序
	 */
	private Integer sort;

}
