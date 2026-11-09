package com.cloudwaer.workflow.serve.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 节点执行器注册表（SPI 模式）
 * <p>
 * 懒加载：避免在 Bean 构造阶段触发过早初始化。
 */
@Component
public class NodeExecutorRegistry {

	@Autowired(required = false)
	private List<NodeExecutor> executors;

	private volatile Map<String, NodeExecutor> executorMap;

	private void ensureInit() {
		if (executorMap == null) {
			synchronized (this) {
				if (executorMap == null) {
					executorMap = executors != null ? executors.stream()
						.collect(Collectors.toMap(NodeExecutor::getNodeType, Function.identity())) : Map.of();
				}
			}
		}
	}

	public NodeExecutor get(String nodeType) {
		ensureInit();
		return nodeType != null ? executorMap.get(nodeType) : null;
	}

	public boolean has(String nodeType) {
		ensureInit();
		return nodeType != null && executorMap.containsKey(nodeType);
	}

}
