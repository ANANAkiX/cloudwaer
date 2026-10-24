package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客评论审核DTO
 *
 * @author cloudwaer
 */
@Data
public class CommentAuditDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 评论ID
	 */
	@NotNull(message = "评论ID不能为空")
	private Long id;

	/**
	 * 审核结果：1-已通过，2-已拒绝
	 */
	@NotNull(message = "审核结果不能为空")
	private Integer auditStatus;

}
