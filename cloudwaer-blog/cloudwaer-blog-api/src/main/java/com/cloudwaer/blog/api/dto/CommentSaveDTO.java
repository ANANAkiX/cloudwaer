package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 博客评论发表DTO
 *
 * @author cloudwaer
 */
@Data
public class CommentSaveDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 文章ID
	 */
	@NotNull(message = "文章ID不能为空")
	private Long articleId;

	/**
	 * 父评论ID，0-顶级评论
	 */
	private Long parentId;

	/**
	 * 根评论ID（楼中楼归属），0-顶级评论
	 */
	private Long rootId;

	/**
	 * 评论内容
	 */
	@NotBlank(message = "评论内容不能为空")
	@Size(max = 1000, message = "评论内容最长1000字符")
	private String content;

	/**
	 * 附图URL列表（/blog/uploads/**，最多3张）
	 */
	@Size(max = 3, message = "评论图片最多3张")
	private List<String> images;

}
