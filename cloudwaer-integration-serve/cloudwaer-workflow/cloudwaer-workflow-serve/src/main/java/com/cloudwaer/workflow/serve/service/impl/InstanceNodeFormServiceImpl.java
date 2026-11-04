package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloudwaer.workflow.serve.engine.DslDefinition;
import com.cloudwaer.workflow.serve.engine.DslNode;
import com.cloudwaer.workflow.serve.engine.DslParser;
import com.cloudwaer.workflow.serve.entity.WfInstanceNodeForm;
import com.cloudwaer.workflow.serve.mapper.WfInstanceNodeFormMapper;
import com.cloudwaer.workflow.serve.service.InstanceNodeFormService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstanceNodeFormServiceImpl implements InstanceNodeFormService {

	@Autowired
	private WfInstanceNodeFormMapper nodeFormMapper;

	@Autowired
	private DslParser dslParser;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private com.cloudwaer.workflow.serve.mapper.WfFormMapper formMapper;

	@Override
	public void initNodeForms(String instanceId, String defKey, Integer defVersion, String dslJson) {
		if (dslJson == null || dslJson.isBlank())
			return;
		try {
			DslDefinition dsl = dslParser.parse(dslJson);
			for (DslNode node : dsl.getNodes()) {
				if (!"user_task".equals(node.getNodeType()))
					continue;
				String configJson = node.getConfigJson();
				Long formId = null;
				if (configJson != null && !configJson.isBlank()) {
					JsonNode cfg = objectMapper.readTree(configJson);
					if (cfg.has("formId") && !cfg.get("formId").isNull()) {
						formId = cfg.get("formId").asLong();
					}
				}
				String formSchema = null;
				if (formId != null) {
					var form = formMapper.selectById(formId);
					if (form != null) {
						formSchema = form.getSchemaJson();
					}
				}
				WfInstanceNodeForm record = new WfInstanceNodeForm();
				record.setInstanceId(instanceId);
				record.setNodeId(node.getNodeId());
				record.setFormId(formId);
				record.setFormSchema(formSchema);
				record.setFormData(null);
				nodeFormMapper.insert(record);
				log.info("[NodeForm] init: instanceId={}, nodeId={}, formId={}, hasSchema={}", instanceId,
						node.getNodeId(), formId, formSchema != null);
			}
		}
		catch (Exception e) {
			log.error("[NodeForm] init error: {}", e.getMessage(), e);
		}
	}

	@Override
	public void updateFormData(String instanceId, String nodeId, String formData) {
		if (formData == null || formData.isBlank())
			return;
		nodeFormMapper.update(null,
				new LambdaUpdateWrapper<WfInstanceNodeForm>().set(WfInstanceNodeForm::getFormData, formData)
					.eq(WfInstanceNodeForm::getInstanceId, instanceId)
					.eq(WfInstanceNodeForm::getNodeId, nodeId));
		log.info("[NodeForm] 更新表单数据: instanceId={}, nodeId={}", instanceId, nodeId);
	}

	@Override
	public void clearFormData(String instanceId, String nodeId) {
		nodeFormMapper.update(null,
				new LambdaUpdateWrapper<WfInstanceNodeForm>().set(WfInstanceNodeForm::getFormData, null)
					.eq(WfInstanceNodeForm::getInstanceId, instanceId)
					.eq(WfInstanceNodeForm::getNodeId, nodeId));
		log.info("[NodeForm] 清空表单数据: instanceId={}, nodeId={}", instanceId, nodeId);
	}

	@Override
	public WfInstanceNodeForm getByInstanceAndNode(String instanceId, String nodeId) {
		return nodeFormMapper
			.selectOne(new LambdaQueryWrapper<WfInstanceNodeForm>().eq(WfInstanceNodeForm::getInstanceId, instanceId)
				.eq(WfInstanceNodeForm::getNodeId, nodeId));
	}

}
