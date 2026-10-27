package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 微论坛回复保存DTO
 *
 * @author cloudwaer
 */
@Data
public class TopicReplySaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 话题ID
	 */
	@NotNull(message = "话题ID不能为空")
	private Long topicId;

	/**
	 * 父回复ID，0-顶级
	 */
	private Long parentId;

	/**
	 * 根回复ID，0-顶级
	 */
	private Long rootId;

	/**
	 * 内容
	 */
	@NotBlank(message = "回复内容不能为空")
	@Size(max = 1000, message = "回复最长1000字符")
	private String content;

}
