package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import org.flowable.engine.repository.Deployment;

import java.util.List;

/**
 * BPMN部署服务接口
 *
 * @author cloudwaer
 * @since 2026-01-15
 */
public interface BpmnDeployService {

	/**
	 * 部署BPMN流程定义
	 */
	Deployment deployBpmn(String bpmnXml, String deploymentName, String category);

	/**
	 * 解析BPMN XML并提取节点信息
	 */
	List<String> extractNodeIds(String bpmnXml);

	/**
	 * 解析BPMN XML并提取用户任务节点
	 */
	List<String> extractUserTaskIds(String bpmnXml);

	/**
	 * 动态部署带节点配置的流程
	 */
	Deployment deployBpmnWithActions(String bpmnXml, String deploymentName, String category,
			List<WfNodeAction> nodeActions);

	/**
	 * 更新流程定义（重新部署）
	 */
	Deployment updateBpmn(String bpmnXml, String deploymentName, String category, String processKey);

	/**
	 * 删除部署
	 */
	boolean deleteDeployment(String deploymentId);

	/**
	 * 获取部署详情
	 */
	Deployment getDeployment(String deploymentId);

	/**
	 * 验证BPMN XML格式
	 */
	boolean validateBpmnXml(String bpmnXml);

	/**
	 * 生成流程图SVG
	 */
	String generateProcessDiagram(String processDefinitionId);

}
