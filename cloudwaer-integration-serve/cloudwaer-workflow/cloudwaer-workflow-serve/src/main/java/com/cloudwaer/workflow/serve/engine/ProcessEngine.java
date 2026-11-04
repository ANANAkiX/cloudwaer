package com.cloudwaer.workflow.serve.engine;

import com.cloudwaer.workflow.serve.entity.WfHistory;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.engine.listener.EventPublisher;
import com.cloudwaer.workflow.serve.engine.listener.WorkflowEvent;
import com.cloudwaer.workflow.serve.mapper.WfHistoryMapper;
import com.cloudwaer.workflow.serve.mapper.WfInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.cloudwaer.workflow.serve.engine.scheduler.SchedulerProvider;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import com.cloudwaer.workflow.serve.entity.WfTask;
import com.cloudwaer.workflow.serve.mapper.WfDefinitionMapper;
import com.cloudwaer.workflow.serve.mapper.WfTaskMapper;

/**
 * 流程引擎主控：负责驱动流程从一个节点流转到下一个节点。
 * <p>
 * 核心循环：executeNode → 根据 NodeResult 决定下一步 → 继续或等待。
 */
@Slf4j
@Component
public class ProcessEngine {

	@Autowired
	private DslParser dslParser;

	@Autowired
	private NodeExecutorRegistry executorRegistry;

	@Autowired
	private WfInstanceMapper instanceMapper;

	@Autowired
	private WfTaskMapper taskMapper;

	@Autowired(required = false)
	private EventPublisher eventPublisher;

	@Autowired
	private WfDefinitionMapper defMapper;

	@Autowired(required = false)
	@org.springframework.context.annotation.Lazy
	private SchedulerProvider schedulerProvider;

	@Autowired
	private WfHistoryMapper historyMapper;

	/**
	 * 启动流程实例
	 */
	@Transactional(rollbackFor = Exception.class)
	public String startProcess(String defKey, String dslJson, String businessKey, String title, String formJson,
			String startUser, String priority, java.util.Map<String, Object> variables, Long defId) {
		DslDefinition dsl = dslParser.parse(dslJson);
		DslNode startNode = dsl.findStartNode();
		if (startNode == null) {
			throw new RuntimeException("流程定义缺少开始节点");
		}

		// 创建实例
		WfInstance instance = new WfInstance();
		String instanceId = UUID.randomUUID().toString().replace("-", "");
		instance.setInstanceId(instanceId);
		instance.setDefId(defId);
		instance.setDefKey(defKey);
		instance.setDefName(dsl.getDefName());
		instance.setBusinessKey(businessKey);
		instance.setTitle(title);
		instance.setFormJson(formJson);
		instance.setInstStatus(0); // running
		instance.setPriority(priority != null ? priority : "normal");
		instance.setStartUser(startUser);
		instance.setStartTime(LocalDateTime.now());
		instance.setVarsJson(variables != null
				? new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(variables).toString() : null);
		instanceMapper.insert(instance);

		// 记录历史
		recordHistory(instanceId, null, startNode.getNodeId(), startNode.getNodeName(), "start", "start", startUser,
				"流程启动", null);

		// 从开始节点驱动
		ExecutionContext ctx = new ExecutionContext();
		ctx.setInstanceId(instanceId);
		ctx.setDefId(defId);
		ctx.setDefKey(defKey);
		ctx.setCurrentNodeId(startNode.getNodeId());
		ctx.setOperator(startUser);
		if (variables != null) {
			ctx.getVariables().putAll(variables);
		}

		publishEvent("process:start", instanceId, null, startNode.getNodeId(), startNode.getNodeName(), startUser,
				null);
		advance(dsl, ctx, startNode);
		return instanceId;
	}

