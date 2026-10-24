package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 微论坛话题保存DTO
 *
 * @author cloudwaer
 */
@Data
public class TopicSaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标题
	 */
	@NotBlank(message = "标题不能为空")
	@Size(max = 200, message = "标题最长200字符")
	private String title;

	/**
	 * 正文
	 */
	@NotBlank(message = "内容不能为空")
	@Size(max = 10000, message = "内容过长")
	private String content;

}
