package com.cloudwaer.wiki.serve.service;

import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.wiki.api.dto.WikiSpaceDTO;
import com.cloudwaer.wiki.api.dto.WikiSpaceSaveDTO;
import java.util.List;

public interface WikiSpaceService {

	PageResult<WikiSpaceDTO> page(Long current, Long size, String keyword);

	List<WikiSpaceDTO> list();

	WikiSpaceDTO detail(Long id);

	Boolean save(WikiSpaceSaveDTO dto);

	Boolean update(WikiSpaceSaveDTO dto);

	Boolean delete(Long id);

}
