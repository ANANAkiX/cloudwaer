package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.model.WsEnvelope;
import com.cloudwaer.common.ws.model.WsSessionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

/**
 * WS 服务端处理器：连接注册、hello 帧、心跳应答（ping/pong）、断线清理。
 *
 * <p>
 * 业务推送不在这里做——统一走 {@link WsDispatcher}；本类只关心连接生命周期与链路维护。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class WsServerHandler extends TextWebSocketHandler {

	private final WsSessionManager sessionManager;

	private final ObjectMapper objectMapper;

	private final List<WsConnectionListener> connectionListeners;

	public WsServerHandler(WsSessionManager sessionManager, ObjectMapper objectMapper,
			List<WsConnectionListener> connectionListeners) {
		this.sessionManager = sessionManager;
		this.objectMapper = objectMapper;
		this.connectionListeners = connectionListeners == null ? List.of() : connectionListeners;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Object userIdAttr = session.getAttributes().get(WsHandshakeInterceptor.ATTR_USER_ID);
		if (!(userIdAttr instanceof Long userId)) {
			closeQuietly(session);
			return;
		}
		String username = String.valueOf(session.getAttributes().get(WsHandshakeInterceptor.ATTR_USERNAME));
		Object clientAttr = session.getAttributes().get(WsHandshakeInterceptor.ATTR_CLIENT_ID);
		String clientId = clientAttr == null ? null : String.valueOf(clientAttr);

		WsSessionInfo info = new WsSessionInfo(userId, username, clientId, session, System.currentTimeMillis());
		sessionManager.register(info);

		send(session, WsEnvelope.of("hello", userId, "user:" + userId, Map.of("sessionId", session.getId())));
		for (WsConnectionListener listener : connectionListeners) {
			try {
				listener.onConnected(userId, username);
			}
			catch (Exception e) {
				log.warn("WS 连接回调异常: {}", e.getMessage());
			}
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		touch(session);
		try {
			WsEnvelope envelope = objectMapper.readValue(message.getPayload(), WsEnvelope.class);
			if ("ping".equals(envelope.getType())) {
				send(session, WsEnvelope.of("pong", null, null, null));
			}
			// 其余帧类型预留（如订阅业务频道），暂不处理
		}
		catch (Exception e) {
			log.debug("WS 帧解析失败 sessionId={}: {}", session.getId(), e.getMessage());
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.warn("WS 传输异常 sessionId={}: {}", session.getId(), exception.getMessage());
		sessionManager.unregister(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessionManager.unregister(session);
	}

	/**
	 * 心跳判活入口：收到任意帧刷新活跃时间
	 */
	public void touch(WebSocketSession session) {
		for (WsSessionInfo info : sessionManager.getByUser(extractUserId(session))) {
			if (info.getSession().getId().equals(session.getId())) {
				info.setLastActiveAt(System.currentTimeMillis());
				return;
			}
		}
	}

	private Long extractUserId(WebSocketSession session) {
		Object userId = session.getAttributes().get(WsHandshakeInterceptor.ATTR_USER_ID);
		return userId instanceof Long id ? id : null;
	}

	private void send(WebSocketSession session, WsEnvelope envelope) {
		try {
			if (session.isOpen()) {
				synchronized (session) {
					session.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
				}
			}
		}
		catch (Exception e) {
			log.warn("WS 帧发送失败 sessionId={}: {}", session.getId(), e.getMessage());
		}
	}

	private void closeQuietly(WebSocketSession session) {
		try {
			session.close(CloseStatus.POLICY_VIOLATION);
		}
		catch (Exception ignored) {
			// ignore
		}
	}

}
