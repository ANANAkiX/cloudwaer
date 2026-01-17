package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
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
import java.util.Objects;

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
        if (dto.getModelKey() == null || dto.getModelKey().isBlank()) {
            throw new BusinessException("modelKey is required");
        }
        if (dto.getModelName() == null || dto.getModelName().isBlank()) {
            throw new BusinessException("modelName is required");
        }
        if (dto.getBpmnXml() == null || dto.getBpmnXml().isBlank()) {
            throw new BusinessException("bpmnXml is required");
        }

        WfModel model;
        boolean createNewVersion = false;
        if (dto.getId() == null) {
            long count = this.count(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, dto.getModelKey())
                    .eq(WfModel::getModelStatus, 1));
            if (count > 0) {
                throw new BusinessException("modelKey already exists");
            }
            model = new WfModel();
            model.setModelKey(dto.getModelKey());
            model.setModelName(dto.getModelName());
            model.setCategory(dto.getCategory());
            model.setRemark(dto.getRemark());
            model.setVersion(1);
            model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
            model.setBpmnXml(dto.getBpmnXml());
            model.setNodeActionsJson(toJson(dto.getNodeActions()));
            this.save(model);
        } else {
            model = this.getById(dto.getId());
            if (model == null) {
                throw new BusinessException("model not found");
            }
            if (Objects.equals(model.getModelStatus(), FlowableConstants.MODEL_STATUS_DRAFT)) {
                model.setModelName(dto.getModelName());
                model.setCategory(dto.getCategory());
                model.setRemark(dto.getRemark());
                model.setBpmnXml(dto.getBpmnXml());
                model.setNodeActionsJson(toJson(dto.getNodeActions()));
                this.updateById(model);
            } else {
                createNewVersion = true;
            }
        }

        if (createNewVersion) {
            WfModel newModel = new WfModel();
            newModel.setModelKey(model.getModelKey());
            newModel.setModelName(dto.getModelName());
            newModel.setCategory(dto.getCategory());
            newModel.setRemark(dto.getRemark());
            newModel.setVersion(resolveNextVersion(model.getModelKey()));
            newModel.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
            newModel.setBpmnXml(dto.getBpmnXml());
            newModel.setNodeActionsJson(toJson(dto.getNodeActions()));
            this.save(newModel);
            model = newModel;
        }

        saveNodeActions(model, dto.getNodeActions());
        return model.getId();
    }

    @Override
    public FlowableModelDetailDTO getDetail(Long id) {
        WfModel model = this.getById(id);
        if (model == null) {
            throw new BusinessException("model not found");
        }
        FlowableModelDetailDTO detail = new FlowableModelDetailDTO();
        BeanUtils.copyProperties(model, detail);
        detail.setNodeActions(toNodeActionDtos(model.getId()));
        return detail;
    }

    @Override
    public PageResult<FlowableModelListDTO> list(PageDTO pageDTO) {
        LambdaQueryWrapper<WfModel> wrapper = new LambdaQueryWrapper<>();
        // 移除状态过滤，显示所有模型（草稿、已发布、已归档）
        // wrapper.eq(WfModel::getStatus, 1);
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(WfModel::getModelName, pageDTO.getKeyword())
                    .or().like(WfModel::getModelKey, pageDTO.getKeyword()));
        }
        wrapper.orderByDesc(WfModel::getUpdateTime).orderByDesc(WfModel::getVersion);
        List<WfModel> models = this.list(wrapper);
        List<WfModel> latestModels = new ArrayList<>();
        java.util.Set<String> seenKeys = new java.util.HashSet<>();
        for (WfModel model : models) {
            if (model.getModelKey() == null) {
                continue;
            }
            if (seenKeys.add(model.getModelKey())) {
                latestModels.add(model);
            }
        }

        long total = latestModels.size();
        int start = (int) ((pageDTO.getCurrent() - 1) * pageDTO.getSize());
        int end = Math.toIntExact(Math.min(start + pageDTO.getSize(), latestModels.size()));
        List<WfModel> pageModels = start < end ? latestModels.subList(start, end) : new ArrayList<>();
        List<FlowableModelListDTO> records = new ArrayList<>();
        for (WfModel model : pageModels) {
            FlowableModelListDTO dto = new FlowableModelListDTO();
            BeanUtils.copyProperties(model, dto);
            records.add(dto);
        }
        return new PageResult<>(records, total, pageDTO.getCurrent(), pageDTO.getSize());
    }

    @Override
    public List<FlowableModelListDTO> listVersions(String modelKey) {
        if (modelKey == null || modelKey.isBlank()) {
            throw new BusinessException("modelKey is required");
        }
        List<WfModel> models = this.list(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getModelKey, modelKey)
                .orderByDesc(WfModel::getVersion));
        List<FlowableModelListDTO> records = new ArrayList<>();
        for (WfModel model : models) {
            FlowableModelListDTO dto = new FlowableModelListDTO();
            BeanUtils.copyProperties(model, dto);
            records.add(dto);
        }
        return records;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(Long id) {
        WfModel model = this.getById(id);
        if (model == null) {
            throw new BusinessException("model not found");
        }
        String bpmnXml = ensureExecutableXml(model.getBpmnXml());
        if (bpmnXml != null && !bpmnXml.equals(model.getBpmnXml())) {
            model.setBpmnXml(bpmnXml);
            this.updateById(model);
        }
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getModelName())
                .key(model.getModelKey())
                .addString(model.getModelKey() + ".bpmn20.xml", model.getBpmnXml())
                .deploy();

        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        if (definition == null) {
            throw new BusinessException("deployment failed");
        }

        LambdaQueryWrapper<WfModel> modelWrapper = new LambdaQueryWrapper<>();
        modelWrapper.eq(WfModel::getModelKey, model.getModelKey());
        List<WfModel> models = this.list(modelWrapper);
        for (WfModel item : models) {
            if (!item.getId().equals(model.getId())
                    && Objects.equals(item.getModelStatus(), FlowableConstants.MODEL_STATUS_RELEASED)) {
                item.setModelStatus(FlowableConstants.MODEL_STATUS_ARCHIVED);
                this.updateById(item);
            }
        }
        model.setModelStatus(FlowableConstants.MODEL_STATUS_RELEASED);
        this.updateById(model);

        LambdaQueryWrapper<WfDeployment> deployWrapper = new LambdaQueryWrapper<>();
        deployWrapper.eq(WfDeployment::getModelKey, model.getModelKey());
        List<WfDeployment> deployments = deploymentMapper.selectList(deployWrapper);
        for (WfDeployment item : deployments) {
            item.setDeployStatus(FlowableConstants.DEPLOY_STATUS_ARCHIVED);
            deploymentMapper.updateById(item);
        }

        WfDeployment wfDeployment = new WfDeployment();
        wfDeployment.setModelId(model.getId());
        wfDeployment.setModelKey(model.getModelKey());
        wfDeployment.setModelVersion(model.getVersion());
        wfDeployment.setDeploymentId(deployment.getId());
        wfDeployment.setProcessDefinitionId(definition.getId());
        wfDeployment.setProcessDefinitionKey(definition.getKey());
        wfDeployment.setProcessDefinitionName(definition.getName());
        wfDeployment.setProcessDefinitionVersion(definition.getVersion());
        wfDeployment.setDeployStatus(FlowableConstants.DEPLOY_STATUS_ACTIVE);
        deploymentMapper.insert(wfDeployment);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copy(FlowableModelCopyDTO dto) {
        WfModel source = this.getById(dto.getSourceId());
        if (source == null) {
            throw new BusinessException("source model not found");
        }
        String newKey = dto.getNewModelKey();
        if (newKey == null || newKey.isBlank()) {
            newKey = source.getModelKey() + "_copy";
        }
        long count = this.count(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getModelKey, newKey)
                .eq(WfModel::getModelStatus, 1));
        if (count > 0) {
            throw new BusinessException("new modelKey already exists");
        }
        WfModel model = new WfModel();
        model.setModelKey(newKey);
        model.setModelName(dto.getNewModelName() == null || dto.getNewModelName().isBlank()
                ? source.getModelName() + " Copy" : dto.getNewModelName());
        model.setCategory(source.getCategory());
        model.setRemark(source.getRemark());
        model.setVersion(1);
        model.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
        model.setBpmnXml(source.getBpmnXml());
        model.setNodeActionsJson(source.getNodeActionsJson());
        this.save(model);

        List<WfNodeAction> actions = nodeActionMapper.selectList(new LambdaQueryWrapper<WfNodeAction>()
                .eq(WfNodeAction::getModelId, source.getId())
                .eq(WfNodeAction::getEnabled, 1));
        for (WfNodeAction action : actions) {
            WfNodeAction copy = new WfNodeAction();
            BeanUtils.copyProperties(action, copy, "id");
            copy.setModelId(model.getId());
            copy.setModelKey(model.getModelKey());
            copy.setModelVersion(model.getVersion());
            nodeActionMapper.insert(copy);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rollback(FlowableModelRollbackDTO dto) {
        if (dto.getModelKey() == null || dto.getModelKey().isBlank() || dto.getVersion() == null) {
            throw new BusinessException("modelKey and version are required");
        }
        WfModel model = this.getOne(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getModelKey, dto.getModelKey())
                .eq(WfModel::getVersion, dto.getVersion()));
        if (model == null) {
            throw new BusinessException("target model not found");
        }
        WfModel newModel = new WfModel();
        newModel.setModelKey(model.getModelKey());
        newModel.setModelName(model.getModelName());
        newModel.setCategory(model.getCategory());
        newModel.setRemark(model.getRemark());
        newModel.setVersion(resolveNextVersion(model.getModelKey()));
        newModel.setModelStatus(FlowableConstants.MODEL_STATUS_DRAFT);
        newModel.setBpmnXml(model.getBpmnXml());
        newModel.setNodeActionsJson(model.getNodeActionsJson());
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
        List<WfDeployment> deployments = deploymentMapper.selectList(new LambdaQueryWrapper<WfDeployment>()
                .eq(WfDeployment::getModelId, id));
        for (WfDeployment deployment : deployments) {
            if (deployment.getDeploymentId() != null && !deployment.getDeploymentId().isBlank()) {
                repositoryService.deleteDeployment(deployment.getDeploymentId(), true);
            }
        }
        deploymentMapper.delete(new LambdaQueryWrapper<WfDeployment>()
                .eq(WfDeployment::getModelId, id));
        nodeActionMapper.delete(new LambdaQueryWrapper<WfNodeAction>()
                .eq(WfNodeAction::getModelId, id));
        return this.removeById(id);
    }

    private void saveNodeActions(WfModel model, List<FlowableNodeActionDTO> nodeActions) {
        nodeActionMapper.delete(new LambdaQueryWrapper<WfNodeAction>()
                .eq(WfNodeAction::getModelId, model.getId()));
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
        List<WfNodeAction> actions = nodeActionMapper.selectList(new LambdaQueryWrapper<WfNodeAction>()
                .eq(WfNodeAction::getModelId, modelId)
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
        } catch (JsonProcessingException e) {
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
        WfModel latest = this.getOne(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getModelKey, modelKey)
                .orderByDesc(WfModel::getVersion)
                .last("limit 1"));
        if (latest == null || latest.getVersion() == null) {
            return 1;
        }
        return latest.getVersion() + 1;
    }

    private void copyNodeActions(Long sourceId, WfModel target) {
        List<WfNodeAction> actions = nodeActionMapper.selectList(new LambdaQueryWrapper<WfNodeAction>()
                .eq(WfNodeAction::getModelId, sourceId)
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
