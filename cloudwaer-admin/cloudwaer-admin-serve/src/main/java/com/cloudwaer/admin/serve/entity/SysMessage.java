package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站内信消息主体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {

	/**
	 * 消息类型：NOTICE-公告 / SYSTEM-系统提示 / PRIVATE-私信（预留）
	 */
	private String msgType;

	private String title;

	private String content;

	/**
	 * 业务类型（如 blog_article / blog_comment）
	 */
	private String bizType;

	/**
	 * 业务 ID
	 */
	private String bizId;

	private Long senderId;

	private String senderName;

}