	/**
	 * 驱动流程前进（核心循环）
	 */
	public void advance(DslDefinition dsl, ExecutionContext ctx, DslNode currentNode) {
		DslNode node = currentNode;
		int maxSteps = 100; // 防止死循环
		while (node != null && maxSteps-- > 0) {
			// 并行汇聚检查：仅 parallel 节点需多分支到齐；条件分支下游不汇聚
			var incomingEdges = dsl.incomingEdges(node.getNodeId());
			boolean isParallelJoin = "parallel".equals(node.getNodeType()) && incomingEdges.size() > 1;
			if (isParallelJoin) {
				int arrived = ctx.getArrivalCount().merge(node.getNodeId(), 1, Integer::sum);
				if (arrived < incomingEdges.size()) {
					log.info("并行汇聚等待: nodeId={}, {}/{} 分支已到", node.getNodeId(), arrived, incomingEdges.size());
					return;
				}
				ctx.getArrivalCount().remove(node.getNodeId());
				log.info("并行汇聚完成: nodeId={}, 全部 {} 分支已到", node.getNodeId(), incomingEdges.size());
			}

			NodeExecutor executor = executorRegistry.get(node.getNodeType());
			if (executor == null) {
				log.error("未找到节点执行器: nodeType={}, nodeId={}", node.getNodeType(), node.getNodeId());
				recordHistory(ctx.getInstanceId(), ctx.getCurrentTaskId(), node.getNodeId(), node.getNodeName(),
						node.getNodeType(), "error", ctx.getOperator(), "执行器缺失: " + node.getNodeType(), "error");
				return;
			}

			ctx.setCurrentNodeId(node.getNodeId());
			publishEvent("node:enter", ctx.getInstanceId(), ctx.getCurrentTaskId(), node.getNodeId(),
					node.getNodeName(), ctx.getOperator(), null);
			NodeResult result;
			try {
				result = executor.execute(node, ctx);
			}
			catch (Exception e) {
				log.error("节点执行异常: nodeId={}, err={}", node.getNodeId(), e.getMessage(), e);
				recordHistory(ctx.getInstanceId(), ctx.getCurrentTaskId(), node.getNodeId(), node.getNodeName(),
						node.getNodeType(), "error", ctx.getOperator(), "执行异常: " + e.getMessage(), "error");
				return;
			}

			if (result.hasError()) {
				log.error("节点执行错误: nodeId={}, err={}", node.getNodeId(), result.getError());
				return;
			}

			// 根据结果决定下一步
			switch (result.getNextType()) {
				case END:
					handleEnd(ctx, node);
					return;
				case WAIT:
					// 等待人工处理
					updateCurrentNodes(ctx.getInstanceId(), node.getNodeId());
					return;
				case TIMER:
					// 调度定时器，到时后自动推进
					updateCurrentNodes(ctx.getInstanceId(), node.getNodeId());
					scheduleTimer(node, ctx);
					return;
				case SIGNAL:
					// 等待外部触发（TriggerService.fire 唤醒）
					updateCurrentNodes(ctx.getInstanceId(), node.getNodeId());
					return;
				case AUTO:
				default:
					List<String> nextIds = result.getNextNodeIds();
					// 如果执行器未指定出线，从 DSL 解析出线
					if (nextIds == null || nextIds.isEmpty()) {
						var outgoings = dsl.outgoingEdges(node.getNodeId());
						log.info("[Engine] 节点 {} 出线数: {}", node.getNodeId(), outgoings.size());
						if (outgoings.isEmpty()) {
							handleEnd(ctx, node);
							return;
						}
						// 条件分支：求值条件选出线；并行分支：全部走
						if (outgoings.size() > 1 && "condition".equals(node.getNodeType())) {
							nextIds = pickConditionalEdge(outgoings, ctx);
						}
						else {
							nextIds = outgoings.stream().map(DslEdge::getTargetId).toList();
						}
					}
					log.info("[Engine] 节点 {} 下一步: {}", node.getNodeId(), nextIds);
					if (nextIds.size() == 1) {
						node = dsl.findNode(nextIds.get(0));
					}
					else {
						// 并行分支：先执行会产生任务的分支（user_task），再执行自动分支
						// 确保 handleEnd 时所有任务已创建
						var waitingNodes = new java.util.ArrayList<DslNode>();
						var autoNodes = new java.util.ArrayList<DslNode>();
						for (String nextId : nextIds) {
							DslNode n = dsl.findNode(nextId);
							if (n != null) {
								if ("user_task".equals(n.getNodeType())) {
									waitingNodes.add(n);
								}
								else {
									autoNodes.add(n);
								}
							}
						}
						// 先处理 user_task（创建任务后返回 WAIT，不会触发 handleEnd）
						for (DslNode n : waitingNodes) {
							advance(dsl, ctx, n);
						}
						// 再处理自动节点
						for (DslNode n : autoNodes) {
							advance(dsl, ctx, n);
						}
						return;
					}
					break;
			}
		}
		if (maxSteps <= 0) {
			log.error("流程推进超过最大步数，可能存在死循环: instanceId={}", ctx.getInstanceId());
		}
	}

