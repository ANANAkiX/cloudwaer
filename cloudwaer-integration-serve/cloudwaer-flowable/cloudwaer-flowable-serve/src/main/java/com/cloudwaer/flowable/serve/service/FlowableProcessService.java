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

	boolean restartProcess(String processInstanceId);

	boolean terminateProcess(String processInstanceId);

	String getProcessDiagram(String processInstanceId);

	/**
	 * 获取流程实例对应的 BPMN XML（用于前端只读渲染）
	 */
	String getProcessBpmnXml(String processInstanceId);

	List<Map<String, Object>> getProcessVariables(String processInstanceId);

	List<Map<String, Object>> getProcessHistory(String processInstanceId);

	/**
	 * 获取流程图高亮信息（已走过节点、已走过连线、当前节点）
	 */
	Map<String, Object> getProcessHighlight(String processInstanceId);

}
