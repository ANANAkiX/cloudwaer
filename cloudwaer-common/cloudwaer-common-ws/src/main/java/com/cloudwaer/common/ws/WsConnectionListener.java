package com.cloudwaer.common.ws;

import com.cloudwaer.common.ws.model.WsEnvelope;

/**
 * WS 连接生命周期回调（可选扩展点）：连接建立时回调，典型用途如推送初始未读数。
 *
 * @author cloudwaer
 */
public interface WsConnectionListener {

	/**
	 * 连接建立并鉴权通过后回调（在 hello 帧之后）
	 */
	void onConnected(Long userId, String username);

}
