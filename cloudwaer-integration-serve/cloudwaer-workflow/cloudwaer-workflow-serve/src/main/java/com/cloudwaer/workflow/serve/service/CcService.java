package com.cloudwaer.workflow.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.workflow.api.dto.WorkflowCcRecordDTO;

/**
 * 抄送服务
 */
public interface CcService {

	PageResult<WorkflowCcRecordDTO> listMyCc(PageDTO pageDTO);

	boolean markRead(Long id);

	boolean markAllRead();

}
