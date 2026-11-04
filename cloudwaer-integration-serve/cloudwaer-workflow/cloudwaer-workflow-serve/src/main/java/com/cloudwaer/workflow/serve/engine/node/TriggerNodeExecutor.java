package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 触发器节点执行器：等待外部事件触发后流转
 * <p>
 * 触发器类型（configJson）： {"triggerType":"signal","signalName":"xxx"} /
 * {"triggerType":"message","messageName":"xxx"}
 * {"triggerType":"webhook","webhookUrl":"xxx"} / {"triggerType":"timer","cron":"xxx"}
 */
@Slf4j
@Component
public class TriggerNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "trigger";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		log.info("触发器节点: nodeId={}, config={}", node.getNodeId(), node.getConfigJson());
		// 返回 SIGNAL，等待外部触发
		// 外部通过 TriggerService.fire(instanceId, nodeId) 唤醒
		return NodeResult.signal();
	}

}
