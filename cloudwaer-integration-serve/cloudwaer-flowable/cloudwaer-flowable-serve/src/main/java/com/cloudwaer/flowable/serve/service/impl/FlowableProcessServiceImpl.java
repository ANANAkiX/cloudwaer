package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.flowable.api.dto.FlowableProcessInstanceDTO;
import com.cloudwaer.flowable.api.dto.FlowableProcessStartDTO;
import com.cloudwaer.flowable.api.enums.ProcessStatusEnum;
import com.cloudwaer.flowable.serve.entity.WfModel;
import com.cloudwaer.flowable.serve.entity.WfProcessExt;
import com.cloudwaer.flowable.serve.entity.WfTaskHandleRecord;
import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.mapper.WfProcessExtMapper;
import com.cloudwaer.flowable.serve.mapper.WfTaskHandleRecordMapper;
import com.cloudwaer.flowable.serve.service.FlowableProcessService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class FlowableProcessServiceImpl implements FlowableProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WfProcessExtMapper processExtMapper;

    @Autowired
    private WfTaskHandleRecordMapper taskHandleRecordMapper;

    @Autowired
    private WfModelMapper wfModelMapper;

    @Autowired
    private WfNodeActionMapper wfNodeActionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcess(FlowableProcessStartDTO dto) {
        String username = SecurityContextUtil.getCurrentUsername();
        try {
            identityService.setAuthenticatedUserId(username);

            log.info("=== 开始启动流程 ===");
            log.info("流程定义Key: {}", dto.getProcessDefinitionKey());
            log.info("业务Key: {}", dto.getBusinessKey());
            log.info("流程变量: {}", dto.getVariables());
            // 约定：流程申请可传入 priority（任务优先级）与 dueTime（预期结束时间）等流程变量

            // 获取流程模型信息
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, dto.getProcessDefinitionKey())
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));

            log.info("查询到的流程模型: {}", wfModel);

            if (wfModel == null) {
                throw new BusinessException("流程模型不存在: " + dto.getProcessDefinitionKey());
            }

            Map<String, Object> variables = dto.getVariables() != null
                    ? new HashMap<>(dto.getVariables())
                    : new HashMap<>();

            // 约定：流程申请可传入 priority（任务优先级）与 dueTime（预期结束时间）等流程变量
            // 若未传入 dueTime，则默认使用模型 endTime。
            java.time.LocalDateTime modelEndTime = wfModel.getEndTime();
            if (modelEndTime == null) {
                throw new BusinessException("模型结束时间未配置");
            }
            java.time.LocalDateTime dueTime = parseDueTime(variables.get("dueTime"));
            if (dueTime == null) {
                dueTime = modelEndTime;
            }
            if (dueTime.isBefore(java.time.LocalDateTime.now())) {
                throw new BusinessException("结束时间不能是过去的时间");
            }
            if (dueTime.isAfter(modelEndTime)) {
                throw new BusinessException("结束时间不能超过模型结束时间");
            }
            variables.put("dueTime", dueTime);

            Object priority = variables.get("priority");
            if (priority == null || String.valueOf(priority).isBlank()) {
                variables.put("priority", "normal");
            }

            // 用于前端展示：流程定义名称显示为模型名称
            variables.putIfAbsent("processDefinitionName", wfModel.getModelName());

            // 启动流程实例（不需要预分配所有节点处理人）
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                    dto.getProcessDefinitionKey(), dto.getBusinessKey(), variables);

            // 预留扩展口：后续可在此发送 Kafka 延迟/定时消息，按 dueTime 触发超时流程处理
            // 目前仅保留变量写入与校验，消息发送在后续接入 MQ 后实现

            log.info("流程实例启动成功: instanceId={}", instance.getId());
            log.info("流程定义ID: {}", instance.getProcessDefinitionId());
            log.info("流程定义Key: {}", instance.getProcessDefinitionKey());

            // 保存流程扩展信息
            WfProcessExt ext = new WfProcessExt();
            ext.setProcessInstanceId(instance.getId());
            ext.setProcessDefinitionKey(instance.getProcessDefinitionKey());
            ext.setBusinessKey(instance.getBusinessKey());
            ext.setCreateUser(username);
            ext.setStatus(ProcessStatusEnum.RUNNING.getCode());
            processExtMapper.insert(ext);

            log.info("流程扩展信息保存完成");
            log.info("=== 流程启动完成 ===");

            return instance.getId();
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
    }

    @Override
    public PageResult<FlowableProcessInstanceDTO> listStarted(PageDTO pageDTO) {
        String username = SecurityContextUtil.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessException("user not authenticated");
        }
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .startedBy(username)
                .orderByProcessInstanceStartTime()
                .desc();
        long total = query.count();
        int offset = Math.toIntExact(pageDTO.getOffset());
        int size = Math.toIntExact(pageDTO.getSize());
        List<HistoricProcessInstance> list = query.listPage(offset, size);
        List<FlowableProcessInstanceDTO> records = new ArrayList<>();
        for (HistoricProcessInstance instance : list) {
            FlowableProcessInstanceDTO dto = new FlowableProcessInstanceDTO();
            dto.setId(instance.getId());
            dto.setProcessDefinitionKey(instance.getProcessDefinitionKey());
            dto.setProcessDefinitionName(resolveProcessDisplayName(instance.getId(), instance.getProcessDefinitionName()));
            dto.setBusinessKey(instance.getBusinessKey());
            dto.setStartTime(toLocalDateTime(instance.getStartTime()));
            dto.setEndTime(toLocalDateTime(instance.getEndTime()));
            dto.setStarter(instance.getStartUserId());
            dto.setDueTime(resolveProcessDueTime(instance.getId()));

            // 从扩展表获取状态，如果不存在则根据endTime判断
            WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                    .eq(WfProcessExt::getProcessInstanceId, instance.getId()));
            if (ext != null && ext.getStatus() != null) {
                ProcessStatusEnum statusEnum = ProcessStatusEnum.getByCode(ext.getStatus());
                dto.setStatus(statusEnum != null ? statusEnum.getValue() : "running");
            } else {
                dto.setStatus(instance.getEndTime() == null ? "running" : "completed");
            }
            records.add(dto);
        }
        return new PageResult<>(records, total, pageDTO.getCurrent(), pageDTO.getSize());
    }

    @Override
    public FlowableProcessInstanceDTO getDetail(String processInstanceId) {
        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance == null) {
            throw new BusinessException("process instance not found");
        }
        FlowableProcessInstanceDTO dto = new FlowableProcessInstanceDTO();
        dto.setId(instance.getId());
        dto.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        dto.setProcessDefinitionName(resolveProcessDisplayName(instance.getId(), instance.getProcessDefinitionName()));
        dto.setBusinessKey(instance.getBusinessKey());
        dto.setStartTime(toLocalDateTime(instance.getStartTime()));
        dto.setEndTime(toLocalDateTime(instance.getEndTime()));
        dto.setStarter(instance.getStartUserId());
        dto.setDueTime(resolveProcessDueTime(instance.getId()));

        // 从扩展表获取状态，如果不存在则根据endTime判断
        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext != null && ext.getStatus() != null) {
            ProcessStatusEnum statusEnum = ProcessStatusEnum.getByCode(ext.getStatus());
            dto.setStatus(statusEnum != null ? statusEnum.getValue() : "running");
        } else {
            dto.setStatus(instance.getEndTime() == null ? "running" : "completed");
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProcess(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new BusinessException("processInstanceId is required");
        }
        ProcessInstance runtimeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (runtimeInstance != null) {
            runtimeService.deleteProcessInstance(processInstanceId, "deleted by user");
        }
        HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (historic != null) {
            historyService.deleteHistoricProcessInstance(processInstanceId);
        }
        processExtMapper.delete(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean suspendProcess(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new BusinessException("processInstanceId is required");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance == null) {
            throw new BusinessException("process instance not found or not running");
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);

        // 更新扩展表状态
        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext != null) {
            ext.setStatus(ProcessStatusEnum.SUSPENDED.getCode());
            processExtMapper.updateById(ext);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateProcess(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new BusinessException("processInstanceId is required");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .suspended()
                .singleResult();
        if (instance == null) {
            throw new BusinessException("process instance not found or not suspended");
        }
        runtimeService.activateProcessInstanceById(processInstanceId);

        // 更新扩展表状态
        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext != null) {
            ext.setStatus(ProcessStatusEnum.RUNNING.getCode());
            processExtMapper.updateById(ext);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restartProcess(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new BusinessException("processInstanceId is required");
        }
        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext == null || ext.getStatus() == null || !ext.getStatus().equals(ProcessStatusEnum.REJECTED.getCode())) {
            throw new BusinessException("process instance not rejected");
        }

        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance == null) {
            throw new BusinessException("process instance not found or not running");
        }

        if (instance.isSuspended()) {
            runtimeService.activateProcessInstanceById(processInstanceId);
        }

        String username = SecurityContextUtil.getCurrentUsername();
        runtimeService.setVariable(processInstanceId, "restart:" + System.currentTimeMillis(), username != null ? username : "system");

        String startEventId = resolveStartEventId(instance.getProcessDefinitionId());
        if (startEventId != null) {
            List<String> activeIds = runtimeService.getActiveActivityIds(processInstanceId);
            if (activeIds != null && !activeIds.isEmpty()) {
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(processInstanceId)
                        .moveActivityIdsToSingleActivityId(activeIds, startEventId)
                        .changeState();
            }
        }

        ext.setStatus(ProcessStatusEnum.RUNNING.getCode());
        ext.setUpdateUser(username);
        processExtMapper.updateById(ext);
        return true;
    }

    private String resolveStartEventId(String processDefinitionId) {
        if (processDefinitionId == null) {
            return null;
        }
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        if (model == null || model.getMainProcess() == null) {
            return null;
        }
        for (FlowElement element : model.getMainProcess().getFlowElements()) {
            if (element instanceof StartEvent) {
                return element.getId();
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean terminateProcess(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new BusinessException("processInstanceId is required");
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance == null) {
            throw new BusinessException("process instance not found or not running");
        }
        runtimeService.deleteProcessInstance(processInstanceId, "terminated by user");

        // 更新扩展表状态
        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext != null) {
            ext.setStatus(ProcessStatusEnum.TERMINATED.getCode());
            processExtMapper.updateById(ext);
        }
        return true;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String resolveProcessDisplayName(String processInstanceId, String defaultName) {
        HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("processDefinitionName")
                .singleResult();
        if (variable != null && variable.getValue() != null) {
            return String.valueOf(variable.getValue());
        }
        return defaultName;
    }

    private LocalDateTime resolveProcessDueTime(String processInstanceId) {
        HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("dueTime")
                .singleResult();
        if (variable == null) {
            return null;
        }
        return parseDueTime(variable.getValue());
    }

    private LocalDateTime parseDueTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.time.LocalDateTime) {
            return (java.time.LocalDateTime) value;
        }
        if (value instanceof java.util.Date) {
            return toLocalDateTime((java.util.Date) value);
        }
        if (value instanceof java.time.Instant) {
            return java.time.LocalDateTime.ofInstant((java.time.Instant) value, java.time.ZoneId.systemDefault());
        }
        if (value instanceof Long) {
            return java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli((Long) value), java.time.ZoneId.systemDefault());
        }
        String text = String.valueOf(value).trim();
        if (text.isBlank()) {
            return null;
        }
        try {
            return java.time.LocalDateTime.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getProcessDiagram(String processInstanceId) {
        try {
            String bpmnXml = getProcessBpmnXml(processInstanceId);
            if (bpmnXml == null || bpmnXml.isBlank()) {
                return generateSvgPlaceholder("未找到流程实例", processInstanceId);
            }

            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            String processDefinitionKey = historicProcessInstance != null ? historicProcessInstance.getProcessDefinitionKey() : null;

            String svgDiagram = convertBpmnXmlToSvg(bpmnXml, processDefinitionKey);
            if (svgDiagram != null && !svgDiagram.trim().isEmpty()) {
                return svgDiagram;
            }

            return generateDetailedSvgPlaceholder(
                    processDefinitionKey != null ? processDefinitionKey : "流程图",
                    processDefinitionKey,
                    processInstanceId
            );

        } catch (Exception e) {
            return generateSvgPlaceholder("获取流程图失败", processInstanceId);
        }
    }

    @Override
    public String getProcessBpmnXml(String processInstanceId) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (historicProcessInstance == null) {
                return null;
            }

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                    .singleResult();
            if (processDefinition == null) {
                return null;
            }

            String resourceName = processDefinition.getResourceName();
            if (resourceName == null || resourceName.endsWith(".png")) {
                return null;
            }

            InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
            if (inputStream == null) {
                return null;
            }

            try (inputStream) {
                return new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String generateDetailedSvgPlaceholder(String processName, String processKey, String instanceId) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg width=\"900\" height=\"700\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "  <defs>\n" +
                "    <marker id=\"arrowhead\" markerWidth=\"10\" markerHeight=\"7\" refX=\"9\" refY=\"3.5\" orient=\"auto\">\n" +
                "      <polygon points=\"0 0, 10 3.5, 0 7\" fill=\"#666\"/>\n" +
                "    </marker>\n" +
                "    <filter id=\"shadow\" x=\"-50%\" y=\"-50%\" width=\"200%\" height=\"200%\">\n" +
                "      <feGaussianBlur in=\"SourceAlpha\" stdDeviation=\"3\"/>\n" +
                "      <feOffset dx=\"2\" dy=\"2\" result=\"offsetblur\"/>\n" +
                "      <feComponentTransfer>\n" +
                "        <feFuncA type=\"linear\" slope=\"0.2\"/>\n" +
                "      </feComponentTransfer>\n" +
                "      <feMerge>\n" +
                "        <feMergeNode/>\n" +
                "        <feMergeNode in=\"SourceGraphic\"/>\n" +
                "      </feMerge>\n" +
                "    </filter>\n" +
                "  </defs>\n" +
                "  <rect width=\"900\" height=\"700\" fill=\"#f8f9fa\" stroke=\"#dee2e6\" stroke-width=\"1\"/>\n" +
                "  <rect x=\"20\" y=\"20\" width=\"860\" height=\"80\" rx=\"8\" fill=\"white\" stroke=\"#dee2e6\" stroke-width=\"1\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"450\" y=\"45\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"18\" font-weight=\"bold\" fill=\"#495057\">\n" +
                "    " + (processName != null ? processName : "流程图") + "\n" +
                "  </text>\n" +
                "  <text x=\"450\" y=\"70\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#6c757d\">\n" +
                "    Key: " + (processKey != null ? processKey : "unknown") + " | Instance ID: " + (instanceId != null ? instanceId.substring(0, 8) + "..." : "unknown") + "\n" +
                "  </text>\n" +
                "  <text x=\"450\" y=\"90\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"12\" fill=\"#adb5bd\">\n" +
                "    流程设计器预览 - 实际流程图需要BPMN模型文件\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 开始事件 -->\n" +
                "  <circle cx=\"150\" cy=\"200\" r=\"30\" fill=\"#28a745\" stroke=\"#1e7e34\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"150\" y=\"207\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                "    开始\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 任务节点 -->\n" +
                "  <rect x=\"300\" y=\"170\" width=\"120\" height=\"60\" rx=\"8\" fill=\"#007bff\" stroke=\"#0056b3\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"360\" y=\"195\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                "    用户任务\n" +
                "  </text>\n" +
                "  <text x=\"360\" y=\"215\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"12\" fill=\"white\">\n" +
                "    处理申请\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 网关 -->\n" +
                "  <path d=\"M 520 200 L 560 160 L 600 200 L 560 240 Z\" fill=\"#ffc107\" stroke=\"#d39e00\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"560\" y=\"205\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"#212529\">\n" +
                "    审批\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 结束事件 -->\n" +
                "  <circle cx=\"720\" cy=\"200\" r=\"30\" fill=\"#dc3545\" stroke=\"#c82333\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"720\" y=\"207\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                "    结束\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 连接线 -->\n" +
                "  <path d=\"M 180 200 L 300 200\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n" +
                "  <path d=\"M 420 200 L 520 200\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n" +
                "  <path d=\"M 600 200 L 690 200\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n" +
                "  \n" +
                "  <!-- 拒绝路径 -->\n" +
                "  <path d=\"M 560 240 L 560 300 L 360 300 L 360 230\" stroke=\"#dc3545\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\" stroke-dasharray=\"5,5\"/>\n" +
                "  <text x=\"460\" y=\"320\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"12\" fill=\"#dc3545\">\n" +
                "    拒绝\n" +
                "  </text>\n" +
                "  \n" +
                "  <!-- 图例 -->\n" +
                "  <rect x=\"20\" y=\"400\" width=\"860\" height=\"280\" rx=\"8\" fill=\"white\" stroke=\"#dee2e6\" stroke-width=\"1\" filter=\"url(#shadow)\"/>\n" +
                "  <text x=\"450\" y=\"425\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"16\" font-weight=\"bold\" fill=\"#495057\">\n" +
                "    流程节点说明\n" +
                "  </text>\n" +
                "  \n" +
                "  <circle cx=\"80\" cy=\"460\" r=\"15\" fill=\"#28a745\" stroke=\"#1e7e34\" stroke-width=\"2\"/>\n" +
                "  <text x=\"110\" y=\"465\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#495057\">\n" +
                "    开始事件 - 流程起点\n" +
                "  </text>\n" +
                "  \n" +
                "  <rect x=\"65\" y=\"490\" width=\"30\" height=\"20\" rx=\"4\" fill=\"#007bff\" stroke=\"#0056b3\" stroke-width=\"2\"/>\n" +
                "  <text x=\"110\" y=\"505\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#495057\">\n" +
                "    用户任务 - 需要人工处理\n" +
                "  </text>\n" +
                "  \n" +
                "  <path d=\"M 80 530 L 90 520 L 100 530 L 90 540 Z\" fill=\"#ffc107\" stroke=\"#d39e00\" stroke-width=\"2\"/>\n" +
                "  <text x=\"110\" y=\"535\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#495057\">\n" +
                "    排他网关 - 条件分支\n" +
                "  </text>\n" +
                "  \n" +
                "  <circle cx=\"80\" cy=\"570\" r=\"15\" fill=\"#dc3545\" stroke=\"#c82333\" stroke-width=\"2\"/>\n" +
                "  <text x=\"110\" y=\"575\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#495057\">\n" +
                "    结束事件 - 流程终点\n" +
                "  </text>\n" +
                "  \n" +
                "  <line x1=\"400\" y1=\"450\" x2=\"400\" y2=\"650\" stroke=\"#dee2e6\" stroke-width=\"1\"/>\n" +
                "  \n" +
                "  <text x=\"450\" y=\"470\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"#495057\">\n" +
                "    当前流程状态\n" +
                "  </text>\n" +
                "  <rect x=\"320\" y=\"490\" width=\"260\" height=\"120\" rx=\"8\" fill=\"#e9ecef\" stroke=\"#adb5bd\" stroke-width=\"1\"/>\n" +
                "  <text x=\"450\" y=\"515\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"13\" fill=\"#495057\">\n" +
                "    • 流程已启动并运行中\n" +
                "  </text>\n" +
                "  <text x=\"450\" y=\"540\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"13\" fill=\"#495057\">\n" +
                "    • 当前等待用户处理\n" +
                "  </text>\n" +
                "  <text x=\"450\" y=\"565\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"13\" fill=\"#495057\">\n" +
                "    • 可进行审批或拒绝操作\n" +
                "  </text>\n" +
                "  <text x=\"450\" y=\"590\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"13\" fill=\"#495057\">\n" +
                "    • 支持流程追踪和监控\n" +
                "  </text>\n" +
                "</svg>";
    }

    private String generateSvgPlaceholder(String processName, String processKey) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg width=\"800\" height=\"600\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "  <rect width=\"800\" height=\"600\" fill=\"#f5f5f5\" stroke=\"#ddd\" stroke-width=\"1\"/>\n" +
                "  <text x=\"400\" y=\"250\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"24\" fill=\"#666\">\n" +
                "    " + (processName != null ? processName : "流程图") + "\n" +
                "  </text>\n" +
                "  <text x=\"400\" y=\"300\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"16\" fill=\"#999\">\n" +
                "    Key: " + (processKey != null ? processKey : "unknown") + "\n" +
                "  </text>\n" +
                "  <circle cx=\"400\" cy=\"350\" r=\"30\" fill=\"#409eff\"/>\n" +
                "  <text x=\"400\" y=\"357\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"white\">\n" +
                "    开始\n" +
                "  </text>\n" +
                "  <rect x=\"350\" y=\"400\" width=\"100\" height=\"60\" rx=\"5\" fill=\"#67c23a\"/>\n" +
                "  <text x=\"400\" y=\"435\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"white\">\n" +
                "    处理中\n" +
                "  </text>\n" +
                "  <circle cx=\"400\" cy=\"500\" r=\"30\" fill=\"#e6a23c\"/>\n" +
                "  <text x=\"400\" y=\"507\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"white\">\n" +
                "    结束\n" +
                "  </text>\n" +
                "  <path d=\"M 400 380 L 400 400\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n" +
                "  <path d=\"M 400 460 L 400 470\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n" +
                "  <defs>\n" +
                "    <marker id=\"arrowhead\" markerWidth=\"10\" markerHeight=\"7\" refX=\"9\" refY=\"3.5\" orient=\"auto\">\n" +
                "      <polygon points=\"0 0, 10 3.5, 0 7\" fill=\"#666\"/>\n" +
                "    </marker>\n" +
                "  </defs>\n" +
                "</svg>";
    }

    @Override
    public List<Map<String, Object>> getProcessVariables(String processInstanceId) {
        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();

        List<Map<String, Object>> result = new ArrayList<>();
        for (HistoricVariableInstance variable : variables) {
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("name", variable.getVariableName());
            varMap.put("type", variable.getValue() != null ? variable.getValue().getClass().getSimpleName() : "null");
            varMap.put("value", variable.getValue());
            varMap.put("createTime", toLocalDateTime(variable.getCreateTime()));
            result.add(varMap);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getProcessHistory(String processInstanceId) {
        List<Map<String, Object>> result = new ArrayList<>();

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance == null) {
            return result;
        }

        String processDefinitionKey = processInstance.getProcessDefinitionKey();
        if (processInstance.getStartTime() != null) {
            result.add(buildHistoryItem(
                    processInstance.getId() + "_start",
                    "系统",
                    "创建流程",
                    processInstance.getStartTime(),
                    null,
                    "start",
                    "",
                    ""
            ));
        }

        List<WfTaskHandleRecord> handleRecords = taskHandleRecordMapper.selectList(
                new LambdaQueryWrapper<WfTaskHandleRecord>()
                        .eq(WfTaskHandleRecord::getProcessInstanceId, processInstanceId)
        );
        Map<String, WfTaskHandleRecord> handleRecordMap = new HashMap<>();
        for (WfTaskHandleRecord record : handleRecords) {
            if (record.getTaskId() == null) {
                continue;
            }
            String recordType = record.getRecordType();
            boolean isComplete = "complete".equalsIgnoreCase(recordType);
            boolean isReject = "reject".equalsIgnoreCase(recordType);
            if (!isComplete && !isReject) {
                continue;
            }
            WfTaskHandleRecord existing = handleRecordMap.get(record.getTaskId());
            if (existing == null) {
                handleRecordMap.put(record.getTaskId(), record);
                continue;
            }
            if (existing.getCreateTime() == null && record.getCreateTime() != null) {
                handleRecordMap.put(record.getTaskId(), record);
                continue;
            }
            if (existing.getCreateTime() != null && record.getCreateTime() != null
                    && record.getCreateTime().isAfter(existing.getCreateTime())) {
                handleRecordMap.put(record.getTaskId(), record);
            }
        }

        List<HistoricVariableInstance> restartVariables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableNameLike("restart:%")
                .list();
        restartVariables.sort(Comparator.comparing(HistoricVariableInstance::getCreateTime, Comparator.nullsLast(Date::compareTo)));

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();

        boolean endAdded = false;
        int taskIndex = 0;
        Date cycleStart = processInstance.getStartTime();

        for (HistoricVariableInstance restartVar : restartVariables) {
            Date boundary = restartVar.getCreateTime();

            while (taskIndex < historicTasks.size()) {
                HistoricTaskInstance task = historicTasks.get(taskIndex);
                Date taskStartTime = task.getStartTime();
                if (taskStartTime == null) {
                    taskIndex++;
                    continue;
                }
                if (cycleStart != null && taskStartTime.before(cycleStart)) {
                    taskIndex++;
                    continue;
                }
                if (boundary != null && !taskStartTime.before(boundary)) {
                    break;
                }

                appendTaskHistoryItems(result, task, processDefinitionKey, handleRecordMap);
                if (!endAdded && isTaskRejected(processInstanceId, task, handleRecordMap)) {
                    endAdded = true;
                }
                taskIndex++;
            }

            String restartUser = restartVar.getValue() != null ? String.valueOf(restartVar.getValue()) : "system";
            Date restartTime = restartVar.getCreateTime();
            result.add(buildHistoryItem(
                    processInstanceId + "_restart_" + restartVar.getId(),
                    restartUser,
                    "重新发起",
                    restartTime,
                    restartTime,
                    "restart",
                    "",
                    ""
            ));

            cycleStart = boundary;
        }

        while (taskIndex < historicTasks.size()) {
            HistoricTaskInstance task = historicTasks.get(taskIndex);
            Date taskStartTime = task.getStartTime();
            if (taskStartTime == null) {
                taskIndex++;
                continue;
            }
            if (cycleStart != null && taskStartTime.before(cycleStart)) {
                taskIndex++;
                continue;
            }

            appendTaskHistoryItems(result, task, processDefinitionKey, handleRecordMap);
            if (!endAdded && isTaskRejected(processInstanceId, task, handleRecordMap)) {
                endAdded = true;
            }
            taskIndex++;
        }

        if (processInstance.getEndTime() != null) {
            if (!endAdded) {
                result.add(buildHistoryItem(
                        processInstance.getId() + "_end",
                        "系统",
                        "结束流程",
                        processInstance.getEndTime(),
                        processInstance.getEndTime(),
                        "end",
                        "",
                        ""
                ));
            }

            result.add(buildHistoryItem(
                    processInstance.getId() + "_completed",
                    "系统",
                    "流程已完成",
                    processInstance.getEndTime(),
                    processInstance.getEndTime(),
                    "end",
                    "",
                    ""
            ));
        }

        return result;
    }

    @Override
    public Map<String, Object> getProcessHighlight(String processInstanceId) {
        Map<String, Object> result = new HashMap<>();

        List<String> activeIds = new ArrayList<>();
        try {
            activeIds = runtimeService.getActiveActivityIds(processInstanceId);
        } catch (Exception ignored) {
        }

        Set<String> completedActivityIds = new LinkedHashSet<>();
        Set<String> completedFlowIds = new LinkedHashSet<>();
        try {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();

            for (HistoricActivityInstance a : activities) {
                if (a.getActivityId() != null) {
                    completedActivityIds.add(a.getActivityId());
                }
                if ("sequenceFlow".equals(a.getActivityType()) && a.getActivityId() != null) {
                    completedFlowIds.add(a.getActivityId());
                }
            }
        } catch (Exception ignored) {
        }

        result.put("activeActivityIds", activeIds);
        result.put("completedActivityIds", new ArrayList<>(completedActivityIds));
        result.put("completedFlowIds", new ArrayList<>(completedFlowIds));
        return result;
    }

    private Map<String, Object> buildHistoryItem(String id, String userName, String action, Date time, Date endTime,
                                                 String type, String comment, String duration) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("userName", userName);
        item.put("action", action);
        item.put("time", toLocalDateTime(time));
        item.put("endTime", toLocalDateTime(endTime));
        item.put("duration", duration != null ? duration : "");
        item.put("comment", comment != null ? comment : "");
        item.put("type", type);
        return item;
    }

    private String resolveApprovalResult(String processInstanceId, Date taskEndTime) {
        if (taskEndTime == null) {
            return null;
        }
        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("approvalResult")
                .list();
        if (variables == null || variables.isEmpty()) {
            return null;
        }
        HistoricVariableInstance candidate = null;
        for (HistoricVariableInstance variable : variables) {
            if (variable.getCreateTime() == null) {
                continue;
            }
            if (variable.getCreateTime().after(taskEndTime)) {
                continue;
            }
            if (candidate == null || variable.getCreateTime().after(candidate.getCreateTime())) {
                candidate = variable;
            }
        }
        return candidate != null && candidate.getValue() != null ? String.valueOf(candidate.getValue()) : null;
    }

    private String resolveAssigneeFromNodeConfig(String processDefinitionKey, String taskDefinitionKey) {
        if (processDefinitionKey == null || taskDefinitionKey == null) {
            return null;
        }
        WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                .eq(WfModel::getModelKey, processDefinitionKey)
                .orderByDesc(WfModel::getVersion)
                .last("LIMIT 1"));
        if (wfModel == null) {
            return null;
        }
        WfNodeAction nodeAction = wfNodeActionMapper.selectOne(
                new LambdaQueryWrapper<WfNodeAction>()
                        .eq(WfNodeAction::getModelKey, processDefinitionKey)
                        .eq(WfNodeAction::getModelVersion, wfModel.getVersion())
                        .eq(WfNodeAction::getNodeId, taskDefinitionKey)
                        .in(WfNodeAction::getActionType, "assign", "user_task", "task")
                        .eq(WfNodeAction::getEnabled, 1)
                        .last("LIMIT 1")
        );
        if (nodeAction == null || nodeAction.getActionConfig() == null) {
            return null;
        }
        return parseAssignee(nodeAction.getActionConfig());
    }

    private String parseAssignee(String actionConfig) {
        try {
            JsonNode root = objectMapper.readTree(actionConfig);
            if (root != null && root.isObject()) {
                JsonNode assigneeNode = root.get("assignee");
                if (assigneeNode != null && !assigneeNode.isNull()) {
                    String text = assigneeNode.asText();
                    return text != null && !text.isBlank() ? text : null;
                }
            }
        } catch (Exception ignored) {
        }
        return actionConfig != null && !actionConfig.isBlank() ? actionConfig : null;
    }

    private String getLatestTaskComment(String taskId) {
        try {
            List<Comment> comments = taskService.getTaskComments(taskId);
            if (comments == null || comments.isEmpty()) {
                return null;
            }
            Comment last = comments.get(comments.size() - 1);
            if (last.getFullMessage() != null && !last.getFullMessage().isBlank()) {
                return last.getFullMessage();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String formatApprovalResult(String result) {
        if (result == null || result.isBlank()) {
            return "未填写";
        }
        switch (result) {
            case "approve":
            case "approved":
                return "同意";
            case "reject":
            case "rejected":
                return "拒绝";
            default:
                return result;
        }
    }

    private String resolveResultType(String result) {
        if (result == null) {
            return "complete";
        }
        switch (result) {
            case "approve":
            case "approved":
                return "approve";
            case "reject":
            case "rejected":
                return "reject";
            default:
                return "complete";
        }
    }

    /**
     * 将BPMN XML转换为SVG流程图
     */
    private String convertBpmnXmlToSvg(String bpmnXml, String processKey) {
        try {
            StringBuilder svg = new StringBuilder();
            svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            svg.append("<svg width=\"900\" height=\"700\" xmlns=\"http://www.w3.org/2000/svg\">\n");

            // 添加样式和定义
            svg.append("  <defs>\n");
            svg.append("    <marker id=\"arrowhead\" markerWidth=\"10\" markerHeight=\"7\" refX=\"9\" refY=\"3.5\" orient=\"auto\">\n");
            svg.append("      <polygon points=\"0 0, 10 3.5, 0 7\" fill=\"#666\"/>\n");
            svg.append("    </marker>\n");
            svg.append("    <filter id=\"shadow\" x=\"-50%\" y=\"-50%\" width=\"200%\" height=\"200%\">\n");
            svg.append("      <feGaussianBlur in=\"SourceAlpha\" stdDeviation=\"3\"/>\n");
            svg.append("      <feOffset dx=\"2\" dy=\"2\" result=\"offsetblur\"/>\n");
            svg.append("      <feComponentTransfer>\n");
            svg.append("        <feFuncA type=\"linear\" slope=\"0.2\"/>\n");
            svg.append("      </feComponentTransfer>\n");
            svg.append("      <feMerge>\n");
            svg.append("        <feMergeNode/>\n");
            svg.append("        <feMergeNode in=\"SourceGraphic\"/>\n");
            svg.append("      </feMerge>\n");
            svg.append("    </filter>\n");
            svg.append("  </defs>\n");

            // 背景
            svg.append("  <rect width=\"900\" height=\"700\" fill=\"#f8f9fa\" stroke=\"#dee2e6\" stroke-width=\"1\"/>\n");

            // 标题区域
            svg.append("  <rect x=\"20\" y=\"20\" width=\"860\" height=\"80\" rx=\"8\" fill=\"white\" stroke=\"#dee2e6\" stroke-width=\"1\" filter=\"url(#shadow)\"/>\n");
            svg.append("  <text x=\"450\" y=\"45\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"18\" font-weight=\"bold\" fill=\"#495057\">\n");
            svg.append("    ").append(processKey != null ? processKey : "流程图").append("\n");
            svg.append("  </text>\n");
            svg.append("  <text x=\"450\" y=\"70\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" fill=\"#6c757d\">\n");
            svg.append("    BPMN 流程图 - 自动生成\n");
            svg.append("  </text>\n");

            // 解析BPMN XML中的元素
            List<String> startEvents = extractBpmnElements(bpmnXml, "startEvent");
            List<String> userTasks = extractBpmnElements(bpmnXml, "userTask");
            List<String> serviceTasks = extractBpmnElements(bpmnXml, "serviceTask");
            List<String> gateways = extractBpmnElements(bpmnXml, "exclusiveGateway");
            List<String> endEvents = extractBpmnElements(bpmnXml, "endEvent");

            // 生成流程图布局
            int yOffset = 150;

            // 开始事件
            if (!startEvents.isEmpty()) {
                svg.append(generateStartEventSvg(150, yOffset, startEvents.get(0)));
            }

            // 用户任务
            for (int i = 0; i < userTasks.size() && i < 3; i++) {
                int xPos = 300 + (i * 150);
                svg.append(generateUserTaskSvg(xPos, yOffset, userTasks.get(i), i));
            }

            // 结束事件
            if (!endEvents.isEmpty()) {
                int xPos = 300 + (userTasks.size() * 150);
                svg.append(generateEndEventSvg(xPos, yOffset, endEvents.get(0)));
            }

            // 添加连接线
            svg.append(generateConnectionSvg(userTasks.size()));

            svg.append("</svg>");

            return svg.toString();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从BPMN XML中提取特定类型的元素
     */
    private List<String> extractBpmnElements(String bpmnXml, String elementType) {
        List<String> elements = new ArrayList<>();

        // 支持带命名空间和不带命名空间的格式
        String pattern1 = "<bpmn:" + elementType + "\\s+[^>]*id=\"([^\"]+)\"[^>]*>";
        String pattern2 = "<" + elementType + "\\s+[^>]*id=\"([^\"]+)\"[^>]*>";

        java.util.regex.Pattern p1 = java.util.regex.Pattern.compile(pattern1);
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile(pattern2);

        java.util.regex.Matcher m1 = p1.matcher(bpmnXml);
        while (m1.find()) {
            elements.add(m1.group(1));
        }

        java.util.regex.Matcher m2 = p2.matcher(bpmnXml);
        while (m2.find()) {
            elements.add(m2.group(1));
        }

        return elements;
    }

    private String generateStartEventSvg(int x, int y, String id) {
        return String.format(
                "  <circle cx=\"%d\" cy=\"%d\" r=\"30\" fill=\"#28a745\" stroke=\"#1e7e34\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                        "    开始\n" +
                        "  </text>\n",
                x, y, x, y + 5
        );
    }

    private String generateUserTaskSvg(int x, int y, String id, int index) {
        String taskName = getUserTaskName(id, index);
        return String.format(
                "  <rect x=\"%d\" y=\"%d\" width=\"120\" height=\"60\" rx=\"8\" fill=\"#007bff\" stroke=\"#0056b3\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                        "    用户任务\n" +
                        "  </text>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"12\" fill=\"white\">\n" +
                        "    %s\n" +
                        "  </text>\n",
                x, y - 30, x + 60, y - 5, x + 60, y + 15, taskName
        );
    }

    private String generateEndEventSvg(int x, int y, String id) {
        return String.format(
                "  <circle cx=\"%d\" cy=\"%d\" r=\"30\" fill=\"#dc3545\" stroke=\"#c82333\" stroke-width=\"2\" filter=\"url(#shadow)\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"Arial, sans-serif\" font-size=\"14\" font-weight=\"bold\" fill=\"white\">\n" +
                        "    结束\n" +
                        "  </text>\n",
                x, y, x, y + 5
        );
    }

    private String generateConnectionSvg(int taskCount) {
        StringBuilder connections = new StringBuilder();
        connections.append("  <!-- 连接线 -->\n");

        // 从开始到第一个任务
        connections.append("  <path d=\"M 180 150 L 300 150\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n");

        // 任务之间的连接
        for (int i = 0; i < taskCount - 1; i++) {
            int x1 = 300 + (i * 150);
            int x2 = 300 + ((i + 1) * 150);
            connections.append(String.format(
                    "  <path d=\"M %d 150 L %d 150\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n",
                    x1 + 120, x2
            ));
        }

        // 从最后一个任务到结束
        if (taskCount > 0) {
            int lastTaskX = 300 + ((taskCount - 1) * 150);
            connections.append(String.format(
                    "  <path d=\"M %d 150 L %d 150\" stroke=\"#666\" stroke-width=\"2\" fill=\"none\" marker-end=\"url(#arrowhead)\"/>\n",
                    lastTaskX + 120, lastTaskX + 150
            ));
        }

        return connections.toString();
    }

    private String getUserTaskName(String id, int index) {
        String[] names = {"提交申请", "审核审批", "最终确认", "数据处理", "发送通知"};
        return index < names.length ? names[index] : "任务" + (index + 1);
    }

    private int historyOrder(String type) {
        if (type == null) {
            return 99;
        }
        switch (type) {
            case "start": return 1;
            case "restart": return 2;
            case "wait": return 3;
            case "complete": return 4;
            case "reject": return 5;
            case "end": return 6;
            default: return 99;
        }
    }

    private void appendTaskHistoryItems(List<Map<String, Object>> result,
                                       HistoricTaskInstance task,
                                       String processDefinitionKey,
                                       Map<String, WfTaskHandleRecord> handleRecordMap) {
        String assignee = task.getAssignee();
        if (assignee == null || assignee.isBlank()) {
            assignee = resolveAssigneeFromNodeConfig(processDefinitionKey, task.getTaskDefinitionKey());
        }
        if (assignee == null || assignee.isBlank()) {
            assignee = "未知";
        }

        Date taskStartTime = task.getStartTime();
        if (taskStartTime != null) {
            result.add(buildHistoryItem(
                    task.getId() + "_wait",
                    "系统",
                    "等待 " + assignee + " 处理",
                    taskStartTime,
                    null,
                    "wait",
                    "",
                    ""
            ));
        }

        WfTaskHandleRecord record = handleRecordMap.get(task.getId());
        Date taskEndTime = task.getEndTime();
        if (taskEndTime == null && record != null && record.getCreateTime() != null) {
            taskEndTime = Date.from(record.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (taskEndTime == null) {
            return;
        }

        String approvalResult = record != null ? record.getResult() : resolveApprovalResult(task.getProcessInstanceId(), taskEndTime);
        String resultText = formatApprovalResult(approvalResult);
        String comment = record != null ? record.getComment() : getLatestTaskComment(task.getId());
        String action = "已处理 处理结果: " + resultText;
        String commentText = comment == null || comment.isBlank() ? "" : "处理意见: " + comment;
        String type = resolveResultType(approvalResult);
        String durationText = "";
        if (record != null && record.getDurationMs() != null) {
            durationText = record.getDurationMs() + "ms";
        } else if (task.getDurationInMillis() != null) {
            durationText = task.getDurationInMillis() + "ms";
        }

        result.add(buildHistoryItem(
                task.getId() + "_complete",
                assignee,
                action,
                taskEndTime,
                taskEndTime,
                type,
                commentText,
                durationText
        ));

        if ("reject".equalsIgnoreCase(String.valueOf(approvalResult))) {
            result.add(buildHistoryItem(
                    task.getId() + "_reject_end",
                    "系统",
                    "结束流程",
                    taskEndTime,
                    taskEndTime,
                    "end",
                    "",
                    ""
            ));
        }
    }

    private boolean isTaskRejected(String processInstanceId,
                                  HistoricTaskInstance task,
                                  Map<String, WfTaskHandleRecord> handleRecordMap) {
        WfTaskHandleRecord record = handleRecordMap.get(task.getId());
        String approvalResult = record != null ? record.getResult() : null;
        if (approvalResult == null) {
            Date taskEndTime = task.getEndTime();
            if (taskEndTime != null) {
                approvalResult = resolveApprovalResult(processInstanceId, taskEndTime);
            }
        }
        return "reject".equalsIgnoreCase(String.valueOf(approvalResult)) || "rejected".equalsIgnoreCase(String.valueOf(approvalResult));
    }
}
