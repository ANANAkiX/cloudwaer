package com.cloudwaer.workflow.serve.engine.node;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.workflow.serve.engine.*;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import com.cloudwaer.workflow.serve.mapper.WfDefinitionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 子流程节点执行器：嵌套启动另一个流程
 * <p>
 * config: {"subDefKey":"xxx","waitComplete":true,"passVariables":true}
 */
@Slf4j
@Component
public class SubProcessNodeExecutor implements NodeExecutor {

	@Autowired
	@Lazy
	private ProcessEngine processEngine;

	@Autowired
	private WfDefinitionMapper defMapper;

	@Autowired
	private DslParser dslParser;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String getNodeType() {
		return "sub_process";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		String config = node.getConfigJson();
		if (config == null || config.isBlank()) {
			log.warn("[SUB_PROCESS] 无配置，跳过: nodeId={}", node.getNodeId());
			return NodeResult.auto(null);
		}

		try {
			JsonNode root = objectMapper.readTree(config);
			String subDefKey = root.has("subDefKey") ? root.get("subDefKey").asText() : null;
			boolean waitComplete = root.has("waitComplete") && root.get("waitComplete").asBoolean(false);
			boolean passVariables = !root.has("passVariables") || root.get("passVariables").asBoolean(true);

			if (subDefKey == null || subDefKey.isBlank()) {
				log.warn("[SUB_PROCESS] subDefKey 为空，跳过");
				return NodeResult.auto(null);
			}

			// 查找子流程最新已发布定义：引擎 startProcess 需要真实 DSL，
			// 传 null 会在 @Transactional 方法内抛出并把外层事务标记 rollback-only
			WfDefinition def = defMapper
				.selectOne(new LambdaQueryWrapper<WfDefinition>().eq(WfDefinition::getDefKey, subDefKey)
					.eq(WfDefinition::getDefStatus, 1)
					.orderByDesc(WfDefinition::getVersion)
					.last("LIMIT 1"));
			if (def == null || def.getDslJson() == null) {
				return NodeResult.error("子流程定义不存在或未发布: " + subDefKey);
			}
			// 先做 DSL 校验，避免异常发生在事务方法内
			dslParser.parse(def.getDslJson());

			// 启动子流程
			Map<String, Object> vars = passVariables ? context.getVariables() : Map.of();
			String subInstanceId = processEngine.startProcess(subDefKey, def.getDslJson(), null,
					"子流程-" + node.getNodeName(), null, context.getOperator(), null, vars, def.getId());
			log.info("[SUB_PROCESS] 子流程已启动: subDefKey={}, subInstanceId={}, waitComplete={}", subDefKey, subInstanceId,
					waitComplete);

			// TODO: waitComplete=true 时等待子流程完成后继续
			// 当前版本：立即继续（不等待）
			return NodeResult.auto(null);
		}
		catch (Exception e) {
			log.error("[SUB_PROCESS] 启动失败: nodeId={}, err={}", node.getNodeId(), e.getMessage(), e);
			return NodeResult.error("子流程启动失败: " + e.getMessage());
		}
	}

}
