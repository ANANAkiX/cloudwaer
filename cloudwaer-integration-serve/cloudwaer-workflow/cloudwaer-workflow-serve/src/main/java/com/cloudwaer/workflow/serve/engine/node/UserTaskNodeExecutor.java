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
		// 解析处理人
		String assignee = resolveAssignee(node, context);

		// 创建任务
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

		// 处理候选人/候选组
		if ("role".equals(node.getAssigneeType())) {
			task.setCandidateGroups(node.getAssigneeVal());
		}
		else if ("user".equals(node.getAssigneeType()) && node.getAssigneeVal() != null
				&& node.getAssigneeVal().contains(",")) {
			task.setCandidateUsers(node.getAssigneeVal());
			task.setAssignee(null); // 多候选人，需要认领
		}

		// 存节点表单配置（含 formId）
		if (node.getConfigJson() != null && !node.getConfigJson().isBlank()) {
			task.setFormJson(node.getConfigJson());
		}

		// 超时时间
		if (node.getDueHours() != null && node.getDueHours() > 0) {
			task.setDueTime(LocalDateTime.now().plusHours(node.getDueHours()));
		}

		taskMapper.insert(task);
		log.info("创建用户任务: taskId={}, nodeId={}, assignee={}", taskId, node.getNodeId(), assignee);
		if (eventPublisher != null) {
			eventPublisher.publish("task:create", context.getInstanceId(), taskId, node.getNodeId(), node.getNodeName(),
					context.getOperator(), null);
		}
		return NodeResult.wait(taskId);
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
