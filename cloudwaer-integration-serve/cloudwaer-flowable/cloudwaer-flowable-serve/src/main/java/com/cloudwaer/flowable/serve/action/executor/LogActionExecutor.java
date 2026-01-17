package com.cloudwaer.flowable.serve.action.executor;

import com.cloudwaer.flowable.serve.action.ActionContext;
import com.cloudwaer.flowable.serve.action.ActionExecutor;
import com.cloudwaer.flowable.serve.constant.FlowableConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogActionExecutor implements ActionExecutor {

    @Override
    public String getType() {
        return FlowableConstants.ACTION_LOG;
    }

    @Override
    public void execute(ActionContext context, String config) {
        log.info("Flowable action log: event={}, taskId={}, config={}",
                context.getEventType(), context.getTaskId(), config);
    }
}
