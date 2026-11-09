package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.admin.api.dto.SendMessageDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.workflow.serve.engine.action.NotifyActionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 站内信消费：通知动作 / 站内信监听器发布的 {@link NotifyActionExecutor.NotifyEvent} → 经 admin Feign
 * 落库到消息中心（sys_message）。
 * <p>
 * 失败只记日志，不影响流程主链路。邮件/短信类型为预留口，暂不投递。
 */
@Slf4j
@Component
public class NotifyEventConsumer {

	@Autowired
	private AdminFeignClient adminFeignClient;

	@EventListener
	public void onNotify(NotifyActionExecutor.NotifyEvent event) {
		try {
			if (!"inner".equals(event.getType())) {
				return;
			}
			List<Long> userIds = new ArrayList<>();
			for (String username : event.getTarget().split(",")) {
				String u = username.trim();
				if (u.isEmpty()) {
					continue;
				}
				UserDTO dto = adminFeignClient.getUserByUsername(u).getData();
				if (dto != null && dto.getId() != null) {
					userIds.add(dto.getId());
				}
				else {
					log.warn("[NotifyConsumer] 用户不存在: {}", u);
				}
			}
			if (userIds.isEmpty()) {
				log.warn("[NotifyConsumer] 无有效接收人，跳过: target={}", event.getTarget());
				return;
			}
			SendMessageDTO msg = new SendMessageDTO();
			msg.setUserIds(userIds);
			msg.setMsgType("SYSTEM");
			msg.setTitle(event.getTitle());
			msg.setContent(event.getContent());
			msg.setBizType("workflow_instance");
			msg.setBizId(event.getInstanceId());
			msg.setSenderName("系统");
			adminFeignClient.sendMessage(msg);
			log.info("[NotifyConsumer] 站内信已投递: users={}, title={}", userIds, event.getTitle());
		}
		catch (Exception e) {
			log.error("[NotifyConsumer] 站内信投递失败: title={}, err={}", event.getTitle(), e.getMessage());
		}
	}

}
