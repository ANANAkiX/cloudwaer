package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客主暂停请求DTO
 *
 * @author cloudwaer
 */
@Data
public class BloggerSuspendDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 被暂停的用户ID
	 */
	@NotNull(message = "用户ID不能为空")
	private Long userId;

	/**
	 * 暂停截止时间；为空表示永久暂停
	 */
	private LocalDateTime suspendUntil;

	/**
	 * 暂停原因（回执给本人与管理员）
	 */
	private String reason;

}
