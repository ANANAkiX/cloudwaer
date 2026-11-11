package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.workflow.api.dto.TaskCompleteDTO;
import com.cloudwaer.workflow.api.dto.WorkflowTaskDTO;
import com.cloudwaer.workflow.api.enums.InstanceStatus;
import com.cloudwaer.workflow.api.enums.TaskAction;
import com.cloudwaer.workflow.api.enums.TaskStatus;
import com.cloudwaer.workflow.serve.engine.ProcessEngine;
import com.cloudwaer.workflow.serve.service.InstanceNodeFormService;
import com.cloudwaer.workflow.serve.engine.listener.EventPublisher;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.entity.WfTask;
import com.cloudwaer.workflow.serve.mapper.WfInstanceMapper;
import com.cloudwaer.workflow.serve.mapper.WfTaskMapper;
import com.cloudwaer.workflow.serve.service.WorkflowTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import com.cloudwaer.workflow.serve.engine.DslDefinition;
import com.cloudwaer.workflow.serve.engine.DslEdge;
import com.cloudwaer.workflow.serve.engine.DslNode;
import com.cloudwaer.workflow.serve.engine.ExecutionContext;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.entity.WfNode;
import com.cloudwaer.workflow.serve.mapper.WfDefinitionMapper;
import com.cloudwaer.workflow.serve.mapper.WfNodeMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

