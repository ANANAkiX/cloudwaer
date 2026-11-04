package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 并行分支节点执行器：同时激活所有出线
 * <p>
 * 并行汇聚：当所有入线都到达时，才继续向下流转。 简化实现：由 ProcessEngine 的 advance 处理多出线并行推进。
 */
@Slf4j
@Component
public class ParallelNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "parallel";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		// 并行节点：返回 AUTO，引擎会对每条出线分别推进
		// 汇聚逻辑：引擎检查所有入线是否都已激活
		log.info("并行分支节点执行: nodeId={}", node.getNodeId());
		return NodeResult.auto(null);
	}

}
