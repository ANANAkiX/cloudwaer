package com.cloudwaer.common.ws.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * WS 统一信封：所有实时消息共用的帧结构。
 *
 * <p>
 * type 取值：message（业务消息）/ unread（未读数）/ ping / pong / hello（连接成功）；后续业务扩展新 type 即可，通道本身不关心
 * payload 内容。channel 为逻辑通道（如 user:123 / process:456），targetUserId 为空表示广播。
 * </p>
 *
 * @author cloudwaer
 */
@Data
public class WsEnvelope implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 帧类型：message / unread / ping / pong / hello
	 */
	private String type;

	/**
	 * 逻辑通道（user:{id} / biz 频道等）
	 */
	private String channel;

	/**
	 * 目标用户；null = 广播
	 */
	private Long targetUserId;

	/**
	 * 生产该帧的实例标识（跨实例中继回声去重用）
	 */
	private String origin;

	/**
	 * 业务负载（通道本身不解析）
	 */
	private Map<String, Object> payload;

	private long ts;

	public static WsEnvelope of(String type, Long targetUserId, String channel, Map<String, Object> payload) {
		WsEnvelope envelope = new WsEnvelope();
		envelope.setId(UUID.randomUUID().toString().replace("-", ""));
		envelope.setType(type);
		envelope.setTargetUserId(targetUserId);
		envelope.setChannel(channel);
		envelope.setPayload(payload);
		envelope.setTs(System.currentTimeMillis());
		return envelope;
	}

}
