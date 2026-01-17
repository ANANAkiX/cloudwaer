package com.cloudwaer.flowable.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.flowable.api.dto.FlowableTaskCompleteDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskDTO;
import com.cloudwaer.flowable.serve.entity.WfTaskExt;
import com.cloudwaer.flowable.serve.mapper.WfTaskExtMapper;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            taskService.addComment(dto.getTaskId(), null, dto.getComment());
        }
        taskService.complete(dto.getTaskId(), dto.getVariables());
        return true;
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
