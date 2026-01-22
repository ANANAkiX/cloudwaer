package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.Preconditions;
import com.cloudwaer.flowable.api.dto.*;
import com.cloudwaer.flowable.serve.constant.FlowableConstants;
import com.cloudwaer.flowable.serve.entity.WfDeployment;
import com.cloudwaer.flowable.serve.entity.WfModel;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.mapper.WfDeploymentMapper;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.service.FlowableModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlowableModelServiceImpl extends ServiceImpl<WfModelMapper, WfModel> implements FlowableModelService {

	@Autowired
	private WfNodeActionMapper nodeActionMapper;

	@Autowired
	private WfDeploymentMapper deploymentMapper;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long saveModel(FlowableModelSaveDTO dto) {
		Preconditions.requireNonBlank(dto.getModelKey(), "modelKey is required");
		Preconditions.requireNonBlank(dto.getModelName(), "modelName is required");
		Preconditions.requireNonBlank(dto.getBpmnXml(), "bpmnXml is required");
		Preconditions.requireNonNull(dto.getEndTime(), "endTime is required");
		if (dto.getEndTime().isBefore(java.time.LocalDateTime.now())) {
			throw new BusinessException("endTime must be in the future");
		}

		if (dto.getId() == null) {
			long count = this.count(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, dto.getModelKey())
				.eq(WfModel::getModelStatus, FlowableConstants.MODEL_STATUS_RELEASED));
			if (count > 0) {
				throw new BusinessException("modelKey already exists");
			}

			WfModel model = new WfModel();
			model.setModelKey(dto.getModelKey());
			model.setModelName(dto.getModelName());
			model.setCategory(dto.getCategory());
			model.setRemark(dto.getRemark());
			model.setFormJson(dto.getFormJson());
			model.setVersion(1);
			model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
			model.setBpmnXml(ensureExecutableXml(dto.getBpmnXml()));
			model.setEndTime(dto.getEndTime());
			model.setNodeActionsJson(toJson(dto.getNodeActions()));
			this.save(model);

			saveNodeActions(model, dto.getNodeActions());
			return model.getId();
		}

		// 更新/保存草稿
		WfModel model = this.getById(dto.getId());
		if (model == null) {
			throw new BusinessException("model not found");
		}

		model.setModelName(dto.getModelName());
		model.setCategory(dto.getCategory());
		model.setRemark(dto.getRemark());
		model.setFormJson(dto.getFormJson());
		model.setBpmnXml(ensureExecutableXml(dto.getBpmnXml()));
		model.setEndTime(dto.getEndTime());
		model.setNodeActionsJson(toJson(dto.getNodeActions()));
		this.updateById(model);

		saveNodeActions(model, dto.getNodeActions());
		return model.getId();
	}

	@Override
	public FlowableModelDetailDTO getDetail(Long id) {
		if (id == null) {
			throw new BusinessException("id is required");
		}
		WfModel model = this.getById(id);
		if (model == null) {
			throw new BusinessException("model not found");
		}
		FlowableModelDetailDTO dto = new FlowableModelDetailDTO();
		BeanUtils.copyProperties(model, dto);
		dto.setNodeActions(toNodeActionDtos(model.getId()));
		return dto;
	}

	@Override
	public PageResult<FlowableModelListDTO> list(PageDTO pageDTO) {
		Page<WfModel> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());

		LambdaQueryWrapper<WfModel> wrapper = new LambdaQueryWrapper<>();
		String keyword = pageDTO.getKeywordTrimmed();
		if (keyword != null) {
			wrapper.and(w -> w.like(WfModel::getModelKey, keyword).or().like(WfModel::getModelName, keyword));
		}
		wrapper.orderByDesc(WfModel::getUpdateTime);

		IPage<WfModel> pageResult = this.page(page, wrapper);
		List<FlowableModelListDTO> records = pageResult.getRecords().stream().map(model -> {
			FlowableModelListDTO dto = new FlowableModelListDTO();
			BeanUtils.copyProperties(model, dto);
			return dto;
		}).toList();

		return new PageResult<>(records, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
	}

	@Override
	public List<FlowableModelListDTO> listVersions(String modelKey) {
		Preconditions.requireNonBlank(modelKey, "modelKey is required");
		List<WfModel> list = this.list(
				new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, modelKey).orderByDesc(WfModel::getVersion));
		List<FlowableModelListDTO> result = new ArrayList<>();
		for (WfModel model : list) {
			FlowableModelListDTO dto = new FlowableModelListDTO();
			BeanUtils.copyProperties(model, dto);
			result.add(dto);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean publish(Long id) {
		if (id == null) {
			throw new BusinessException("id is required");
		}
		WfModel model = this.getById(id);
		if (model == null) {
			throw new BusinessException("model not found");
		}
		if (model.getBpmnXml() == null || model.getBpmnXml().isBlank()) {
			throw new BusinessException("bpmnXml is required");
		}

		// 将其它已发布版本标记为已更新
		this.update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfModel>()
			.set(WfModel::getModelStatus, FlowableConstants.MODEL_STATUS_ARCHIVED)
			.eq(WfModel::getModelKey, model.getModelKey())
			.eq(WfModel::getModelStatus, FlowableConstants.MODEL_STATUS_RELEASED)
			.ne(WfModel::getId, model.getId()));

		// 将历史部署标记为归档
		deploymentMapper.update(null,
				new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfDeployment>()
					.set(WfDeployment::getDeployStatus, FlowableConstants.DEPLOY_STATUS_ARCHIVED)
					.eq(WfDeployment::getModelKey, model.getModelKey())
					.eq(WfDeployment::getDeployStatus, FlowableConstants.DEPLOY_STATUS_ACTIVE));

		// 发布到 Flowable
		Deployment deployment = repositoryService.createDeployment()
			.name(model.getModelName())
			.category(model.getCategory())
			.addInputStream(model.getModelKey() + ".bpmn20.xml",
					new java.io.ByteArrayInputStream(
							model.getBpmnXml().getBytes(java.nio.charset.StandardCharsets.UTF_8)))
			.deploy();

		ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
			.deploymentId(deployment.getId())
			.latestVersion()
			.singleResult();

		WfDeployment wfDeployment = new WfDeployment();
		wfDeployment.setModelId(model.getId());
		wfDeployment.setModelKey(model.getModelKey());
		wfDeployment.setModelVersion(model.getVersion());
		wfDeployment.setDeploymentId(deployment.getId());
		if (definition != null) {
			wfDeployment.setProcessDefinitionId(definition.getId());
			wfDeployment.setProcessDefinitionKey(definition.getKey());
			wfDeployment.setProcessDefinitionName(definition.getName());
			wfDeployment.setProcessDefinitionVersion(definition.getVersion());
		}
		wfDeployment.setFormJson(model.getFormJson());
		wfDeployment.setDeployStatus(FlowableConstants.DEPLOY_STATUS_ACTIVE);
		deploymentMapper.insert(wfDeployment);

		model.setModelStatus(FlowableConstants.MODEL_STATUS_RELEASED);
		this.updateById(model);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean copy(FlowableModelCopyDTO dto) {
		if (dto == null || dto.getSourceId() == null) {
			throw new BusinessException("sourceId is required");
		}
		if (dto.getNewModelKey() == null || dto.getNewModelKey().isBlank()) {
			throw new BusinessException("newModelKey is required");
		}
		if (dto.getNewModelName() == null || dto.getNewModelName().isBlank()) {
			throw new BusinessException("newModelName is required");
		}
		WfModel source = this.getById(dto.getSourceId());
		if (source == null) {
			throw new BusinessException("source model not found");
		}
		long exists = this.count(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, dto.getNewModelKey()));
		if (exists > 0) {
			throw new BusinessException("modelKey already exists");
		}

		WfModel model = new WfModel();
		model.setModelKey(dto.getNewModelKey());
		model.setModelName(dto.getNewModelName());
		model.setCategory(source.getCategory());
		model.setRemark(source.getRemark());
		model.setFormJson(source.getFormJson());
		model.setVersion(1);
		model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
		model.setBpmnXml(source.getBpmnXml());
		model.setEndTime(source.getEndTime());
		model.setNodeActionsJson(source.getNodeActionsJson());
		this.save(model);

		List<WfNodeAction> actions = nodeActionMapper
			.selectList(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, source.getId())
				.eq(WfNodeAction::getEnabled, 1));
		for (WfNodeAction action : actions) {
			WfNodeAction copied = new WfNodeAction();
			BeanUtils.copyProperties(action, copied, "id");
			copied.setModelId(model.getId());
			copied.setModelKey(model.getModelKey());
			copied.setModelVersion(model.getVersion());
			nodeActionMapper.insert(copied);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean rollback(FlowableModelRollbackDTO dto) {
		if (dto.getModelKey() == null || dto.getModelKey().isBlank() || dto.getVersion() == null) {
			throw new BusinessException("modelKey and version are required");
		}
		WfModel model = this.getOne(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, dto.getModelKey())
			.eq(WfModel::getVersion, dto.getVersion()));
		if (model == null) {
			throw new BusinessException("target model not found");
		}
		WfModel newModel = new WfModel();
		newModel.setModelKey(model.getModelKey());
		newModel.setModelName(model.getModelName());
		newModel.setCategory(model.getCategory());
		newModel.setRemark(model.getRemark());
		newModel.setFormJson(model.getFormJson());
		newModel.setVersion(resolveNextVersion(model.getModelKey()));
		newModel.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
		newModel.setBpmnXml(model.getBpmnXml());
		newModel.setNodeActionsJson(model.getNodeActionsJson());
		newModel.setEndTime(model.getEndTime());
		this.save(newModel);
		copyNodeActions(model.getId(), newModel);
		return true;
	}

	@Override
	public String getBpmnXml(Long id) {
		WfModel model = this.getById(id);
		if (model == null) {
			throw new BusinessException("model not found");
		}
		return model.getBpmnXml();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteModel(Long id) {
		WfModel model = this.getById(id);
		if (model == null) {
			throw new BusinessException("model not found");
		}
		List<WfDeployment> deployments = deploymentMapper
			.selectList(new LambdaQueryWrapper<WfDeployment>().eq(WfDeployment::getModelId, id));
		for (WfDeployment deployment : deployments) {
			if (deployment.getDeploymentId() != null && !deployment.getDeploymentId().isBlank()) {
				repositoryService.deleteDeployment(deployment.getDeploymentId(), true);
			}
		}
		deploymentMapper.delete(new LambdaQueryWrapper<WfDeployment>().eq(WfDeployment::getModelId, id));
		nodeActionMapper.delete(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, id));
		return this.removeById(id);
	}

	private void saveNodeActions(WfModel model, List<FlowableNodeActionDTO> nodeActions) {
		nodeActionMapper.delete(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, model.getId()));
		if (nodeActions == null || nodeActions.isEmpty()) {
			return;
		}
		for (FlowableNodeActionDTO dto : nodeActions) {
			WfNodeAction action = new WfNodeAction();
			action.setModelId(model.getId());
			action.setModelKey(model.getModelKey());
			action.setModelVersion(model.getVersion());
			action.setNodeId(dto.getNodeId());
			action.setNodeName(dto.getNodeName());
			action.setEventType(dto.getEventType());
			action.setActionType(dto.getActionType());
			action.setActionConfig(dto.getActionConfig());
			action.setEnabled(1);
			nodeActionMapper.insert(action);
		}
	}

	private List<FlowableNodeActionDTO> toNodeActionDtos(Long modelId) {
		List<WfNodeAction> actions = nodeActionMapper
			.selectList(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, modelId)
				.eq(WfNodeAction::getEnabled, 1));
		List<FlowableNodeActionDTO> result = new ArrayList<>();
		for (WfNodeAction action : actions) {
			FlowableNodeActionDTO dto = new FlowableNodeActionDTO();
			dto.setNodeId(action.getNodeId());
			dto.setNodeName(action.getNodeName());
			dto.setEventType(action.getEventType());
			dto.setActionType(action.getActionType());
			dto.setActionConfig(action.getActionConfig());
			result.add(dto);
		}
		return result;
	}

	private String toJson(List<FlowableNodeActionDTO> actions) {
		if (actions == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(actions);
		}
		catch (JsonProcessingException e) {
			throw new BusinessException("nodeActions json invalid");
		}
	}

	private String ensureExecutableXml(String xml) {
		if (xml == null || xml.isBlank()) {
			return xml;
		}
		String cleaned = xml.replaceAll("(<(bpmn:)?process\\b[^>]*?)\\s+isExecutable=\"[^\"]*\"", "$1");
		if (!cleaned.equals(xml)) {
			return cleaned.replaceFirst("(<(bpmn:)?process\\b[^>]*)(>)", "$1 isExecutable=\"true\"$3");
		}
		return xml.replaceFirst("(<(bpmn:)?process\\b[^>]*)(>)", "$1 isExecutable=\"true\"$3");
	}

	private int resolveNextVersion(String modelKey) {
		WfModel latest = this.getOne(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, modelKey)
			.orderByDesc(WfModel::getVersion)
			.last("limit 1"));
		if (latest == null || latest.getVersion() == null) {
			return 1;
		}
		return latest.getVersion() + 1;
	}

	private void copyNodeActions(Long sourceId, WfModel target) {
		List<WfNodeAction> actions = nodeActionMapper
			.selectList(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, sourceId)
				.eq(WfNodeAction::getEnabled, 1));
		for (WfNodeAction action : actions) {
			WfNodeAction copy = new WfNodeAction();
			BeanUtils.copyProperties(action, copy, "id");
			copy.setModelId(target.getId());
			copy.setModelKey(target.getModelKey());
			copy.setModelVersion(target.getVersion());
			nodeActionMapper.insert(copy);
		}
	}

}