package com.cloudwaer.workflow.serve.engine.scheduler;

/**
 * 调度器提供商 SPI 接口。
 * <p>
 * 当前实现：{@code SpringTimerScheduler}（Spring @Scheduled + DB 轮询）。 后续可替换为 MQ 延迟消息 / Quartz
 * 实现，无需改业务代码。
 */
public interface SchedulerProvider {

	/**
	 * 调度器类型标识
	 */
	String getType();

	/**
	 * 调度一个延时任务
	 * @param jobId 任务ID（幂等键）
	 * @param instanceId 流程实例ID
	 * @param nodeId 节点ID
	 * @param delayMs 延时毫秒数
	 */
	void schedule(String jobId, String instanceId, String nodeId, long delayMs);

	/**
	 * 取消一个延时任务
	 * @param jobId 任务ID
	 */
	void cancel(String jobId);

}
