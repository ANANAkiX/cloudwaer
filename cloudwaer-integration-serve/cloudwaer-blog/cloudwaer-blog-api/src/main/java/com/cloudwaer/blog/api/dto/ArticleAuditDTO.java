package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客文章审核DTO
 *
 * @author cloudwaer
 */
@Data
public class ArticleAuditDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 文章ID
	 */
	@NotNull(message = "文章ID不能为空")
	private Long id;

	/**
	 * 审核结果：1-通过（上线），2-拒绝（回草稿）
	 */
	@NotNull(message = "审核结果不能为空")
	private Integer auditStatus;

	/**
	 * 审核意见（拒绝时作为原因反馈给作者）
	 */
	private String remark;

}
