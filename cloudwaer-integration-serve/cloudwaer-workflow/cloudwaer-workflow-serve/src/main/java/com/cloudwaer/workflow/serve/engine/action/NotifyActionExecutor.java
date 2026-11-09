package com.cloudwaer.workflow.serve.engine.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 通知动作执行器：发送站内信/邮件/短信
 * <p>
 * config:
 * {"notifyType":"inner|email|sms","target":"user1,user2","title":"xxx","content":"xxx"}
 * 站内信通过 Spring 事件发布，由消息模块消费落库
 */
@Slf4j
@Component
public class NotifyActionExecutor implements ActionExecutor {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public String getActionType() {
		return "notify";
	}

	@Override
	public void execute(ActionContext context, String config) {
		try {
			JsonNode root = objectMapper.readTree(config);
			String notifyType = root.has("notifyType") ? root.get("notifyType").asText("inner") : "inner";
			String target = root.has("target") ? root.get("target").asText() : null;
			String title = root.has("title") ? root.get("title").asText() : "流程通知";
			String content = root.has("content") ? root.get("content").asText() : "";

			if (target == null || target.isBlank()) {
				log.warn("[NOTIFY] 目标为空，跳过: config={}", config);
				return;
			}

			switch (notifyType) {
				case "inner":
					sendInnerMessage(target, title, content, context);
					break;
				case "email":
					sendEmail(target, title, content);
					break;
				case "sms":
					sendSms(target, content);
					break;
				default:
					log.warn("[NOTIFY] 未知通知类型: {}", notifyType);
			}
			log.info("[NOTIFY] 发送完成: type={}, target={}, title={}", notifyType, target, title);
		}
		catch (Exception e) {
			log.error("[NOTIFY] 发送失败: config={}, err={}", config, e.getMessage(), e);
		}
	}

	/**
	 * 站内信：发布 Spring 事件，由消息模块监听落库
	 */
	private void sendInnerMessage(String target, String title, String content, ActionContext context) {
		NotifyEvent event = new NotifyEvent(this, "inner", target, title, content, context.getInstanceId());
		eventPublisher.publishEvent(event);
		log.info("[NOTIFY:inner] 事件已发布: target={}, title={}", target, title);
	}

	/**
	 * 邮件通知（预留扩展口）
	 */
	private void sendEmail(String to, String subject, String body) {
		// 预留：对接 JavaMailSender / 邮件服务
		log.info("[NOTIFY:email] to={}, subject={}", to, subject);
		eventPublisher.publishEvent(new NotifyEvent(this, "email", to, subject, body, null));
	}

	/**
	 * 短信通知（预留扩展口）
	 */
	private void sendSms(String phone, String content) {
		// 预留：对接阿里云/腾讯云 SMS
		log.info("[NOTIFY:sms] phone={}", phone);
		eventPublisher.publishEvent(new NotifyEvent(this, "sms", phone, null, content, null));
	}

	/**
	 * 通知事件（供消息模块监听消费）
	 */
	public static class NotifyEvent extends org.springframework.context.ApplicationEvent {

		private final String type;

		private final String target;

		private final String title;

		private final String content;

		private final String instanceId;

		public NotifyEvent(Object source, String type, String target, String title, String content, String instanceId) {
			super(source);
			this.type = type;
			this.target = target;
			this.title = title;
			this.content = content;
			this.instanceId = instanceId;
		}

		public String getType() {
			return type;
		}

		public String getTarget() {
			return target;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}

		public String getInstanceId() {
			return instanceId;
		}

	}

}
