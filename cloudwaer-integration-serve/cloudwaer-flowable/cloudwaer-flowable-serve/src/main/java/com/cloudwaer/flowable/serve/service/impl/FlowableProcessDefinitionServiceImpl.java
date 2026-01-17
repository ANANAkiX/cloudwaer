package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.flowable.api.dto.FlowableProcessDefinitionDTO;
import com.cloudwaer.flowable.api.dto.FormFieldDTO;
import com.cloudwaer.flowable.api.dto.OptionDTO;
import com.cloudwaer.flowable.serve.entity.WfModel;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.service.FlowableProcessDefinitionService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlowableProcessDefinitionServiceImpl implements FlowableProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private WfModelMapper wfModelMapper;

    @Override
    public PageResult<FlowableProcessDefinitionDTO> getProcessDefinitions(PageDTO pageDTO, String category, String keyword) {
        try {
            // 查询已发布的流程定义（基于已发布的模型）
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                    .latestVersion()
                    .active()
                    .list();

            List<FlowableProcessDefinitionDTO> result = new ArrayList<>();

            for (ProcessDefinition pd : processDefinitions) {
                // 过滤条件
                if (category != null && !category.isEmpty() && !category.equals("all")) {
                    if (!category.equals(pd.getCategory())) {
                        continue;
                    }
                }

                if (keyword != null && !keyword.isEmpty()) {
                    if (pd.getName() == null || !pd.getName().toLowerCase().contains(keyword.toLowerCase())) {
                        if (pd.getKey() == null || !pd.getKey().toLowerCase().contains(keyword.toLowerCase())) {
                            continue;
                        }
                    }
                }

                // 检查对应的模型是否已发布
                WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                        .eq(WfModel::getModelKey, pd.getKey())
                        .eq(WfModel::getVersion, pd.getVersion())
                        .eq(WfModel::getModelStatus, 1)); // 1表示已发布

                if (wfModel == null) {
                    wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                            .eq(WfModel::getModelKey, pd.getKey())
                            .eq(WfModel::getModelStatus, 1)
                            .orderByDesc(WfModel::getVersion)
                            .last("limit 1"));
                }

                if (wfModel == null) {
                    continue; // 只显示已发布模型的流程定义
                }

                FlowableProcessDefinitionDTO dto = new FlowableProcessDefinitionDTO();
                dto.setId(pd.getId());
                dto.setProcessKey(pd.getKey());
                dto.setProcessName(pd.getName());
                dto.setCategory(pd.getCategory());
                dto.setVersion(pd.getVersion());
                String remark = wfModel.getRemark();
                dto.setDescription(remark != null && !remark.isBlank() ? remark : pd.getDescription());

                // 转换时间
                if (pd.getDeploymentId() != null) {
                    Deployment deployment = repositoryService.createDeploymentQuery()
                            .deploymentId(pd.getDeploymentId())
                            .singleResult();
                    if (deployment != null && deployment.getDeploymentTime() != null) {
                        dto.setCreateTime(LocalDateTime.ofInstant(deployment.getDeploymentTime().toInstant(), ZoneId.systemDefault()));
                    }
                }

                // 获取实例数量（简化处理）
                dto.setInstanceCount(0);
                dto.setAvgDuration("-");

                // 获取表单字段
                dto.setFormFields(getFormFields(pd.getKey()));

                result.add(dto);
            }

            // 分页处理
            int total = result.size();
            int start = (int) ((pageDTO.getCurrent() - 1) * pageDTO.getSize());
            int end = Math.toIntExact(Math.min(start + pageDTO.getSize(), total));

            List<FlowableProcessDefinitionDTO> pageData = start < total ? result.subList(start, end) : new ArrayList<>();

            return new PageResult<>(pageData, (long) total, pageDTO.getCurrent(), pageDTO.getSize());

        } catch (Exception e) {
            return new PageResult<>(new ArrayList<>(), 0L, pageDTO.getCurrent(), pageDTO.getSize());
        }
    }

    @Override
    public FlowableProcessDefinitionDTO getProcessDefinitionDetail(String id) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(id)
                    .singleResult();
            
            if (processDefinition == null) {
                return null;
            }
            
            FlowableProcessDefinitionDTO dto = new FlowableProcessDefinitionDTO();
            dto.setId(processDefinition.getId());
            dto.setProcessKey(processDefinition.getKey());
            dto.setProcessName(processDefinition.getName());
            dto.setCategory(processDefinition.getCategory());
            dto.setVersion(processDefinition.getVersion());
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, processDefinition.getKey())
                    .eq(WfModel::getVersion, processDefinition.getVersion())
                    .eq(WfModel::getModelStatus, 1));
            if (wfModel == null) {
                wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                        .eq(WfModel::getModelKey, processDefinition.getKey())
                        .eq(WfModel::getModelStatus, 1)
                        .orderByDesc(WfModel::getVersion)
                        .last("limit 1"));
            }
            String remark = wfModel != null ? wfModel.getRemark() : null;
            dto.setDescription(remark != null && !remark.isBlank() ? remark : processDefinition.getDescription());
            
            // 转换时间
            if (processDefinition.getDeploymentId() != null) {
                Deployment deployment = repositoryService.createDeploymentQuery()
                        .deploymentId(processDefinition.getDeploymentId())
                        .singleResult();
                if (deployment != null && deployment.getDeploymentTime() != null) {
                    dto.setCreateTime(LocalDateTime.ofInstant(deployment.getDeploymentTime().toInstant(), ZoneId.systemDefault()));
                }
            }
            
            // 获取表单字段
            dto.setFormFields(getFormFields(processDefinition.getKey()));
            
            return dto;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取表单字段配置
     */
    private List<FormFieldDTO> getFormFields(String processKey) {
        List<FormFieldDTO> formFields = new ArrayList<>();
        
        // 根据不同的流程类型返回不同的表单字段
        if ("leave".equals(processKey)) {
            // 请假流程表单
            FormFieldDTO leaveType = new FormFieldDTO();
            leaveType.setId("leaveType");
            leaveType.setLabel("请假类型");
            leaveType.setType("select");
            leaveType.setRequired(true);
            leaveType.setPlaceholder("请选择请假类型");
            
            List<OptionDTO> leaveOptions = new ArrayList<>();
            leaveOptions.add(new OptionDTO());
            leaveOptions.get(0).setLabel("事假");
            leaveOptions.get(0).setValue("personal");
            leaveOptions.add(new OptionDTO());
            leaveOptions.get(1).setLabel("病假");
            leaveOptions.get(1).setValue("sick");
            leaveOptions.add(new OptionDTO());
            leaveOptions.get(2).setLabel("年假");
            leaveOptions.get(2).setValue("annual");
            leaveType.setOptions(leaveOptions);
            
            FormFieldDTO startDate = new FormFieldDTO();
            startDate.setId("startDate");
            startDate.setLabel("开始日期");
            startDate.setType("date");
            startDate.setRequired(true);
            startDate.setPlaceholder("请选择开始日期");
            
            FormFieldDTO endDate = new FormFieldDTO();
            endDate.setId("endDate");
            endDate.setLabel("结束日期");
            endDate.setType("date");
            endDate.setRequired(true);
            endDate.setPlaceholder("请选择结束日期");
            
            FormFieldDTO reason = new FormFieldDTO();
            reason.setId("reason");
            reason.setLabel("请假原因");
            reason.setType("textarea");
            reason.setRequired(true);
            reason.setPlaceholder("请输入请假原因");
            
            formFields.add(leaveType);
            formFields.add(startDate);
            formFields.add(endDate);
            formFields.add(reason);
            
        } else if ("expense".equals(processKey)) {
            // 报销流程表单
            FormFieldDTO amount = new FormFieldDTO();
            amount.setId("amount");
            amount.setLabel("报销金额");
            amount.setType("number");
            amount.setRequired(true);
            amount.setPlaceholder("请输入报销金额");
            amount.setMin(0);
            amount.setPrecision(2);
            
            FormFieldDTO expenseType = new FormFieldDTO();
            expenseType.setId("expenseType");
            expenseType.setLabel("报销类型");
            expenseType.setType("select");
            expenseType.setRequired(true);
            expenseType.setPlaceholder("请选择报销类型");
            
            List<OptionDTO> expenseOptions = new ArrayList<>();
            expenseOptions.add(new OptionDTO());
            expenseOptions.get(0).setLabel("交通费");
            expenseOptions.get(0).setValue("transport");
            expenseOptions.add(new OptionDTO());
            expenseOptions.get(1).setLabel("住宿费");
            expenseOptions.get(1).setValue("accommodation");
            expenseOptions.add(new OptionDTO());
            expenseOptions.get(2).setLabel("餐饮费");
            expenseOptions.get(2).setValue("meal");
            expenseOptions.add(new OptionDTO());
            expenseOptions.get(3).setLabel("其他");
            expenseOptions.get(3).setValue("other");
            expenseType.setOptions(expenseOptions);
            
            FormFieldDTO description = new FormFieldDTO();
            description.setId("description");
            description.setLabel("报销说明");
            description.setType("textarea");
            description.setRequired(true);
            description.setPlaceholder("请输入报销说明");
            
            formFields.add(amount);
            formFields.add(expenseType);
            formFields.add(description);
        }
        
        return formFields;
    }
}
