package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.model.WsEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * WS 跨实例中继：订阅 {@link WsDispatcher#DISPATCH_CHANNEL}，把其他实例发布的信封投给本实例的连接。
 *
 * @author cloudwaer
 */
@Slf4j
public class WsRedisRelay implements MessageListener, SmartLifecycle {

	private final WsDispatcher dispatcher;

	private final ObjectProvider<RedisConnectionFactory> connectionFactoryProvider;

	private final ObjectMapper objectMapper;

	private volatile RedisMessageListenerContainer container;

	private volatile boolean running = false;

	public WsRedisRelay(WsDispatcher dispatcher, ObjectProvider<RedisConnectionFactory> connectionFactoryProvider,
			ObjectMapper objectMapper) {
		this.dispatcher = dispatcher;
		this.connectionFactoryProvider = connectionFactoryProvider;
		this.objectMapper = objectMapper;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			WsEnvelope envelope = objectMapper.readValue(message.getBody(), WsEnvelope.class);
			// 本实例发出的帧已本地直投过，跳过回声
			if (dispatcher.isOwnOrigin(envelope)) {
				return;
			}
			dispatcher.deliverLocal(envelope);
		}
		catch (Exception e) {
			log.warn("WS 中继消息解析失败: {}", e.getMessage());
		}
	}

	@Override
	public void start() {
		RedisConnectionFactory factory = connectionFactoryProvider.getIfAvailable();
		if (factory == null) {
			log.info("WS 跨实例中继未启动：无 RedisConnectionFactory（单实例模式可用）");
			return;
		}
		try {
			RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
			listenerContainer.setConnectionFactory(factory);
			listenerContainer.addMessageListener(this, new ChannelTopic(WsDispatcher.DISPATCH_CHANNEL));
			listenerContainer.afterPropertiesSet();
			listenerContainer.start();
			this.container = listenerContainer;
			this.running = true;
			log.info("WS 跨实例中继已启动: channel={}", WsDispatcher.DISPATCH_CHANNEL);
		}
		catch (Exception e) {
			log.warn("WS 跨实例中继启动失败（不影响本实例直投）: {}", e.getMessage());
		}
	}

	@Override
	public void stop() {
		this.running = false;
		RedisMessageListenerContainer listenerContainer = this.container;
		if (listenerContainer != null) {
			try {
				listenerContainer.stop();
			}
			catch (Exception e) {
				log.debug("WS 中继停止异常", e);
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
