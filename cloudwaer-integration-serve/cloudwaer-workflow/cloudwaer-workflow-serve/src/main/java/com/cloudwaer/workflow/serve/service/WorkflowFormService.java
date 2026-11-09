package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.workflow.api.dto.WorkflowFormDTO;

/**
 * 表单服务
 */
public interface WorkflowFormService {

	Long saveForm(WorkflowFormDTO dto);

	WorkflowFormDTO getDetail(Long id);

	PageResult<WorkflowFormDTO> list(PageDTO pageDTO, String category, String keyword);

	boolean deleteForm(Long id);

	WorkflowFormDTO getByKeyAndVersion(String formKey, Integer version);

}
