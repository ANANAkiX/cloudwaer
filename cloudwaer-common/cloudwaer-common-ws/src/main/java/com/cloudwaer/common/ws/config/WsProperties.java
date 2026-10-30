package com.cloudwaer.common.ws.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 平台 WebSocket 通道配置
 *
 * @author cloudwaer
 */
@Data
@ConfigurationProperties(prefix = "cloudwaer.ws")
public class WsProperties {

	/**
	 * 是否启用 WS 通道（注册 /ws 端点）
	 */
	private boolean enabled = true;

	/**
	 * 握手路径
	 */
	private String path = "/ws";

	/**
	 * 心跳间隔（秒）；超时阈值为 3 倍间隔
	 */
	private long heartbeatSeconds = 30;

}
