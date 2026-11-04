package com.cloudwaer.workflow.serve.engine.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 审计日志监听器：记录流程节点进出、任务创建/完成等事件
 */
@Slf4j
@Component
public class AuditLogListener implements WorkflowEventListener {

	@Override
	public String getEventType() {
		return "*"; // 监听所有事件
	}

	@Override
	public void onEvent(WorkflowEvent event) {
		log.info("[AUDIT] {} | instance={} | task={} | node={}/{} | operator={}", event.getEventType(),
				event.getInstanceId(), event.getTaskId(), event.getNodeId(), event.getNodeName(), event.getOperator());
	}

}
