package com.cloudwaer.admin.serve.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收件箱条目
 *
 * @author cloudwaer
 */
@Data
public class MessageItemVO {

	private Long recipientId;

	private Long messageId;

	/**
	 * 消息类型：NOTICE / SYSTEM / PRIVATE
	 */
	private String msgType;

	private String title;

	private String content;

	private String bizType;

	private String bizId;

	private String senderName;

	private Integer readFlag;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

}
