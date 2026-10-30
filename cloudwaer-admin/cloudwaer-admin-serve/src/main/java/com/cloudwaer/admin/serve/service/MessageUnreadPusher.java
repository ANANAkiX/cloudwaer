package com.cloudwaer.admin.serve.service;

import com.cloudwaer.common.ws.WsConnectionListener;
import org.springframework.stereotype.Component;

/**
 * WS 连接建立时推送初始未读数（断线重连后铃铛立即对账，不依赖轮询）
 *
 * @author cloudwaer
 */
@Component
public class MessageUnreadPusher implements WsConnectionListener {

	private final MessageService messageService;

	public MessageUnreadPusher(MessageService messageService) {
		this.messageService = messageService;
	}

	@Override
	public void onConnected(Long userId, String username) {
		messageService.pushUnread(userId);
	}

}
