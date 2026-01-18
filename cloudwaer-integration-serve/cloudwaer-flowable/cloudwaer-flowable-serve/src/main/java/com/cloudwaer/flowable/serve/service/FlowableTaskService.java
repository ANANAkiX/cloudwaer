package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.flowable.api.dto.FlowableTaskCompleteDTO;
import com.cloudwaer.flowable.api.dto.FlowableTaskDTO;

public interface FlowableTaskService {

    PageResult<FlowableTaskDTO> listTodo(PageDTO pageDTO);

    PageResult<FlowableTaskDTO> listDone(PageDTO pageDTO);

    boolean claim(String taskId);

    boolean complete(FlowableTaskCompleteDTO dto);

    FlowableTaskDTO getDetail(String taskId);

    boolean deleteTask(String taskId);
}
