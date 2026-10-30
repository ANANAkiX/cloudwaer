package com.cloudwaer.common.ws.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 一条 WS 连接的会话信息
 *
 * @author cloudwaer
 */
@Data
@AllArgsConstructor
public class WsSessionInfo {

	private Long userId;

	private String username;

	private String clientId;

	private WebSocketSession session;

	/**
	 * 最近一次收到客户端帧的时间（心跳判活）
	 */
	private volatile long lastActiveAt;

}
