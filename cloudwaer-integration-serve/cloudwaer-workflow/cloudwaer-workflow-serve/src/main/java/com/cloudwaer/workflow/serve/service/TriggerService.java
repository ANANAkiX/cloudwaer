package com.cloudwaer.workflow.serve.service;

/**
 * 触发器服务：外部唤醒触发器节点
 */
public interface TriggerService {

	/**
	 * 触发唤醒：使触发器节点的流程继续推进
	 * @param instanceId 流程实例ID
	 * @param nodeId 触发器节点ID
	 */
	void fire(String instanceId, String nodeId);

}
