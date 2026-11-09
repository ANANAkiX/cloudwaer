package com.cloudwaer.workflow.serve.engine.action;

/**
 * 动作执行器 SPI 接口。
 * <p>
 * 每种动作类型实现一个执行器，通过 Spring Bean 自动注册到 {@code ActionDispatcher}。
 */
public interface ActionExecutor {

	/**
	 * 动作类型（notify/http/log/mq/script）
	 */
	String getActionType();

	/**
	 * 执行动作
	 * @param context 动作上下文
	 * @param config 动作配置 JSON
	 */
	void execute(ActionContext context, String config);

}
