package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 博客主申请实体（MVP 打标）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_author_apply")
public class BlogAuthorApply extends BaseEntity {

	/**
	 * 申请人用户ID
	 */
	private Long userId;

	/**
	 * 用户名（冗余）
	 */
	private String username;

	/**
	 * 昵称（冗余）
	 */
	private String nickname;

	/**
	 * 申请理由
	 */
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

}
