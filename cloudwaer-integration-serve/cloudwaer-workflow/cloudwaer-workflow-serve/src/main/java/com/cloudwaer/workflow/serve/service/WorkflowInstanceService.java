package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.workflow.api.dto.ProcessStartDTO;
import com.cloudwaer.workflow.api.dto.WorkflowInstanceDTO;
import com.cloudwaer.workflow.api.dto.WorkflowHistoryDTO;

/**
 * 流程实例服务
 */
public interface WorkflowInstanceService {

	String startProcess(ProcessStartDTO dto);

	PageResult<WorkflowInstanceDTO> listStarted(PageDTO pageDTO);

	PageResult<WorkflowInstanceDTO> listAll(PageDTO pageDTO, String status, String keyword);

	WorkflowInstanceDTO getDetail(String instanceId);

	boolean suspend(String instanceId);

	boolean activate(String instanceId);

	boolean terminate(String instanceId);

	boolean restart(String instanceId);

	boolean deleteInstance(String instanceId);

	java.util.List<WorkflowHistoryDTO> getHistory(String instanceId);

}
