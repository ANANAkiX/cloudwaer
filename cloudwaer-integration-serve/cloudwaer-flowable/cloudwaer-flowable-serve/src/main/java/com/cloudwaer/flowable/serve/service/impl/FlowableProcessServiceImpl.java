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
import com.cloudwaer.flowable.serve.mapper.WfModelMapper;
import com.cloudwaer.flowable.serve.mapper.WfNodeActionMapper;
import com.cloudwaer.flowable.serve.mapper.WfProcessExtMapper;
import com.cloudwaer.flowable.serve.service.FlowableProcessService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
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
    private WfProcessExtMapper processExtMapper;

    @Autowired
    private WfModelMapper wfModelMapper;

    @Autowired
    private WfNodeActionMapper wfNodeActionMapper;

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
            
            // 获取流程模型信息
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, dto.getProcessDefinitionKey())
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));
            
            log.info("查询到的流程模型: {}", wfModel);
            
            if (wfModel == null) {
                throw new BusinessException("流程模型不存在: " + dto.getProcessDefinitionKey());
            }
            
            // 启动流程实例（不需要预分配所有节点处理人）
            ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                    dto.getProcessDefinitionKey(), dto.getBusinessKey(), dto.getVariables());
            
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

    @Override
    public String getProcessDiagram(String processInstanceId) {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (historicProcessInstance == null) {
                return generateSvgPlaceholder("未找到流程实例", processInstanceId);
            }

            String processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();

            // 从WfModel表中获取BPMN XML
            WfModel wfModel = wfModelMapper.selectOne(new LambdaQueryWrapper<WfModel>()
                    .eq(WfModel::getModelKey, processDefinitionKey)
                    .orderByDesc(WfModel::getVersion)
                    .last("LIMIT 1"));
            
            if (wfModel != null) {
                String bpmnXml = wfModel.getBpmnXml();
                
                if (bpmnXml != null && !bpmnXml.trim().isEmpty()) {
                    // 验证是否是有效的BPMN XML（支持带命名空间的格式）
                    boolean isValidBpmn = (bpmnXml.contains("<definitions") && bpmnXml.contains("<process")) ||
                                       (bpmnXml.contains("<bpmn:definitions") && bpmnXml.contains("<bpmn:process"));
                    
                    if (isValidBpmn) {
                        // 将BPMN XML转换为SVG流程图
                        String svgDiagram = convertBpmnXmlToSvg(bpmnXml, processDefinitionKey);
                        if (svgDiagram != null && !svgDiagram.trim().isEmpty()) {
                            return svgDiagram;
                        }
                    }
                }
            }

            // 如果无法获取或转换失败，尝试从Flowable部署资源中获取
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                    .singleResult();
            
            if (processDefinition != null) {
                String resourceName = processDefinition.getResourceName();
                if (resourceName != null && !resourceName.endsWith(".png")) {
                    try {
                        InputStream inputStream = repositoryService.getResourceAsStream(
                                processDefinition.getDeploymentId(), resourceName);
                        if (inputStream != null) {
                            try (inputStream) {
                                String bpmnXml = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                                
                                boolean isValidBpmn = (bpmnXml.contains("<definitions") && bpmnXml.contains("<process")) ||
                                                   (bpmnXml.contains("<bpmn:definitions") && bpmnXml.contains("<bpmn:process"));
                                
                                if (isValidBpmn) {
                                    String svgDiagram = convertBpmnXmlToSvg(bpmnXml, processDefinitionKey);
                                    if (svgDiagram != null && !svgDiagram.trim().isEmpty()) {
                                        return svgDiagram;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 忽略错误，继续使用占位符
                    }
                }
            }

            // 生成详细的SVG占位符
            return generateDetailedSvgPlaceholder(
                processDefinition != null ? processDefinition.getName() : "流程图", 
                processDefinitionKey,
                historicProcessInstance.getId()
            );
            
        } catch (Exception e) {
            return generateSvgPlaceholder("获取流程图失败", processInstanceId);
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
        // 获取历史任务
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (HistoricTaskInstance task : historicTasks) {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", task.getId());
            taskMap.put("userName", task.getAssignee() != null ? task.getAssignee() : "未分配");
            taskMap.put("action", task.getName());
            taskMap.put("time", toLocalDateTime(task.getStartTime()));
            taskMap.put("endTime", toLocalDateTime(task.getEndTime()));
            taskMap.put("duration", task.getDurationInMillis() != null ? task.getDurationInMillis() + "ms" : "");
            taskMap.put("comment", task.getDescription() != null ? task.getDescription() : "");
            result.add(taskMap);
        }
        
        // 获取历史活动实例
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        
        for (HistoricActivityInstance activity : activities) {
            Map<String, Object> activityMap = new HashMap<>();
            activityMap.put("id", activity.getId());
            activityMap.put("userName", activity.getAssignee() != null ? activity.getAssignee() : "系统");
            activityMap.put("action", activity.getActivityName());
            activityMap.put("time", toLocalDateTime(activity.getStartTime()));
            activityMap.put("endTime", toLocalDateTime(activity.getEndTime()));
            activityMap.put("duration", activity.getDurationInMillis() != null ? activity.getDurationInMillis() + "ms" : "");
            activityMap.put("comment", "");
            result.add(activityMap);
        }
        
        return result;
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
}
