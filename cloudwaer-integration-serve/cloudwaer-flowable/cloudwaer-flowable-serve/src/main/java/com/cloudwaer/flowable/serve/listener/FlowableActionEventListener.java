package com.cloudwaer.flowable.serve.listener;

import com.cloudwaer.flowable.serve.action.ActionContext;
import com.cloudwaer.flowable.serve.action.ActionDispatcher;
import com.cloudwaer.flowable.serve.constant.FlowableConstants;
import com.cloudwaer.flowable.serve.entity.WfDeployment;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.mapper.WfDeploymentMapper;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEvent;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FlowableActionEventListener implements FlowableEventListener {

	@Autowired
	private WfNodeActionService nodeActionService;

	@Autowired
	private ActionDispatcher actionDispatcher;

	@Autowired
	private WfDeploymentMapper deploymentMapper;

	@Autowired
	private TaskService taskService;

	@Autowired(required = false)
	private ObjectProvider<CandidateExpandTaskListener> candidateExpandTaskListenerProvider;

	@Override
	public void onEvent(FlowableEvent event) {
		if (event.getType() == FlowableEngineEventType.TASK_CREATED) {
			expandCandidateGroupsSafely(event);
			handleTaskEvent(event, FlowableConstants.EVENT_TASK_CREATE);
		}
		else if (event.getType() == FlowableEngineEventType.TASK_COMPLETED) {
			handleTaskEvent(event, FlowableConstants.EVENT_TASK_COMPLETE);
		}
		else if (event.getType() == FlowableEngineEventType.PROCESS_STARTED) {
			handleProcessEvent(event, FlowableConstants.EVENT_PROCESS_START);
		}
		else if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED) {
			handleProcessEvent(event, FlowableConstants.EVENT_PROCESS_END);
		}
	}

	/**
	 * 任务创建时，将候选组（角色ID）展开为候选用户，保证待办可被 role 查到。
	 */
	private void expandCandidateGroupsSafely(FlowableEvent event) {
		CandidateExpandTaskListener candidateExpandTaskListener = candidateExpandTaskListenerProvider != null
				? candidateExpandTaskListenerProvider.getIfAvailable() : null;
		if (candidateExpandTaskListener == null || !(event instanceof FlowableEntityEvent)) {
			return;
		}
		Object entity = ((FlowableEntityEvent) event).getEntity();
		if (!(entity instanceof Task)) {
			return;
		}
		Task task = (Task) entity;
		try {
			java.util.List<org.flowable.identitylink.api.IdentityLink> links = taskService
				.getIdentityLinksForTask(task.getId());
			java.util.List<String> groups = new java.util.ArrayList<>();
			if (links != null) {
				for (org.flowable.identitylink.api.IdentityLink link : links) {
					if (org.flowable.identitylink.api.IdentityLinkType.CANDIDATE.equals(link.getType())
							&& link.getGroupId() != null && !link.getGroupId().isBlank()) {
						groups.add(link.getGroupId().trim());
					}
				}
			}
			if (groups.isEmpty()) {
				return;
			}
			List<String> usernames = candidateExpandTaskListener.expandRoleIdsToUsernames(groups);
			for (String username : usernames) {
				taskService.addCandidateUser(task.getId(), username);
			}
			if (!usernames.isEmpty()) {
				log.info("任务候选组已展开: taskId={}, groups={}, users={}", task.getId(), groups, usernames);
			}
		}
		catch (Exception e) {
			log.warn("任务候选组展开失败: taskId={}, err={}", task.getId(), e.getMessage());
		}
	}

	private void handleTaskEvent(FlowableEvent event, String eventType) {
		if (!(event instanceof FlowableEntityEvent)) {
			return;
		}
		Object entity = ((FlowableEntityEvent) event).getEntity();
		if (!(entity instanceof Task)) {
			return;
		}
		Task task = (Task) entity;
		String processDefinitionId = task.getProcessDefinitionId();
		String processDefinitionKey = resolveProcessDefinitionKey(processDefinitionId);
		Integer modelVersion = resolveModelVersion(processDefinitionId);
		List<WfNodeAction> actions = nodeActionService.listActions(processDefinitionKey, modelVersion,
				task.getTaskDefinitionKey(), eventType);
		ActionContext context = new ActionContext();
		context.setProcessDefinitionId(processDefinitionId);
		context.setProcessDefinitionKey(processDefinitionKey);
		context.setProcessDefinitionVersion(modelVersion);
		context.setProcessInstanceId(task.getProcessInstanceId());
		context.setTaskId(task.getId());
		context.setTaskDefinitionKey(task.getTaskDefinitionKey());
		context.setEventType(eventType);
		actionDispatcher.dispatch(context, actions);
	}

	private void handleProcessEvent(FlowableEvent event, String eventType) {
		if (!(event instanceof FlowableEngineEvent)) {
			return;
		}
		FlowableEngineEvent processEvent = (FlowableEngineEvent) event;
		String processDefinitionId = processEvent.getProcessDefinitionId();
		String processDefinitionKey = resolveProcessDefinitionKey(processDefinitionId);
		Integer modelVersion = resolveModelVersion(processDefinitionId);
		List<WfNodeAction> actions = nodeActionService.listActions(processDefinitionKey, modelVersion, null, eventType);
		ActionContext context = new ActionContext();
		context.setProcessDefinitionId(processDefinitionId);
		context.setProcessDefinitionKey(processDefinitionKey);
		context.setProcessDefinitionVersion(modelVersion);
		context.setProcessInstanceId(processEvent.getProcessInstanceId());
		context.setEventType(eventType);
		actionDispatcher.dispatch(context, actions);
	}

	private Integer resolveModelVersion(String processDefinitionId) {
		WfDeployment deployment = deploymentMapper
			.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WfDeployment>()
				.eq(WfDeployment::getProcessDefinitionId, processDefinitionId)
				.eq(WfDeployment::getStatus, 1));
		if (deployment != null) {
			return deployment.getModelVersion();
		}
		return parseDefinitionVersion(processDefinitionId);
	}

	private String resolveProcessDefinitionKey(String processDefinitionId) {
		WfDeployment deployment = deploymentMapper
			.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WfDeployment>()
				.eq(WfDeployment::getProcessDefinitionId, processDefinitionId)
				.eq(WfDeployment::getStatus, 1));
		if (deployment != null) {
			return deployment.getProcessDefinitionKey();
		}
		return parseDefinitionKey(processDefinitionId);
	}

	private String parseDefinitionKey(String processDefinitionId) {
		if (processDefinitionId == null) {
			return null;
		}
		String[] parts = processDefinitionId.split(":");
		return parts.length > 0 ? parts[0] : processDefinitionId;
	}

	private Integer parseDefinitionVersion(String processDefinitionId) {
		if (processDefinitionId == null) {
			return null;
		}
		String[] parts = processDefinitionId.split(":");
		if (parts.length >= 2) {
			try {
				return Integer.parseInt(parts[1]);
			}
			catch (NumberFormatException ignored) {
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public boolean isFireOnTransactionLifecycleEvent() {
		return false;
	}

	@Override
	public String getOnTransaction() {
		return null;
	}

}
