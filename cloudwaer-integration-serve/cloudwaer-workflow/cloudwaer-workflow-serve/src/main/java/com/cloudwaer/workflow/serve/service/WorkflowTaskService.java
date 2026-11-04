package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.workflow.api.dto.TaskCompleteDTO;
import com.cloudwaer.workflow.api.dto.WorkflowTaskDTO;

/**
 * 用户任务服务
 */
public interface WorkflowTaskService {

	PageResult<WorkflowTaskDTO> listTodo(PageDTO pageDTO);

	PageResult<WorkflowTaskDTO> listDone(PageDTO pageDTO);

	PageResult<WorkflowTaskDTO> listByInstance(String instanceId);

	boolean claim(String taskId);

	boolean complete(TaskCompleteDTO dto);

	WorkflowTaskDTO getDetail(String taskId);

	boolean deleteTask(String taskId);

}
