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
			WfModel model = new WfModel();
			model.setModelKey(dto.getModelKey());
			model.setModelName(dto.getModelName());
			model.setCategory(dto.getCategory());
			model.setRemark(dto.getRemark());
			model.setFormJson(dto.getFormJson());
			model.setVersion(resolveNextVersion(dto.getModelKey()));
			model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
			model.setBpmnXml(ensureExecutableXml(dto.getBpmnXml()));
			model.setEndTime(dto.getEndTime());
			model.setNodeActionsJson(toJson(dto.getNodeActions()));
			this.save(model);

			saveNodeActions(model, dto.getNodeActions());
			return model.getId();
		}

		// 更新草稿 / 已更新 / 已回滚
		WfModel model = this.getById(dto.getId());
		if (model == null) {
			throw new BusinessException("model not found");
		}
		if (model.getModelStatus() != null && model.getModelStatus() == FlowableConstants.MODEL_STATUS_ARCHIVED) {
			throw new BusinessException("历史归档版本不可直接编辑，请回退后再修改");
		}

		// 编辑「已发布」版本：若已有工作副本则就地更新，否则新建一条「已更新未发布」
		if (model.getModelStatus() != null && model.getModelStatus() == FlowableConstants.MODEL_STATUS_RELEASED) {
			WfModel working = findWorkingCopy(model.getModelKey());
			if (working != null) {
				applyModelContent(working, dto);
				working.setModelStatus(FlowableConstants.MODEL_STATUS_UPDATED);
				this.updateById(working);
				saveNodeActions(working, dto.getNodeActions());
				return working.getId();
			}
			WfModel next = new WfModel();
			next.setModelKey(model.getModelKey());
			applyModelContent(next, dto);
			next.setVersion(resolveNextVersion(model.getModelKey()));
			next.setModelStatus(FlowableConstants.MODEL_STATUS_UPDATED);
			this.save(next);
			saveNodeActions(next, dto.getNodeActions());
			return next.getId();
		}

		applyModelContent(model, dto);
		Integer status = model.getModelStatus();
		if (status == null || status == FlowableConstants.MODEL_STATUS_DRAFT) {
			model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
		}
		else {
			// 已更新 / 已回滚 后续编辑仍在同一条工作副本上
			model.setModelStatus(FlowableConstants.MODEL_STATUS_UPDATED);
		}
		this.updateById(model);

		saveNodeActions(model, dto.getNodeActions());
		return model.getId();
	}

	private void applyModelContent(WfModel model, FlowableModelSaveDTO dto) {
		model.setModelKey(dto.getModelKey() != null ? dto.getModelKey() : model.getModelKey());
		model.setModelName(dto.getModelName());
		model.setCategory(dto.getCategory());
		model.setRemark(dto.getRemark());
		model.setFormJson(dto.getFormJson());
		model.setBpmnXml(ensureExecutableXml(dto.getBpmnXml()));
		model.setEndTime(dto.getEndTime());
		model.setNodeActionsJson(toJson(dto.getNodeActions()));
	}

	/**
	 * 查找同 Key 下唯一工作副本（草稿/已更新未发布/已回滚未发布，非归档、非已发布）。
	 */
	private WfModel findWorkingCopy(String modelKey) {
		if (modelKey == null || modelKey.isBlank()) {
			return null;
		}
		List<WfModel> list = this.list(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, modelKey)
			.in(WfModel::getModelStatus, FlowableConstants.MODEL_STATUS_DRAFT, FlowableConstants.MODEL_STATUS_UPDATED,
					FlowableConstants.MODEL_STATUS_ROLLED_BACK)
			.orderByDesc(WfModel::getVersion)
			.last("LIMIT 1"));
		return list == null || list.isEmpty() ? null : list.get(0);
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
		// 模型列表不显示已归档（历史版本只在版本历史中查看）
		wrapper.ne(WfModel::getModelStatus, FlowableConstants.MODEL_STATUS_ARCHIVED);
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

		// 归档同 Key 下其它「已发布」版本到历史
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
	public boolean offline(Long id) {
		if (id == null) {
			throw new BusinessException("id is required");
		}
		WfModel model = this.getById(id);
		if (model == null) {
			throw new BusinessException("model not found");
		}
		if (model.getModelStatus() == null || model.getModelStatus() != FlowableConstants.MODEL_STATUS_RELEASED) {
			throw new BusinessException("仅已发布模型可关闭发布");
		}

		// 归档该模型相关部署（下线，不可再启动新实例）
		deploymentMapper.update(null,
				new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfDeployment>()
					.set(WfDeployment::getDeployStatus, FlowableConstants.DEPLOY_STATUS_ARCHIVED)
					.eq(WfDeployment::getModelId, model.getId())
					.eq(WfDeployment::getDeployStatus, FlowableConstants.DEPLOY_STATUS_ACTIVE));

		model.setModelStatus(FlowableConstants.MODEL_STATUS_CLOSED);
		return this.updateById(model);
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

		// 回滚到工作副本：有则覆盖内容，无则新建一条「已回滚未发布」
		WfModel working = findWorkingCopy(dto.getModelKey());
		if (working != null) {
			working.setModelName(model.getModelName());
			working.setCategory(model.getCategory());
			working.setRemark(model.getRemark());
			working.setFormJson(model.getFormJson());
			working.setBpmnXml(model.getBpmnXml());
			working.setNodeActionsJson(model.getNodeActionsJson());
			working.setEndTime(model.getEndTime());
			working.setModelStatus(FlowableConstants.MODEL_STATUS_ROLLED_BACK);
			this.updateById(working);
			nodeActionMapper
				.delete(new LambdaQueryWrapper<WfNodeAction>().eq(WfNodeAction::getModelId, working.getId()));
			copyNodeActions(model.getId(), working);
			return true;
		}

		WfModel newModel = new WfModel();
		newModel.setModelKey(model.getModelKey());
		newModel.setModelName(model.getModelName());
		newModel.setCategory(model.getCategory());
		newModel.setRemark(model.getRemark());
		newModel.setFormJson(model.getFormJson());
		newModel.setVersion(resolveNextVersion(model.getModelKey()));
		newModel.setModelStatus(FlowableConstants.MODEL_STATUS_ROLLED_BACK);
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
		// 仅禁止删除「当前已发布」；已关闭/草稿/未发布/归档历史可删
		if (model.getModelStatus() != null && model.getModelStatus() == FlowableConstants.MODEL_STATUS_RELEASED) {
			throw new BusinessException("当前已发布版本不可删除，请先关闭发布");
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
		// 逻辑删除的行仍占用 uk(model_key, version)，必须用含删除的 MAX(version)+1
		int max = 0;
		if (this.baseMapper != null) {
			Integer dbMax = this.baseMapper.selectMaxVersionIncludingDeleted(modelKey);
			if (dbMax != null) {
				max = dbMax;
			}
		}
		if (max <= 0) {
			List<WfModel> all = this
				.list(new LambdaQueryWrapper<WfModel>().eq(WfModel::getModelKey, modelKey).select(WfModel::getVersion));
			if (all != null) {
				max = all.stream()
					.map(WfModel::getVersion)
					.filter(java.util.Objects::nonNull)
					.mapToInt(Integer::intValue)
					.max()
					.orElse(0);
			}
		}
		int next = max + 1;
		while (this.baseMapper != null && this.baseMapper.countVersionIncludingDeleted(modelKey, next) > 0) {
			next++;
		}
		return next;
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