	/**
	 * 调度延时器节点
	 */
	private void scheduleTimer(DslNode node, ExecutionContext ctx) {
		try {
			long delayMs = resolveTimerDelayMs(node);
			String jobId = ctx.getInstanceId() + ":" + node.getNodeId();
			if (schedulerProvider != null) {
				schedulerProvider.schedule(jobId, ctx.getInstanceId(), node.getNodeId(), delayMs);
				log.info("延时器已调度: instanceId={}, nodeId={}, delayMs={}", ctx.getInstanceId(), node.getNodeId(),
						delayMs);
			}
		}
		catch (Exception e) {
			log.error("延时器调度失败: nodeId={}, err={}", node.getNodeId(), e.getMessage(), e);
		}
	}

	/**
	 * 解析延时器延时毫秒数
	 */
	private long resolveTimerDelayMs(DslNode node) {
		String config = node.getConfigJson();
		if (config == null || config.isBlank()) {
			return 60_000; // 默认 1 分钟
		}
		try {
			com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = om.readTree(config);
			long value = root.has("delayValue") ? root.get("delayValue").asLong(30) : 30;
			String unit = root.has("delayUnit") ? root.get("delayUnit").asText("minutes") : "minutes";
			switch (unit) {
				case "seconds":
					return value * 1000;
				case "minutes":
					return value * 60 * 1000;
				case "hours":
					return value * 60 * 60 * 1000;
				case "days":
					return value * 24 * 60 * 60 * 1000;
				default:
					return value * 60 * 1000;
			}
		}
		catch (Exception e) {
			log.warn("解析延时配置失败: {}", e.getMessage());
			return 60_000;
		}
	}

