package com.cloudwaer.flowable.serve.action.executor;

import com.cloudwaer.flowable.serve.action.ActionContext;
import com.cloudwaer.flowable.serve.action.ActionExecutor;
import com.cloudwaer.flowable.serve.constant.FlowableConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyActionExecutor implements ActionExecutor {

	@Override
	public String getType() {
		return FlowableConstants.ACTION_NOTIFY;
	}

	@Override
	public void execute(ActionContext context, String config) {
		log.info("Notify action placeholder: event={}, taskId={}, config={}", context.getEventType(),
				context.getTaskId(), config);
	}

}
