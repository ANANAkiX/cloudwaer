package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.workflow.api.dto.WorkflowDefDTO;
import com.cloudwaer.workflow.api.dto.WorkflowNodeDTO;
import com.cloudwaer.workflow.api.dto.WorkflowEdgeDTO;

import java.util.List;

/**
 * 流程定义服务
 */
public interface WorkflowDefService {

	Long saveDef(WorkflowDefDTO dto);

	WorkflowDefDTO getDetail(Long id);

	PageResult<WorkflowDefDTO> list(PageDTO pageDTO, String category, String keyword);

	List<WorkflowDefDTO> listVersions(String defKey);

	boolean publish(Long id);

	boolean offline(Long id);

	boolean deleteDef(Long id);

	WorkflowDefDTO getByKeyAndVersion(String defKey, Integer version);

}
