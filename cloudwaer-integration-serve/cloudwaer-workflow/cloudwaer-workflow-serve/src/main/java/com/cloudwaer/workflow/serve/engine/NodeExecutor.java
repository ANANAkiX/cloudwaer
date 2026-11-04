package com.cloudwaer.workflow.serve.engine;

/**
 * 节点执行器 SPI 接口。
 * <p>
 * 每种节点类型实现一个执行器，通过 Spring Bean 自动注册到 {@code NodeExecutorRegistry}，由 {@code ProcessEngine}
 * 按 nodeType 分发。
 */
public interface NodeExecutor {

	/**
	 * 节点类型（对应 NodeType 枚举 code）
	 */
	String getNodeType();

	/**
	 * 执行节点逻辑
	 * @param node 节点定义
	 * @param context 执行上下文
	 * @return 执行结果（决定下一步）
	 */
	NodeResult execute(DslNode node, ExecutionContext context);

}
