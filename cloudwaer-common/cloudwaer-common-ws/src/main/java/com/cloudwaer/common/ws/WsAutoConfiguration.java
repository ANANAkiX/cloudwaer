package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.config.WsProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 平台 WebSocket 通道自动配置：注册 /ws 端点（鉴权握手）+ 跨实例中继 + 心跳。
 *
 * <p>
 * 仅 Servlet 应用启用（网关 WebFlux 通过 ws 路由转发到业务服务，不在此注册）。业务模块引用 cloudwaer-common-ws
 * 即得：连接鉴权、用户通道推送（{@link WsDispatcher}）、跨实例分发。
 * </p>
 *
 * @author cloudwaer
 */
@AutoConfiguration
@ConditionalOnClass(org.springframework.web.socket.WebSocketSession.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "cloudwaer.ws", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WsProperties.class)
@EnableWebSocket
public class WsAutoConfiguration implements WebSocketConfigurer {

	private final WsServerHandler wsServerHandler;

	private final WsHandshakeInterceptor wsHandshakeInterceptor;

	private final WsProperties wsProperties;

	public WsAutoConfiguration(WsServerHandler wsServerHandler, WsHandshakeInterceptor wsHandshakeInterceptor,
			WsProperties wsProperties) {
		this.wsServerHandler = wsServerHandler;
		this.wsHandshakeInterceptor = wsHandshakeInterceptor;
		this.wsProperties = wsProperties;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(wsServerHandler, wsProperties.getPath())
			.addInterceptors(wsHandshakeInterceptor)
			.setAllowedOrigins("*");
	}

	@Bean
	public WsRedisRelay wsRedisRelay(WsDispatcher dispatcher,
			ObjectProvider<RedisConnectionFactory> connectionFactoryProvider, ObjectMapper objectMapper) {
		return new WsRedisRelay(dispatcher, connectionFactoryProvider, objectMapper);
	}

	@Bean
	public WsHeartbeat wsHeartbeat(WsSessionManager sessionManager, ObjectMapper objectMapper) {
		return new WsHeartbeat(sessionManager, objectMapper, wsProperties.getHeartbeatSeconds());
	}

	/**
	 * 兼容容器级 WS 端点（预留；当前使用 Spring WebSocketHandler 注册）
	 */
	@Bean
	@ConditionalOnProperty(prefix = "cloudwaer.ws", name = "server-endpoint-exporter", havingValue = "true",
			matchIfMissing = false)
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
