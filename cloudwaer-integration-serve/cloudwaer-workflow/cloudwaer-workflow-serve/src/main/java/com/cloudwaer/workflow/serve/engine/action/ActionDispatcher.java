package com.cloudwaer.workflow.serve.engine.action;

import com.cloudwaer.workflow.serve.entity.WfActionLog;
import com.cloudwaer.workflow.serve.mapper.WfActionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 动作分发器：根据 actionType 分发到对应执行器
 */
@Slf4j
@Component
public class ActionDispatcher {

	@Autowired(required = false)
	private List<ActionExecutor> executors;

	@Autowired
	private WfActionLogMapper actionLogMapper;

	private volatile Map<String, ActionExecutor> executorMap;

	private void ensureInit() {
		if (executorMap == null) {
			synchronized (this) {
				if (executorMap == null) {
					executorMap = executors != null
							? executors.stream()
								.collect(Collectors.toMap(ActionExecutor::getActionType, Function.identity()))
							: Map.of();
				}
			}
		}
	}

	/**
	 * 分发动作（带日志记录）
	 */
	public void dispatch(ActionContext context, String actionType, String config) {
		// 记录动作日志
		WfActionLog logEntry = new WfActionLog();
		logEntry.setInstanceId(context.getInstanceId());
		logEntry.setTaskId(context.getTaskId());
		logEntry.setNodeId(context.getNodeId());
		logEntry.setEventType(context.getEventType());
		logEntry.setActionType(actionType);
		logEntry.setActionConfig(config);
		logEntry.setExecStatus(0); // pending

		ensureInit();
		ActionExecutor executor = actionType != null ? executorMap.get(actionType) : null;
		if (executor == null) {
			log.warn("未找到动作执行器: actionType={}", actionType);
			logEntry.setExecStatus(2);
			logEntry.setExecResult("执行器缺失: " + actionType);
			actionLogMapper.insert(logEntry);
			return;
		}

		long start = System.currentTimeMillis();
		try {
			executor.execute(context, config);
			logEntry.setExecStatus(1);
			logEntry.setExecResult("成功");
		}
		catch (Exception e) {
			log.error("动作执行失败: actionType={}, err={}", actionType, e.getMessage(), e);
			logEntry.setExecStatus(2);
			logEntry.setExecResult(e.getMessage());
		}
		logEntry.setExecTime(LocalDateTime.now());
		logEntry.setDurationMs(System.currentTimeMillis() - start);
		actionLogMapper.insert(logEntry);
	}

	/**
	 * 批量分发
	 */
	public void dispatchAll(ActionContext context, List<String[]> actionTypeConfigs) {
		if (actionTypeConfigs == null) {
			return;
		}
		for (String[] pair : actionTypeConfigs) {
			if (pair.length >= 2) {
				dispatch(context, pair[0], pair[1]);
			}
		}
	}

}
