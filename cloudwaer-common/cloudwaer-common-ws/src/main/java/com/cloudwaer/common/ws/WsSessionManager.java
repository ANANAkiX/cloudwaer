package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.model.WsSessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本实例的 WS 连接注册表（userId -&gt; 会话集合，同一用户允许多端多连接）。
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class WsSessionManager {

	private final Map<String, WsSessionInfo> sessionsById = new ConcurrentHashMap<>();

	private final Map<Long, List<WsSessionInfo>> sessionsByUser = new ConcurrentHashMap<>();

	public void register(WsSessionInfo info) {
		sessionsById.put(info.getSession().getId(), info);
		sessionsByUser.computeIfAbsent(info.getUserId(), k -> new ArrayList<>()).add(info);
		log.info("WS 连接建立: userId={}, username={}, sessionId={}, online={}", info.getUserId(), info.getUsername(),
				info.getSession().getId(), sessionsById.size());
	}

	public void unregister(WebSocketSession session) {
		WsSessionInfo removed = sessionsById.remove(session.getId());
		if (removed != null) {
			List<WsSessionInfo> list = sessionsByUser.get(removed.getUserId());
			if (list != null) {
				list.removeIf(info -> info.getSession().getId().equals(session.getId()));
				if (list.isEmpty()) {
					sessionsByUser.remove(removed.getUserId());
				}
			}
			log.info("WS 连接断开: userId={}, sessionId={}, online={}", removed.getUserId(), session.getId(),
					sessionsById.size());
		}
	}

	public List<WsSessionInfo> getByUser(Long userId) {
		List<WsSessionInfo> list = sessionsByUser.get(userId);
		return list == null ? List.of() : List.copyOf(list);
	}

	public Collection<WsSessionInfo> all() {
		return List.copyOf(sessionsById.values());
	}

	public List<Long> onlineUserIds() {
		return sessionsByUser.keySet().stream().sorted().collect(Collectors.toList());
	}

	public int connectionCount() {
		return sessionsById.size();
	}

}
