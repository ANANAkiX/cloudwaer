package com.cloudwaer.workflow.serve.engine.node;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.workflow.serve.engine.*;
import com.cloudwaer.workflow.serve.engine.listener.EventPublisher;
import com.cloudwaer.workflow.serve.engine.listener.WorkflowEvent;
import com.cloudwaer.workflow.serve.entity.WfTask;
import com.cloudwaer.workflow.serve.mapper.WfTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 用户任务节点执行器：创建待办任务，等待人工处理
 */
@Slf4j
@Component
public class UserTaskNodeExecutor implements NodeExecutor {

	@Autowired
	private WfTaskMapper taskMapper;

	@Autowired(required = false)
	private EventPublisher eventPublisher;

	@Override
	public String getNodeType() {
		return "user_task";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		// 会签/或签 + 多个指定用户：每人一个任务，complete 时按 multiType 聚合裁决
		List<String> signUsers = resolveSignUsers(node);
		if (signUsers.size() > 1) {
			String lastTaskId = null;
			for (String user : signUsers) {
				lastTaskId = insertTask(node, context, user, null, null);
			}
			log.info("创建{}任务组: nodeId={}, users={}", "countersign".equals(node.getMultiType()) ? "会签" : "或签",
					node.getNodeId(), signUsers);
			return NodeResult.wait(lastTaskId);
		}

		// 单任务模式（含候选人认领）
		String assignee = resolveAssignee(node, context);
		String candidateUsers = null;
		String candidateGroups = null;
		if ("role".equals(node.getAssigneeType()) || "dept".equals(node.getAssigneeType())) {
			// 候选组模式，不指定具体人
			candidateGroups = node.getAssigneeVal();
		}
		else if ("user".equals(node.getAssigneeType()) && node.getAssigneeVal() != null
				&& node.getAssigneeVal().contains(",")) {
			candidateUsers = node.getAssigneeVal();
			assignee = null; // 多候选人，需要认领
		}
		String taskId = insertTask(node, context, assignee, candidateUsers, candidateGroups);
		log.info("创建用户任务: taskId={}, nodeId={}, assignee={}", taskId, node.getNodeId(), assignee);
		return NodeResult.wait(taskId);
	}

	/**
	 * 会签/或签按人拆任务：仅「指定用户」多人时拆分；角色/部门候选组无法确定成员，保持单任务。
	 */
	private List<String> resolveSignUsers(DslNode node) {
		String multiType = node.getMultiType();
		if (!"countersign".equals(multiType) && !"or_sign".equals(multiType)) {
			return List.of();
		}
		if (!"user".equals(node.getAssigneeType()) || node.getAssigneeVal() == null) {
			return List.of();
		}
		return Arrays.stream(node.getAssigneeVal().split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.distinct()
			.toList();
	}

	private String insertTask(DslNode node, ExecutionContext context, String assignee, String candidateUsers,
			String candidateGroups) {
		WfTask task = new WfTask();
		String taskId = UUID.randomUUID().toString().replace("-", "");
		task.setTaskId(taskId);
		task.setInstanceId(context.getInstanceId());
		task.setDefId(context.getDefId());
		task.setDefKey(context.getDefKey());
		task.setNodeId(node.getNodeId());
		task.setNodeName(node.getNodeName());
		task.setTaskName(node.getNodeName() != null ? node.getNodeName() : "审批任务");
		task.setTaskStatus(0); // pending
		task.setAssignee(assignee);
		task.setMultiType(node.getMultiType());
		task.setCandidateUsers(candidateUsers);
		task.setCandidateGroups(candidateGroups);

		// 存节点表单配置（含 formId）
		if (node.getConfigJson() != null && !node.getConfigJson().isBlank()) {
			task.setFormJson(node.getConfigJson());
		}

		// 超时时间
		if (node.getDueHours() != null && node.getDueHours() > 0) {
			task.setDueTime(LocalDateTime.now().plusHours(node.getDueHours()));
		}

		taskMapper.insert(task);
		if (eventPublisher != null) {
			eventPublisher.publish("task:create", context.getInstanceId(), taskId, node.getNodeId(), node.getNodeName(),
					context.getOperator(), null);
		}
		return taskId;
	}

	private String resolveAssignee(DslNode node, ExecutionContext context) {
		if (node.getAssigneeType() == null) {
			return null;
		}
		switch (node.getAssigneeType()) {
			case "user":
				// 取第一个用户
				String val = node.getAssigneeVal();
				return val != null ? val.split(",")[0].trim() : null;
			case "initiator":
				return (String) context.getVariable("startUser");
			case "variable":
				Object v = context.getVariable(node.getAssigneeVal());
				return v != null ? String.valueOf(v) : null;
			case "role":
			case "dept":
				// 候选组模式，不指定具体人
				return null;
			default:
				return null;
		}
	}

}
