package com.cloudwaer.flowable.serve.service;

import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.flowable.api.dto.FlowableModelCopyDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelDetailDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelListDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelRollbackDTO;
import com.cloudwaer.flowable.api.dto.FlowableModelSaveDTO;
import java.util.List;

public interface FlowableModelService {

	Long saveModel(FlowableModelSaveDTO dto);

	FlowableModelDetailDTO getDetail(Long id);

	PageResult<FlowableModelListDTO> list(PageDTO pageDTO);

	List<FlowableModelListDTO> listVersions(String modelKey);

	boolean publish(Long id);

	boolean copy(FlowableModelCopyDTO dto);

	boolean rollback(FlowableModelRollbackDTO dto);

	String getBpmnXml(Long id);

	boolean deleteModel(Long id);

}