	/**
	 * 条件分支选线：先求值条件，命中即选；都没命中走默认线
	 */
	private List<String> pickConditionalEdge(java.util.List<DslEdge> outgoings, ExecutionContext ctx) {
		log.info("[Engine] 条件选线: {} 条出线, 变量={}", outgoings.size(), ctx.getVariables());
		DslEdge defaultEdge = null;
		for (DslEdge edge : outgoings) {
			String cond = edge.getCondition();
			log.info("[Engine] 求值条件: \"{}\" -> target={}", cond, edge.getTargetId());
			if (cond == null || cond.isBlank()) {
				if (defaultEdge == null)
					defaultEdge = edge;
				continue;
			}
			boolean hit = evalCondition(cond, ctx);
			log.info("[Engine] 条件结果: \"{}\" = {}", cond, hit);
			if (hit) {
				return List.of(edge.getTargetId());
			}
		}
		if (defaultEdge != null) {
			log.info("[Engine] 未命中，走默认线 -> {}", defaultEdge.getTargetId());
			return List.of(defaultEdge.getTargetId());
		}
		return List.of(outgoings.get(0).getTargetId());
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
			log.warn("条件求值失败: {}", condition);
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

	private void publishEvent(String type, String instId, String taskId, String nodeId, String nodeName,
			String operator, Object payload) {
		if (eventPublisher != null) {
			try {
				eventPublisher.publish(type, instId, taskId, nodeId, nodeName, operator, payload);
			}
			catch (Exception e) {
				log.warn("事件发布失败: type={}, err={}", type, e.getMessage());
			}
		}
	}

	private void handleEnd(ExecutionContext ctx, DslNode endNode) {
		// 并行分支：检查是否还有活跃任务，有则只记历史不结束流程
		long activeTasks = taskMapper
			.selectCount(new LambdaQueryWrapper<WfTask>().eq(WfTask::getInstanceId, ctx.getInstanceId())
				.in(WfTask::getTaskStatus, 0, 1));
		if (activeTasks > 0) {
			log.info("分支到达结束节点，但还有 {} 个活跃任务，不结束流程: instanceId={}", activeTasks, ctx.getInstanceId());
			recordHistory(ctx.getInstanceId(), null, endNode.getNodeId(), endNode.getNodeName(), endNode.getNodeType(),
					"branch_end", ctx.getOperator(), "分支完成", null);
			return;
		}

		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, ctx.getInstanceId()));
		if (instance != null) {
			instance.setInstStatus(1); // completed
			instance.setEndTime(LocalDateTime.now());
			instance.setCurrentNodes(null);
			instanceMapper.updateById(instance);
		}
		recordHistory(ctx.getInstanceId(), null, endNode.getNodeId(), endNode.getNodeName(), "end", "end",
				ctx.getOperator(), "流程结束", "completed");
		publishEvent("process:end", ctx.getInstanceId(), null, endNode.getNodeId(), endNode.getNodeName(),
				ctx.getOperator(), null);
	}

	private void updateCurrentNodes(String instanceId, String nodeId) {
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance != null) {
			instance.setCurrentNodes(nodeId);
			instanceMapper.updateById(instance);
		}
	}

	/**
	 * 记录全链路历史
	 */
	public void recordHistory(String instanceId, String taskId, String nodeId, String nodeName, String nodeType,
			String action, String operator, String detail, String result) {
		try {
			WfHistory h = new WfHistory();
			h.setInstanceId(instanceId);
			h.setTaskId(taskId);
			h.setNodeId(nodeId);
			h.setNodeName(nodeName);
			h.setNodeType(nodeType);
			h.setAction(action);
			h.setOperator(operator);
			h.setDetail(detail);
			h.setResult(result);
			historyMapper.insert(h);
		}
		catch (Exception e) {
			log.warn("记录历史失败: {}", e.getMessage());
		}
	}

	/**
	 * 从指定节点恢复执行（定时器到期/外部触发时调用）
	 */
	public void resumeFromNode(String instanceId, String nodeId) {
		try {
			WfInstance instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
			if (instance == null) {
				log.warn("恢复执行失败: 实例不存在 instanceId={}", instanceId);
				return;
			}

			// 获取流程定义 DSL
			WfDefinition def = null;
			if (instance.getDefId() != null) {
				def = defMapper.selectById(instance.getDefId());
			}
			if (def == null || def.getDslJson() == null) {
				log.warn("恢复执行失败: 流程定义缺失 instanceId={}", instanceId);
				return;
			}

			DslDefinition dsl = dslParser.parse(def.getDslJson());
			DslNode resumeNode = dsl.findNode(nodeId);
			if (resumeNode == null) {
				log.warn("恢复执行失败: 节点不存在 nodeId={}", nodeId);
				return;
			}

			ExecutionContext ctx = new ExecutionContext();
			ctx.setInstanceId(instanceId);
			ctx.setDefKey(instance.getDefKey());
			ctx.setDefVersion(instance.getDefVersion());
			ctx.setCurrentNodeId(nodeId);
			ctx.setOperator(instance.getStartUser());

			// 找到出线，推进到下一个节点
			var outgoings = dsl.outgoingEdges(nodeId);
			if (!outgoings.isEmpty()) {
				DslNode nextNode = dsl.findNode(outgoings.get(0).getTargetId());
				if (nextNode != null) {
					log.info("定时器唤醒推进: instanceId={}, {} → {}", instanceId, nodeId, nextNode.getNodeId());
					advance(dsl, ctx, nextNode);
				}
			}

			recordHistory(instanceId, null, nodeId, resumeNode.getNodeName(), resumeNode.getNodeType(), "timer_fire",
					instance.getStartUser(), "定时器到期，流程继续", null);
		}
		catch (Exception e) {
			log.error("恢复执行异常: instanceId={}, nodeId={}, err={}", instanceId, nodeId, e.getMessage(), e);
		}
	}

}
