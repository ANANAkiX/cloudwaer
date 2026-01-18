package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.flowable.api.dto.FlowableTaskCompleteDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskDTO;
import com.cloudwaer.flowable.api.enums.ProcessStatusEnum;
import com.cloudwaer.flowable.serve.entity.WfProcessExt;
import com.cloudwaer.flowable.serve.entity.WfTaskExt;
import com.cloudwaer.flowable.serve.entity.WfTaskHandleRecord;
import com.cloudwaer.flowable.serve.mapper.WfProcessExtMapper;
import com.cloudwaer.flowable.serve.mapper.WfTaskExtMapper;
import com.cloudwaer.flowable.serve.mapper.WfTaskHandleRecordMapper;
import com.cloudwaer.flowable.serve.service.FlowableTaskService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class FlowableTaskServiceImpl implements FlowableTaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private WfTaskExtMapper taskExtMapper;

    @Autowired
    private WfTaskHandleRecordMapper taskHandleRecordMapper;

    @Autowired
    private WfProcessExtMapper processExtMapper;

    @Override
    public PageResult<FlowableTaskDTO> listTodo(PageDTO pageDTO) {
        String username = SecurityContextUtil.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessException("user not authenticated");
        }
        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateOrAssigned(username)
                .orderByTaskCreateTime()
                .desc();
        long total = query.count();
        int offset = Math.toIntExact(pageDTO.getOffset());
        int size = Math.toIntExact(pageDTO.getSize());
        List<Task> list = query.listPage(offset, size);
        List<FlowableTaskDTO> records = new ArrayList<>();
        for (Task task : list) {
            FlowableTaskDTO dto = new FlowableTaskDTO();
            dto.setId(task.getId());
            dto.setName(task.getName());
            dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setProcessDefinitionKey(resolveProcessDefinitionKey(task.getProcessDefinitionId()));
            dto.setBusinessKey(resolveBusinessKey(task.getProcessInstanceId()));
            dto.setAssignee(task.getAssignee());
            dto.setCreateTime(toLocalDateTime(task.getCreateTime()));
            dto.setStatus("TODO");
            records.add(dto);
        }
        return new PageResult<>(records, total, pageDTO.getCurrent(), pageDTO.getSize());
    }

    @Override
    public PageResult<FlowableTaskDTO> listDone(PageDTO pageDTO) {
        String username = SecurityContextUtil.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessException("user not authenticated");
        }
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(username)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc();
        long total = query.count();
        int offset = Math.toIntExact(pageDTO.getOffset());
        int size = Math.toIntExact(pageDTO.getSize());
        List<HistoricTaskInstance> list = query.listPage(offset, size);
        List<FlowableTaskDTO> records = new ArrayList<>();
        for (HistoricTaskInstance task : list) {
            FlowableTaskDTO dto = new FlowableTaskDTO();
            dto.setId(task.getId());
            dto.setName(task.getName());
            dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setProcessDefinitionKey(resolveProcessDefinitionKey(task.getProcessDefinitionId()));
            dto.setBusinessKey(resolveBusinessKey(task.getProcessInstanceId()));
            dto.setAssignee(task.getAssignee());
            dto.setCreateTime(toLocalDateTime(task.getCreateTime()));
            dto.setEndTime(toLocalDateTime(task.getEndTime()));
            dto.setStatus("DONE");
            records.add(dto);
        }
        return new PageResult<>(records, total, pageDTO.getCurrent(), pageDTO.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean claim(String taskId) {
        String username = SecurityContextUtil.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessException("user not authenticated");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new BusinessException("task not found");
        }
        taskService.claim(taskId, username);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean complete(FlowableTaskCompleteDTO dto) {
        if (dto.getTaskId() == null || dto.getTaskId().isBlank()) {
            throw new BusinessException("taskId is required");
        }
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        if (task == null) {
            throw new BusinessException("task not found");
        }
        String username = SecurityContextUtil.getCurrentUsername();
        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            taskService.addComment(dto.getTaskId(), null, dto.getComment());
        }

        Map<String, Object> variables = dto.getVariables() != null
                ? new HashMap<>(dto.getVariables())
                : new HashMap<>();
        Object approvalResult = variables.get("approvalResult");
        if (approvalResult != null && username != null && !username.isBlank()) {
            variables.put(username + ":approvalResult", approvalResult);
        }

        saveTaskHandleRecord(task, dto, username, approvalResult);

        if (approvalResult != null && "reject".equalsIgnoreCase(String.valueOf(approvalResult))) {
            handleReject(task, variables, username);
            return true;
        }

        taskService.complete(dto.getTaskId(), variables);
        return true;
    }

    private void handleReject(Task task, Map<String, Object> variables, String username) {
        runtimeService.setVariables(task.getProcessInstanceId(), variables);
        markProcessRejected(task.getProcessInstanceId(), username);
    }

    private void markProcessRejected(String processInstanceId, String username) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            return;
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance != null && !instance.isSuspended()) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }

        WfProcessExt ext = processExtMapper.selectOne(new LambdaQueryWrapper<WfProcessExt>()
                .eq(WfProcessExt::getProcessInstanceId, processInstanceId));
        if (ext != null) {
            ext.setStatus(ProcessStatusEnum.REJECTED.getCode());
            ext.setUpdateUser(username);
            processExtMapper.updateById(ext);
        }
    }

    private String formatApprovalResult(String approvalResult) {
        if (approvalResult == null) {
            return "";
        }
        String normalized = approvalResult.trim().toLowerCase();
        if ("approve".equals(normalized) || "approved".equals(normalized)) {
            return "同意";
        }
        if ("reject".equals(normalized) || "rejected".equals(normalized)) {
            return "拒绝";
        }
        return approvalResult;
    }

    private void saveTaskHandleRecord(Task task, FlowableTaskCompleteDTO dto, String username, Object approvalResult) {
        WfTaskHandleRecord record = new WfTaskHandleRecord();
        record.setProcessInstanceId(task.getProcessInstanceId());
        record.setTaskId(task.getId());
        record.setTaskDefinitionKey(task.getTaskDefinitionKey());
        record.setTaskName(task.getName());
        record.setAssignee(task.getAssignee());
        record.setComment(dto.getComment());
        if (approvalResult != null) {
            record.setResult(String.valueOf(approvalResult));
        }
        String resultText = approvalResult != null ? String.valueOf(approvalResult) : "";
        if ("reject".equalsIgnoreCase(resultText)) {
            record.setRecordType("reject");
        } else {
            record.setRecordType("complete");
        }
        record.setAction("已处理 处理结果: " + formatApprovalResult(resultText));
        if (task.getCreateTime() != null) {
            long duration = Duration.between(task.getCreateTime().toInstant(), new Date().toInstant()).toMillis();
            record.setDurationMs(duration);
        }
        record.setCreateUser(username);
        taskHandleRecordMapper.insert(record);

        if (approvalResult != null && "reject".equalsIgnoreCase(String.valueOf(approvalResult))) {
            WfTaskHandleRecord endRecord = new WfTaskHandleRecord();
            endRecord.setProcessInstanceId(task.getProcessInstanceId());
            endRecord.setTaskId("end:" + task.getProcessInstanceId());
            endRecord.setAssignee("系统");
            endRecord.setRecordType("end");
            endRecord.setAction("结束流程");
            endRecord.setCreateUser(username);
            taskHandleRecordMapper.insert(endRecord);
        }
    }

    @Override
    public FlowableTaskDTO getDetail(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            FlowableTaskDTO dto = new FlowableTaskDTO();
            dto.setId(task.getId());
            dto.setName(task.getName());
            dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setProcessDefinitionKey(resolveProcessDefinitionKey(task.getProcessDefinitionId()));
            dto.setBusinessKey(resolveBusinessKey(task.getProcessInstanceId()));
            dto.setAssignee(task.getAssignee());
            dto.setCreateTime(toLocalDateTime(task.getCreateTime()));
            dto.setStatus("TODO");
            return dto;
        }

        HistoricTaskInstance historic = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        if (historic == null) {
            throw new BusinessException("task not found");
        }
        FlowableTaskDTO dto = new FlowableTaskDTO();
        dto.setId(historic.getId());
        dto.setName(historic.getName());
        dto.setTaskDefinitionKey(historic.getTaskDefinitionKey());
        dto.setProcessInstanceId(historic.getProcessInstanceId());
        dto.setProcessDefinitionKey(resolveProcessDefinitionKey(historic.getProcessDefinitionId()));
        dto.setBusinessKey(resolveBusinessKey(historic.getProcessInstanceId()));
        dto.setAssignee(historic.getAssignee());
        dto.setCreateTime(toLocalDateTime(historic.getCreateTime()));
        dto.setEndTime(toLocalDateTime(historic.getEndTime()));
        dto.setStatus("DONE");
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTask(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new BusinessException("taskId is required");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            taskService.deleteTask(taskId, true);
        }
        HistoricTaskInstance historic = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        if (historic != null) {
            historyService.deleteHistoricTaskInstance(taskId);
        }
        taskExtMapper.delete(new LambdaQueryWrapper<WfTaskExt>()
                .eq(WfTaskExt::getTaskId, taskId));
        return true;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String resolveProcessDefinitionKey(String processDefinitionId) {
        if (processDefinitionId == null) {
            return null;
        }
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        return definition == null ? processDefinitionId : definition.getKey();
    }

    private String resolveBusinessKey(String processInstanceId) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            return null;
        }
        ProcessInstance runtimeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (runtimeInstance != null) {
            return runtimeInstance.getBusinessKey();
        }
        HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        return historicInstance != null ? historicInstance.getBusinessKey() : null;
    }
}
