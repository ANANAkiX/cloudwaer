package com.cloudwaer.flowable.serve.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 日志任务监听器（通用示例实现）。
 */
@Slf4j
@Component("logTaskListener")
public class LogTaskListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("[logTaskListener] event={}, taskId={}, name={}, assignee={}", delegateTask.getEventName(),
				delegateTask.getId(), delegateTask.getName(), delegateTask.getAssignee());
	}

}
