package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客主暂停状态DTO
 *
 * @author cloudwaer
 */
@Data
public class BloggerStatusDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 当前是否处于暂停中（已到期或已解除则为 false）
	 */
	private Boolean suspended;

	/**
	 * 暂停截止时间；空表示永久
	 */
	private LocalDateTime suspendUntil;

	/**
	 * 暂停原因
	 */
	private String suspendReason;

	/**
	 * 最近操作人
	 */
	private String handleUser;

	/**
	 * 最近更新时间
	 */
	private LocalDateTime updateTime;

}
