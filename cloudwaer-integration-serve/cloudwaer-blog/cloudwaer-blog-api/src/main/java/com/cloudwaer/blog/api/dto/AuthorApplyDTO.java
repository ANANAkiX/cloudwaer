package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客主申请DTO
 *
 * @author cloudwaer
 */
@Data
public class AuthorApplyDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 申请ID
	 */
	private Long id;

	/**
	 * 申请人用户ID
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 申请理由
	 */
	@Size(max = 255, message = "申请理由最长255字符")
	private String reason;

	/**
	 * 状态：0-待处理，1-已通过，2-已拒绝
	 */
	private Integer applyStatus;

	/**
	 * 处理人
	 */
	private String handleUser;

	/**
	 * 处理时间
	 */
	private LocalDateTime handleTime;

	/**
	 * 处理备注
	 */
	private String handleRemark;

	/**
	 * 申请时间
	 */
	private LocalDateTime createTime;

	/**
	 * 当前是否真实持有博客主/管理员身份（仅 myApply 填充；申请已通过但角色被取消时为 false）
	 */
	private Boolean bloggerActive;

}
