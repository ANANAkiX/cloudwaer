package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.flowable.api.dto.FlowableProcessInstanceDTO;
import com.cloudwaer.flowable.api.dto.FlowableProcessStartDTO;

import java.util.List;
import java.util.Map;

public interface FlowableProcessService {

    String startProcess(FlowableProcessStartDTO dto);

    PageResult<FlowableProcessInstanceDTO> listStarted(PageDTO pageDTO);

    FlowableProcessInstanceDTO getDetail(String processInstanceId);

    boolean deleteProcess(String processInstanceId);

    boolean suspendProcess(String processInstanceId);

    boolean activateProcess(String processInstanceId);

    boolean terminateProcess(String processInstanceId);

    String getProcessDiagram(String processInstanceId);

    List<Map<String, Object>> getProcessVariables(String processInstanceId);

    List<Map<String, Object>> getProcessHistory(String processInstanceId);
}
