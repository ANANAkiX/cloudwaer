package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 站内信收件箱（一人一行，支撑已读/删除）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_recipient")
public class SysMessageRecipient extends BaseEntity {

	private Long messageId;

	private Long userId;

	/**
	 * 是否已读：0-未读 1-已读
	 */
	private Integer readFlag;

	private LocalDateTime readTime;

}
