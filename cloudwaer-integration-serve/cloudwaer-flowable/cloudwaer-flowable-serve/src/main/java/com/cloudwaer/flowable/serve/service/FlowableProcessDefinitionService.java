package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.flowable.api.dto.FlowableProcessDefinitionDTO;

/**
 * 流程定义服务接口
 */
public interface FlowableProcessDefinitionService {

	/**
	 * 分页查询流程定义列表
	 */
	PageResult<FlowableProcessDefinitionDTO> getProcessDefinitions(PageDTO pageDTO, String category, String keyword);

	/**
	 * 获取流程定义详情
	 */
	FlowableProcessDefinitionDTO getProcessDefinitionDetail(String id);

}
