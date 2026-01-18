package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.flowable.serve.entity.WfNodeAction;

import java.util.List;

/**
 * 节点动作服务接口
 * 
 * @author cloudwaer
 * @since 2026-01-15
 */
public interface WfNodeActionService {

    /**
     * 查询节点动作列表
     */
    List<WfNodeAction> listActions(String modelKey, Integer modelVersion, String nodeId, String eventType);

    /**
     * 保存节点动作配置
     */
    boolean saveNodeAction(WfNodeAction nodeAction);

    /**
     * 删除节点动作配置
     */
    boolean deleteNodeAction(Long id);

    /**
     * 执行节点动作
     */
    void executeNodeActions(String processDefinitionId, String nodeId, String eventType, String processInstanceId);

    /**
     * 获取节点动作配置
     */
    String getNodeActionConfig(String processDefinitionId, String nodeId);

    /**
     * 批量保存节点动作配置
     */
    boolean batchSaveNodeActions(List<WfNodeAction> nodeActions);

    /**
     * 根据模型ID查询所有动作配置
     */
    List<WfNodeAction> listActionsByModelId(Long modelId);

    /**
     * 启用/禁用节点动作
     */
    boolean updateNodeActionStatus(Long id, Integer enabled);
}
