package com.cloudwaer.common.core.config;

import com.cloudwaer.common.core.service.GlobalSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * 设置发布广播订阅：收到 {@link GlobalSettingService#NOTIFY_CHANNEL} 消息后立即刷新本地设置快照。
 *
 * <p>
 * 与「发布设置」配套：无 TTL 轮询，仅在启动加载与收到广播时读 Redis，减少 IO。Redis 不可用时静默降级 （各实例继续使用启动快照 + yml 兜底）。以
 * SmartLifecycle 编程式启动监听容器，避免 @ConditionalOnBean 对自动装配顺序的依赖。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class GlobalSettingRefreshListener implements MessageListener, SmartLifecycle {

	private final GlobalSettingService globalSettingService;

	private final ObjectProvider<RedisConnectionFactory> connectionFactoryProvider;

	private volatile RedisMessageListenerContainer container;

	private volatile boolean running = false;

	public GlobalSettingRefreshListener(GlobalSettingService globalSettingService,
			ObjectProvider<RedisConnectionFactory> connectionFactoryProvider) {
		this.globalSettingService = globalSettingService;
		this.connectionFactoryProvider = connectionFactoryProvider;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		log.debug("收到设置发布广播，刷新本地快照");
		globalSettingService.refresh();
	}

	@Override
	public void start() {
		RedisConnectionFactory factory = connectionFactoryProvider.getIfAvailable();
		if (factory == null) {
			log.info("GlobalSetting 广播订阅未启动：无 RedisConnectionFactory");
			return;
		}
		try {
			RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
			listenerContainer.setConnectionFactory(factory);
			listenerContainer.addMessageListener(this, new ChannelTopic(GlobalSettingService.NOTIFY_CHANNEL));
			listenerContainer.afterPropertiesSet();
			listenerContainer.start();
			this.container = listenerContainer;
			this.running = true;
			log.info("GlobalSetting 广播订阅已启动: channel={}", GlobalSettingService.NOTIFY_CHANNEL);
		}
		catch (Exception e) {
			log.warn("GlobalSetting 广播订阅启动失败（不影响 yml 兜底）: {}", e.getMessage());
		}
	}

	@Override
	public void stop() {
		RedisMessageListenerContainer listenerContainer = this.container;
		this.running = false;
		if (listenerContainer != null) {
			try {
				listenerContainer.stop();
			}
			catch (Exception e) {
				log.debug("GlobalSetting 广播订阅停止异常", e);
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
