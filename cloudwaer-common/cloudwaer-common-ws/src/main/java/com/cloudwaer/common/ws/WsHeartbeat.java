package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.model.WsEnvelope;
import com.cloudwaer.common.ws.model.WsSessionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WS 心跳：周期向所有连接发 ping 帧，超时未活跃的连接直接关闭（防死连接占位）。
 *
 * @author cloudwaer
 */
@Slf4j
public class WsHeartbeat implements SmartLifecycle {

	private final WsSessionManager sessionManager;

	private final ObjectMapper objectMapper;

	private final long heartbeatSeconds;

	private final long timeoutMillis;

	private ScheduledExecutorService scheduler;

	private volatile boolean running = false;

	public WsHeartbeat(WsSessionManager sessionManager, ObjectMapper objectMapper, long heartbeatSeconds) {
		this.sessionManager = sessionManager;
		this.objectMapper = objectMapper;
		this.heartbeatSeconds = heartbeatSeconds;
		this.timeoutMillis = heartbeatSeconds * 3 * 1000;
	}

	@Override
	public void start() {
		scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "ws-heartbeat");
			t.setDaemon(true);
			return t;
		});
		scheduler.scheduleAtFixedRate(this::sweep, heartbeatSeconds, heartbeatSeconds, TimeUnit.SECONDS);
		running = true;
		log.info("WS 心跳已启动: interval={}s, timeout={}ms", heartbeatSeconds, timeoutMillis);
	}

	private void sweep() {
		long now = System.currentTimeMillis();
		for (WsSessionInfo info : sessionManager.all()) {
			try {
				if (now - info.getLastActiveAt() > timeoutMillis) {
					log.info("WS 心跳超时关闭: userId={}, sessionId={}", info.getUserId(), info.getSession().getId());
					info.getSession().close();
					continue;
				}
				if (info.getSession().isOpen()) {
					String json = objectMapper
						.writeValueAsString(WsEnvelope.of("ping", info.getUserId(), null, Map.of("ts", now)));
					synchronized (info.getSession()) {
						info.getSession().sendMessage(new org.springframework.web.socket.TextMessage(json));
					}
				}
			}
			catch (Exception e) {
				log.debug("WS 心跳帧失败 sessionId={}: {}", info.getSession().getId(), e.getMessage());
			}
		}
	}

	@Override
	public void stop() {
		running = false;
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
