package com.cloudwaer.common.ws;

import com.cloudwaer.common.core.util.RedisUtil;
import com.cloudwaer.common.ws.model.WsEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * WS 消息分发器：业务侧唯一入口。
 *
 * <p>
 * 推送 = 本实例连接表直投 + 发布到 Redis 频道 {@link #DISPATCH_CHANNEL} 由其他实例的 {@link WsRedisRelay}
 * 收到后投给各自本地连接。发送方无需感知部署拓扑（单体/微服务行为一致）。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class WsDispatcher {

	/**
	 * 跨实例分发频道
	 */
	public static final String DISPATCH_CHANNEL = "ws:dispatch";

	/**
	 * 本实例标识：中继收到自己发出的帧时跳过（本地已直投过），防重复送达
	 */
	private final String originId = java.util.UUID.randomUUID().toString().replace("-", "");

	private final WsSessionManager sessionManager;

	@Autowired(required = false)
	private RedisUtil redisUtil;

	@Autowired(required = false)
	private ObjectMapper objectMapper;

	public WsDispatcher(WsSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * 推送给指定用户（其所有在线连接/多端）
	 */
	public void pushUser(Long userId, String type, Map<String, Object> payload) {
		if (userId == null) {
			return;
		}
		WsEnvelope envelope = WsEnvelope.of(type, userId, "user:" + userId, payload);
		envelope.setOrigin(originId);
		deliverLocal(envelope);
		publishRelay(envelope);
	}

	/**
	 * 广播给所有在线连接
	 */
	public void pushAll(String type, Map<String, Object> payload) {
		WsEnvelope envelope = WsEnvelope.of(type, null, "all", payload);
		envelope.setOrigin(originId);
		deliverLocal(envelope);
		publishRelay(envelope);
	}

	/**
	 * 是否本实例产生的帧（中继回声去重）
	 */
	public boolean isOwnOrigin(WsEnvelope envelope) {
		return envelope != null && originId.equals(envelope.getOrigin());
	}

	/**
	 * 仅本实例直投（由 {@link WsRedisRelay} 收到跨实例消息后调用，勿从业务侧直接调用）
	 */
	public void deliverLocal(WsEnvelope envelope) {
		String json = toJson(envelope);
		if (json == null) {
			return;
		}
		if (envelope.getTargetUserId() != null) {
			for (var info : sessionManager.getByUser(envelope.getTargetUserId())) {
				send(info.getSession(), json);
			}
		}
		else {
			for (var info : sessionManager.all()) {
				send(info.getSession(), json);
			}
		}
	}

	private void publishRelay(WsEnvelope envelope) {
		if (redisUtil == null || !redisUtil.isAvailable()) {
			return;
		}
		try {
			redisUtil.publish(DISPATCH_CHANNEL, toJson(envelope));
		}
		catch (Exception e) {
			log.warn("WS 跨实例分发失败: {}", e.getMessage());
		}
	}

	private void send(WebSocketSession session, String json) {
		try {
			if (session.isOpen()) {
				synchronized (session) {
					session.sendMessage(new TextMessage(json));
				}
			}
		}
		catch (Exception e) {
			log.warn("WS 发送失败 sessionId={}: {}", session.getId(), e.getMessage());
		}
	}

	private String toJson(WsEnvelope envelope) {
		try {
			return getObjectMapper().writeValueAsString(envelope);
		}
		catch (Exception e) {
			log.warn("WS 信封序列化失败: {}", e.getMessage());
			return null;
		}
	}

	private ObjectMapper getObjectMapper() {
		return objectMapper != null ? objectMapper : new ObjectMapper();
	}

}
