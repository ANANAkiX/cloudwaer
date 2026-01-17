package com.cloudwaer.flowable.serve.action;

import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LocalActionDispatcher implements ActionDispatcher {

    private final Map<String, ActionExecutor> executorMap;

    @Autowired
    public LocalActionDispatcher(List<ActionExecutor> executors) {
        this.executorMap = executors.stream()
                .collect(Collectors.toMap(ActionExecutor::getType, Function.identity()));
    }

    @Override
    public void dispatch(ActionContext context, List<WfNodeAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        for (WfNodeAction action : actions) {
            ActionExecutor executor = executorMap.get(action.getActionType());
            if (executor == null) {
                log.warn("No executor for actionType: {}", action.getActionType());
                continue;
            }
            executor.execute(context, action.getActionConfig());
        }
    }
}
