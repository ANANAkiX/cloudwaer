package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import org.springframework.stereotype.Component;

/**
 * 开始节点执行器：自动流转到下一个节点
 */
@Component
public class StartNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "start";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		return NodeResult.auto(null);
	}

}
