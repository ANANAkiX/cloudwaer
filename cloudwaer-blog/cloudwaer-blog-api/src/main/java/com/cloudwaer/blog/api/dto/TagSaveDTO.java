package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客标签保存/更新DTO
 *
 * @author cloudwaer
 */
@Data
public class TagSaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标签ID（新增时为空，更新时必填）
	 */
	private Long id;

	/**
	 * 标签名
	 */
	@NotBlank(message = "标签名不能为空")
	@Size(max = 64, message = "标签名最长64字符")
	private String name;

	/**
	 * 别名
	 */
	@Size(max = 64, message = "别名最长64字符")
	private String slug;

}
