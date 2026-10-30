package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 站内信发送 DTO（Feign 内部调用：业务模块 → admin）
 *
 * @author cloudwaer
 */
@Data
public class SendMessageDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 收件人用户 ID 列表
	 */
	private List<Long> userIds;

	/**
	 * 消息类型：NOTICE-公告 / SYSTEM-系统提示 / PRIVATE-私信（预留）
	 */
	private String msgType = "SYSTEM";

	private String title;

	private String content;

	/**
	 * 业务类型（如 blog_article / blog_comment），用于前端跳回来源
	 */
	private String bizType;

	/**
	 * 业务 ID
	 */
	private String bizId;

	/**
	 * 发送人（系统消息可为空）
	 */
	private Long senderId;

	private String senderName;

}
