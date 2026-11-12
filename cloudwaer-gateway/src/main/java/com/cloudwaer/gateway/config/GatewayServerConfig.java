package com.cloudwaer.gateway.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关服务端工厂固定为 Reactor Netty。
 * <p>
 * common-core 会传递引入 tomcat-embed，而 Boot 3.2 自动配置里 EmbeddedTomcat 先于 EmbeddedNetty 参与
 * {@code @ConditionalOnMissingBean} 竞争，不显式声明时网关会跑在 Tomcat 上——SCG 的 WebSocket
 * 代理（ReactorNettyRequestUpgradeStrategy）拿不到 reactor HttpServerResponse 原生响应，/ws 握手全部 500。
 */
@Configuration(proxyBeanMethods = false)
public class GatewayServerConfig {

	@Bean
	public ReactiveWebServerFactory reactiveWebServerFactory() {
		return new NettyReactiveWebServerFactory();
	}

}
