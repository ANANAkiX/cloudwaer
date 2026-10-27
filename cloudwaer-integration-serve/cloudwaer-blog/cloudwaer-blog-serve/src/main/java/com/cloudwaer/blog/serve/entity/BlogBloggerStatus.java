package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 博客主暂停状态（方案B：不摘角色，仅在发布链路拦截；到期读时即视为恢复）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_blogger_status")
public class BlogBloggerStatus extends BaseEntity {

	/**
	 * 用户ID（sys_user.id）
	 */
	private Long userId;

	/**
	 * 暂停截止时间；NULL 表示永久暂停
	 */
	private LocalDateTime suspendUntil;

	/**
	 * 暂停原因
	 */
	private String suspendReason;

	/**
	 * 是否已解除：0-暂停生效中，1-已解除（保留行便于审计）
	 */
	private Integer revoked;

	/**
	 * 最近操作人（管理员用户名）
	 */
	private String handleUser;

}
