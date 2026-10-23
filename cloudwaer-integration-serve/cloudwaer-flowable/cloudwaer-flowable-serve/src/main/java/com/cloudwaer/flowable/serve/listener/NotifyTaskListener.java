package com.cloudwaer.flowable.serve.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 通知任务监听器占位：可在此对接站内信/邮件/IM。
 */
@Slf4j
@Component("notifyTaskListener")
public class NotifyTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String event = delegateTask.getEventName();
		if ("create".equals(event)) {
			log.info("[notifyTaskListener] 任务创建通知占位: taskId={}, name={}, assignee={}", delegateTask.getId(),
					delegateTask.getName(), delegateTask.getAssignee());
		}
		else if ("complete".equals(event)) {
			log.info("[notifyTaskListener] 任务完成通知占位: taskId={}, assignee={}", delegateTask.getId(),
					delegateTask.getAssignee());
		}
	}

}
