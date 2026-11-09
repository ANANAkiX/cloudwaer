package com.cloudwaer.workflow.serve.engine.listener;

import com.cloudwaer.workflow.serve.engine.action.NotifyActionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 站内信通知监听器：任务创建时给候选人发通知
 */
@Slf4j
@Component
public class NotifyTaskListener implements NodeScopedListener {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public String getEventType() {
		return "task:create";
	}

	@Override
	public void onEvent(WorkflowEvent event) {
		log.info("[NOTIFY] 任务创建通知: instance={}, task={}, node={}", event.getInstanceId(), event.getTaskId(),
				event.getNodeName());
		// 发布站内信事件，由消息模块消费
		eventPublisher.publishEvent(new NotifyActionExecutor.NotifyEvent(this, "inner", event.getOperator(), "新任务提醒",
				"您有新的待办任务: " + event.getNodeName(), event.getInstanceId()));
	}

}
