package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import com.cloudwaer.workflow.serve.entity.WfCcRecord;
import com.cloudwaer.workflow.serve.mapper.WfCcRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 抄送节点执行器：发送抄送通知，不阻断流程
 */
@Slf4j
@Component
public class CcNodeExecutor implements NodeExecutor {

	@Autowired
	private WfCcRecordMapper ccRecordMapper;

	@Override
	public String getNodeType() {
		return "cc";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		// 创建抄送记录
		WfCcRecord record = new WfCcRecord();
		record.setInstanceId(context.getInstanceId());
		record.setDefKey(context.getDefKey());
		record.setNodeId(node.getNodeId());
		record.setCcType(node.getAssigneeType() != null ? node.getAssigneeType() : "user");
		record.setCcTarget(node.getAssigneeVal());
		record.setCcBy(context.getOperator());
		record.setReadStatus(0);
		ccRecordMapper.insert(record);

		log.info("抄送已创建: instanceId={}, target={}", context.getInstanceId(), node.getAssigneeVal());

		// 抄送不阻断流程，自动流转
		// 出线由 ProcessEngine 统一解析
		return NodeResult.auto(null);
	}

}
