package com.cloudwaer.workflow.serve.engine.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 日志动作执行器：记录审计日志
 */
@Slf4j
@Component
public class LogActionExecutor implements ActionExecutor {

	@Override
	public String getActionType() {
		return "log";
	}

	@Override
	public void execute(ActionContext context, String config) {
		log.info("[AUDIT] instanceId={}, taskId={}, nodeId={}, event={}, config={}", context.getInstanceId(),
				context.getTaskId(), context.getNodeId(), context.getEventType(), config);
	}

}
