package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.workflow.serve.entity.WfInstanceNodeForm;

/**
 * 实例节点表单服务
 */
public interface InstanceNodeFormService {

	/**
	 * 流程启动时初始化节点表单记录（只对有关联表单的用户任务节点）
	 */
	void initNodeForms(String instanceId, String defKey, Integer defVersion, String dslJson);

	/**
	 * 审批提交后更新表单数据
	 */
	void updateFormData(String instanceId, String nodeId, String formData);

	/**
	 * 驳回时清空表单数据
	 */
	void clearFormData(String instanceId, String nodeId);

	/**
	 * 查询节点表单记录
	 */
	WfInstanceNodeForm getByInstanceAndNode(String instanceId, String nodeId);

}
