package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import org.springframework.stereotype.Component;

/**
 * 结束节点执行器：标记流程结束
 */
@Component
public class EndNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "end";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		return NodeResult.end();
	}

}