@Slf4j
@Service
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

	@Autowired
	private WfTaskMapper taskMapper;

	@Autowired
	private WfInstanceMapper instanceMapper;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private InstanceNodeFormService instanceNodeFormService;

	@Autowired(required = false)
	private EventPublisher eventPublisher;

	@Autowired
	private WfDefinitionMapper defMapper;

	@Autowired
	private WfNodeMapper nodeMapper;

	@Autowired
	private com.cloudwaer.workflow.serve.engine.DslParser dslParser;

	@Override
	public PageResult<WorkflowTaskDTO> listTodo(PageDTO pageDTO) {
		String username = SecurityContextUtil.getCurrentUsername();
		List<Long> roleIds = SecurityContextUtil.getCurrentRoleIds();
		Page<WfTask> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfTask> wrapper = new LambdaQueryWrapper<>();
		wrapper.in(WfTask::getTaskStatus, TaskStatus.PENDING.getCode(), TaskStatus.CLAIMED.getCode());
		wrapper.and(w -> {
			w.eq(WfTask::getAssignee, username).or().like(WfTask::getCandidateUsers, username);
			// 角色候选组任务：candidateGroups 逗号分隔角色ID
			if (roleIds != null) {
				for (Long roleId : roleIds) {
					w.or().apply("FIND_IN_SET({0}, candidate_groups)", roleId);
				}
			}
		});
		wrapper.orderByDesc(WfTask::getCreateTime);
		IPage<WfTask> result = taskMapper.selectPage(page, wrapper);
		return toPageResult(result);
	}

	@Override
	public PageResult<WorkflowTaskDTO> listDone(PageDTO pageDTO) {
		String username = SecurityContextUtil.getCurrentUsername();
		Page<WfTask> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfTask> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(WfTask::getAssignee, username);
		// 已办 = 我办理过的动作终态：同意/驳回/转办/退回（转办退回不再从我的视图消失）
		wrapper.in(WfTask::getTaskStatus, TaskStatus.COMPLETED.getCode(), TaskStatus.REJECTED.getCode(),
				TaskStatus.TRANSFERRED.getCode(), TaskStatus.RETURNED.getCode());
		wrapper.orderByDesc(WfTask::getCompleteTime);
		IPage<WfTask> result = taskMapper.selectPage(page, wrapper);
		return toPageResult(result);
	}

	@Override
	public PageResult<WorkflowTaskDTO> listByInstance(String instanceId) {
		List<WfTask> tasks = taskMapper
			.selectList(new LambdaQueryWrapper<WfTask>().eq(WfTask::getInstanceId, instanceId)
				.orderByAsc(WfTask::getCreateTime));
		List<WorkflowTaskDTO> records = tasks.stream().map(t -> {
			WorkflowTaskDTO dto = new WorkflowTaskDTO();
			BeanUtils.copyProperties(t, dto);
			TaskStatus s = TaskStatus.getByCode(t.getTaskStatus());
			dto.setStatusDesc(s != null ? s.getDesc() : "未知");
			return dto;
		}).toList();
		return new PageResult<>(records, (long) records.size(), 1L, (long) Math.max(records.size(), 1));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean claim(String taskId) {
		String username = SecurityContextUtil.getCurrentUsername();
		WfTask task = getByTaskId(taskId);
		if (task.getTaskStatus() != null && task.getTaskStatus() != TaskStatus.PENDING.getCode()) {
			throw new BusinessException("任务不在待处理状态");
		}
		task.setAssignee(username);
		task.setTaskStatus(TaskStatus.CLAIMED.getCode());
		task.setClaimTime(LocalDateTime.now());
		taskMapper.updateById(task);
		processEngine.recordHistory(task.getInstanceId(), taskId, task.getNodeId(), task.getNodeName(), "user_task",
				"claim", username, "认领任务", null);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean complete(TaskCompleteDTO dto) {
		String username = SecurityContextUtil.getCurrentUsername();
		WfTask task = getByTaskId(dto.getTaskId());

		// 判断操作类型
		String action = dto.getAction() != null ? dto.getAction() : TaskAction.APPROVE.getCode();

		switch (action) {
			case "approve":
				return handleApprove(task, dto, username);
			case "reject":
				return handleReject(task, dto, username);
			case "transfer":
				return handleTransfer(task, dto, username);
			case "return":
				return handleReturn(task, dto, username);
			default:
				return handleApprove(task, dto, username);
		}
	}

	private boolean handleApprove(WfTask task, TaskCompleteDTO dto, String username) {
		task.setTaskStatus(TaskStatus.COMPLETED.getCode());
		task.setComment(dto.getComment());
		task.setResult("approve");
		task.setCompleteTime(LocalDateTime.now());
		// 保存节点表单数据（合并到 formJson，保留原节点配置的 formId）
		if (dto.getFormJson() != null && !dto.getFormJson().isBlank()) {
			try {
				com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
				com.fasterxml.jackson.databind.node.ObjectNode merged = om.createObjectNode();
				// 保留原配置的 formId 等字段
				if (task.getFormJson() != null && !task.getFormJson().isBlank()) {
					com.fasterxml.jackson.databind.JsonNode old = om.readTree(task.getFormJson());
					if (old.isObject()) {
						old.fields().forEachRemaining(e -> {
							if (!e.getKey().equals("formData")) {
								merged.set(e.getKey(), e.getValue());
							}
						});
					}
				}
				// 加入表单数据
				com.fasterxml.jackson.databind.JsonNode formData = om.readTree(dto.getFormJson());
				merged.set("formData", formData);
				task.setFormJson(om.writeValueAsString(merged));
			}
			catch (Exception e) {
				task.setFormJson(dto.getFormJson());
			}
		}
		if (task.getCreateTime() != null) {
			task.setDurationMs(Duration.between(task.getCreateTime(), LocalDateTime.now()).toMillis());
		}
		taskMapper.updateById(task);

		// 显式更新 formJson（确保不被 updateById 忽略）
		if (dto.getFormJson() != null && !dto.getFormJson().isBlank()) {
			try {
				com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
				com.fasterxml.jackson.databind.node.ObjectNode merged = om.createObjectNode();
				if (task.getFormJson() != null && !task.getFormJson().isBlank()) {
					com.fasterxml.jackson.databind.JsonNode old = om.readTree(task.getFormJson());
					if (old.isObject()) {
						old.fields().forEachRemaining(e -> {
							if (!e.getKey().equals("formData")) {
								merged.set(e.getKey(), e.getValue());
							}
						});
					}
				}
				com.fasterxml.jackson.databind.JsonNode formData = om.readTree(dto.getFormJson());
				merged.set("formData", formData);
				String mergedJson = om.writeValueAsString(merged);
				taskMapper.update(null,
						new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfTask>()
							.set(WfTask::getFormJson, mergedJson)
							.eq(WfTask::getTaskId, task.getTaskId()));
			}
			catch (Exception e) {
				taskMapper.update(null,
						new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfTask>()
							.set(WfTask::getFormJson, dto.getFormJson())
							.eq(WfTask::getTaskId, task.getTaskId()));
			}
			// 同步更新 wf_instance_node_form 表
			instanceNodeFormService.updateFormData(task.getInstanceId(), task.getNodeId(), dto.getFormJson());
		}
		if (eventPublisher != null) {
			eventPublisher.publish("task:complete", task.getInstanceId(), task.getTaskId(), task.getNodeId(),
					task.getNodeName(), username, "approve");
		}
		processEngine.recordHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
				"user_task", "approve", username, dto.getComment(), "approve");

		// 会签/或签聚合裁决：未达推进条件则等待其余审批
		if (!multiGateAllowsAdvance(task, username)) {
			return true;
		}

		// 推进流程到下一个节点
		advanceProcess(task, username);
		return true;
	}

	/**
	 * 会签/或签聚合裁决：或签任一通过即推进（取消其余）；会签达到通过人数才推进。
	 */
	private boolean multiGateAllowsAdvance(WfTask task, String username) {
		String multiType = task.getMultiType();
		if (!"countersign".equals(multiType) && !"or_sign".equals(multiType)) {
			return true;
		}
		List<WfTask> siblings = taskMapper
			.selectList(new LambdaQueryWrapper<WfTask>().eq(WfTask::getInstanceId, task.getInstanceId())
				.eq(WfTask::getNodeId, task.getNodeId()));
		if ("or_sign".equals(multiType)) {
			// 或签：任一通过即推进，取消其余未完成任务
			cancelPendingSiblings(task, siblings);
			return true;
		}
		long approved = siblings.stream()
			.filter(t -> TaskStatus.COMPLETED.getCode().equals(t.getTaskStatus()) && "approve".equals(t.getResult()))
			.count();
		int required = resolveRequiredSignCount(task, siblings.size());
		if (approved >= required) {
			// 会签到齐：取消剩余未完成任务
			cancelPendingSiblings(task, siblings);
			return true;
		}
		processEngine.recordHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
				"user_task", "wait", username, "会签进度 " + approved + "/" + required + "，等待其他人审批", null);
		return false;
	}

	private void cancelPendingSiblings(WfTask current, List<WfTask> siblings) {
		List<String> ids = siblings.stream()
			.filter(t -> !t.getTaskId().equals(current.getTaskId()))
			.filter(t -> TaskStatus.PENDING.getCode().equals(t.getTaskStatus())
					|| TaskStatus.CLAIMED.getCode().equals(t.getTaskStatus()))
			.map(WfTask::getTaskId)
			.toList();
		if (!ids.isEmpty()) {
			taskMapper.update(null,
					new LambdaUpdateWrapper<WfTask>().set(WfTask::getTaskStatus, TaskStatus.CANCELED.getCode())
						.in(WfTask::getTaskId, ids));
		}
	}

	/**
	 * 会签需求数：优先节点配置的通过人数（multiCount），缺省=全员到齐。
	 */
	private int resolveRequiredSignCount(WfTask task, int total) {
		try {
			WfInstance instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, task.getInstanceId()));
			if (instance != null) {
				WfNode node = nodeMapper
					.selectOne(new LambdaQueryWrapper<WfNode>().eq(WfNode::getDefId, instance.getDefId())
						.eq(WfNode::getNodeId, task.getNodeId()));
				if (node != null && node.getMultiCount() != null && node.getMultiCount() > 0) {
					return Math.min(node.getMultiCount(), total);
				}
			}
		}
		catch (Exception e) {
			log.warn("解析会签通过人数失败: {}", e.getMessage());
		}
		return total;
	}

	private boolean handleReject(WfTask task, TaskCompleteDTO dto, String username) {
		task.setTaskStatus(TaskStatus.REJECTED.getCode());
		task.setComment(dto.getComment());
		task.setResult("reject");
		task.setCompleteTime(LocalDateTime.now());
		taskMapper.updateById(task);
		processEngine.recordHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
				"user_task", "reject", username, dto.getComment(), "reject");

		// 清空节点表单数据
		instanceNodeFormService.clearFormData(task.getInstanceId(), task.getNodeId());

		// 取消同实例的其他待处理任务
		taskMapper.update(null,
				new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfTask>()
					.set(WfTask::getTaskStatus, TaskStatus.CANCELED.getCode())
					.eq(WfTask::getInstanceId, task.getInstanceId())
					.in(WfTask::getTaskStatus, TaskStatus.PENDING.getCode(), TaskStatus.CLAIMED.getCode())
					.ne(WfTask::getTaskId, task.getTaskId()));

		// 标记实例被驳回
		instanceMapper.update(null,
				new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfInstance>()
					.set(WfInstance::getInstStatus, InstanceStatus.REJECTED.getCode())
					.eq(WfInstance::getInstanceId, task.getInstanceId()));
		return true;
	}

	private boolean handleTransfer(WfTask task, TaskCompleteDTO dto, String username) {
		if (dto.getTargetUser() == null || dto.getTargetUser().isBlank()) {
			throw new BusinessException("转办目标人不能为空");
		}
		task.setTaskStatus(TaskStatus.TRANSFERRED.getCode());
		task.setComment(dto.getComment());
		task.setCompleteTime(LocalDateTime.now());
		taskMapper.updateById(task);
		processEngine.recordHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
				"user_task", "transfer", username, "转办给 " + dto.getTargetUser(), null);

		// 创建新任务给目标人
		WfTask newTask = new WfTask();
		BeanUtils.copyProperties(task, newTask);
		newTask.setId(null);
		newTask.setTaskId(java.util.UUID.randomUUID().toString().replace("-", ""));
		newTask.setAssignee(dto.getTargetUser());
		newTask.setTaskStatus(TaskStatus.PENDING.getCode());
		newTask.setComment(null);
		newTask.setResult(null);
		newTask.setCompleteTime(null);
		newTask.setClaimTime(null);
		taskMapper.insert(newTask);
		return true;
	}

	private boolean handleReturn(WfTask task, TaskCompleteDTO dto, String username) {
		task.setTaskStatus(TaskStatus.RETURNED.getCode());
		task.setComment(dto.getComment());
		task.setCompleteTime(LocalDateTime.now());
		taskMapper.updateById(task);
		processEngine.recordHistory(task.getInstanceId(), task.getTaskId(), task.getNodeId(), task.getNodeName(),
				"user_task", "return", username, "退回", null);

		// 找到上一个节点，创建新任务
		try {
			var instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, task.getInstanceId()));
			if (instance != null && instance.getDefId() != null) {
				var def = defMapper.selectById(instance.getDefId());
				if (def != null && def.getDslJson() != null) {
					var dsl = dslParser.parse(def.getDslJson());
					// 找到入线的源节点
					var incomings = dsl.incomingEdges(task.getNodeId());
					if (!incomings.isEmpty()) {
						String prevNodeId = incomings.get(0).getSourceId();
						var prevNode = dsl.findNode(prevNodeId);
						if (prevNode != null && "user_task".equals(prevNode.getNodeType())) {
							// 创建退回任务
							WfTask returnTask = new WfTask();
							returnTask.setTaskId(java.util.UUID.randomUUID().toString().replace("-", ""));
							returnTask.setInstanceId(task.getInstanceId());
							returnTask.setDefId(task.getDefId());
							returnTask.setDefKey(task.getDefKey());
							returnTask.setNodeId(prevNodeId);
							returnTask.setNodeName(prevNode.getNodeName());
							returnTask.setTaskName(prevNode.getNodeName() + "（退回重审）");
							returnTask.setTaskStatus(TaskStatus.PENDING.getCode());
							returnTask.setAssignee(prevNode.getAssigneeVal());
							returnTask.setCandidateUsers(prevNode.getAssigneeVal());
							taskMapper.insert(returnTask);
							log.info("退回任务已创建: taskId={}, nodeId={}", returnTask.getTaskId(), prevNodeId);
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("退回逻辑异常: {}", e.getMessage(), e);
		}
		return true;
	}

	private void advanceProcess(WfTask task, String username) {
		try {
			// 获取流程定义 DSL
			WfInstance instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, task.getInstanceId()));
			if (instance == null) {
				log.warn("推进流程失败: 实例不存在 instanceId={}", task.getInstanceId());
				return;
			}

			WfDefinition def = defMapper
				.selectOne(new LambdaQueryWrapper<WfDefinition>().eq(WfDefinition::getId, instance.getDefId()));
			if (def == null || def.getDslJson() == null) {
				log.warn("推进流程失败: 流程定义不存在 defId={}", instance.getDefId());
				return;
			}

			DslDefinition dsl = dslParser.parse(def.getDslJson());
			DslNode currentNode = dsl.findNode(task.getNodeId());
			if (currentNode == null) {
				log.warn("推进流程失败: 当前节点不存在 nodeId={}", task.getNodeId());
				return;
			}

			// 找到出线，选择下一个节点
			var outgoings = dsl.outgoingEdges(currentNode.getNodeId());
			if (outgoings.isEmpty()) {
				// 无出线 = 流程结束
				ExecutionContext ctx = new ExecutionContext();
				ctx.setInstanceId(task.getInstanceId());
				ctx.setDefKey(task.getDefKey());
				ctx.setOperator(username);
				processEngine.recordHistory(task.getInstanceId(), null, "end", "流程结束", "end", "end", username,
						"任务完成，流程结束", "completed");
				return;
			}

			// 构建执行上下文
			ExecutionContext ctx = new ExecutionContext();
			ctx.setInstanceId(task.getInstanceId());
			ctx.setDefKey(task.getDefKey());
			ctx.setDefVersion(instance.getDefVersion());
			ctx.setCurrentNodeId(currentNode.getNodeId());
			ctx.setCurrentTaskId(task.getTaskId());
			ctx.setOperator(username);
			// 加载流程变量
			if (instance.getVarsJson() != null) {
				try {
					var varsMap = new com.fasterxml.jackson.databind.ObjectMapper().readValue(instance.getVarsJson(),
							java.util.Map.class);
					ctx.getVariables().putAll(varsMap);
				}
				catch (Exception ignored) {
				}
			}

			// 选择出线：先找命中条件的，都没有才走默认（无条件）线
			DslEdge chosen = outgoings.get(0);
			if (outgoings.size() > 1) {
				DslEdge defaultEdge = null;
				for (var edge : outgoings) {
					if (edge.getCondition() == null || edge.getCondition().isBlank()) {
						if (defaultEdge == null)
							defaultEdge = edge;
						continue; // 跳过默认线，最后兜底
					}
					if (evalCondition(edge.getCondition(), ctx)) {
						chosen = edge;
						defaultEdge = null; // 标记已命中
						break;
					}
				}
				// 没命中任何条件 → 用默认线
				if (defaultEdge != null) {
					chosen = defaultEdge;
				}
			}

			DslNode nextNode = dsl.findNode(chosen.getTargetId());
			if (nextNode != null) {
				log.info("推进流程: instanceId={}, {} → {}", task.getInstanceId(), currentNode.getNodeId(),
						nextNode.getNodeId());
				processEngine.advance(dsl, ctx, nextNode);
			}
		}
		catch (Exception e) {
			log.error("推进流程异常: instanceId={}, err={}", task.getInstanceId(), e.getMessage(), e);
		}
	}

	private boolean evalCondition(String condition, ExecutionContext ctx) {
		try {
			String expr = condition.trim();
			if (expr.startsWith("${") && expr.endsWith("}")) {
				expr = expr.substring(2, expr.length() - 1).trim();
			}
			String[] ops = { "==", "!=", ">=", "<=", ">", "<" };
			for (String op : ops) {
				int idx = expr.indexOf(op);
				if (idx > 0) {
					String left = expr.substring(0, idx).trim();
					String right = expr.substring(idx + op.length()).trim();
					Object l = resolveValue(left, ctx);
					Object r = resolveValue(right, ctx);
					return compare(l, r, op);
				}
			}
			Object val = resolveValue(expr, ctx);
			return val != null && Boolean.parseBoolean(String.valueOf(val));
		}
		catch (Exception e) {
			return false;
		}
	}

	private Object resolveValue(String token, ExecutionContext ctx) {
		if (token == null)
			return null;
		if ((token.startsWith("'") && token.endsWith("'")) || (token.startsWith("\"") && token.endsWith("\""))) {
			return token.substring(1, token.length() - 1);
		}
		try {
			return token.contains(".") ? (Object) Double.parseDouble(token) : (Object) Long.parseLong(token);
		}
		catch (NumberFormatException ignored) {
		}
		return ctx.getVariable(token);
	}

	private boolean compare(Object l, Object r, String op) {
		if (l == null && r == null)
			return "==".equals(op);
		if (l == null || r == null)
			return "!=".equals(op);
		if (l instanceof Number && r instanceof Number) {
			double lv = ((Number) l).doubleValue();
			double rv = ((Number) r).doubleValue();
			switch (op) {
				case "==":
					return lv == rv;
				case "!=":
					return lv != rv;
				case ">":
					return lv > rv;
				case "<":
					return lv < rv;
				case ">=":
					return lv >= rv;
				case "<=":
					return lv <= rv;
			}
		}
		String ls = String.valueOf(l);
		String rs = String.valueOf(r);
		switch (op) {
			case "==":
				return ls.equals(rs);
			case "!=":
				return !ls.equals(rs);
			case ">":
				return ls.compareTo(rs) > 0;
			case "<":
				return ls.compareTo(rs) < 0;
			case ">=":
				return ls.compareTo(rs) >= 0;
			case "<=":
				return ls.compareTo(rs) <= 0;
		}
		return false;
	}

	@Override
	public WorkflowTaskDTO getDetail(String taskId) {
		WfTask task = getByTaskId(taskId);
		WorkflowTaskDTO dto = new WorkflowTaskDTO();
		BeanUtils.copyProperties(task, dto);
		TaskStatus s = TaskStatus.getByCode(task.getTaskStatus());
		dto.setStatusDesc(s != null ? s.getDesc() : "未知");
		// 获取发起人
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, task.getInstanceId()));
		if (instance != null) {
			dto.setStartUser(instance.getStartUser());
		}
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteTask(String taskId) {
		taskMapper.delete(new LambdaQueryWrapper<WfTask>().eq(WfTask::getTaskId, taskId));
		return true;
	}

	private WfTask getByTaskId(String taskId) {
		WfTask task = taskMapper.selectOne(new LambdaQueryWrapper<WfTask>().eq(WfTask::getTaskId, taskId));
		if (task == null) {
			throw new BusinessException("任务不存在");
		}
		return task;
	}

	private PageResult<WorkflowTaskDTO> toPageResult(IPage<WfTask> result) {
		List<WorkflowTaskDTO> records = result.getRecords().stream().map(t -> {
			WorkflowTaskDTO dto = new WorkflowTaskDTO();
			BeanUtils.copyProperties(t, dto);
			TaskStatus s = TaskStatus.getByCode(t.getTaskStatus());
			dto.setStatusDesc(s != null ? s.getDesc() : "未知");
			return dto;
		}).toList();
		return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
	}

}
