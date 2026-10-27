package com.cloudwaer.wiki.serve.service;

import com.cloudwaer.wiki.api.dto.WikiDocDTO;
import com.cloudwaer.wiki.api.dto.WikiDocSaveDTO;
import java.util.List;

public interface WikiDocService {

	/** 空间下文档树 */
	List<WikiDocDTO> tree(Long spaceId);

	WikiDocDTO detail(Long id);

	Boolean save(WikiDocSaveDTO dto);

	Boolean update(WikiDocSaveDTO dto);

	Boolean delete(Long id);

	/** 调整父级/排序 */
	Boolean move(Long id, Long parentId, Integer sort);

}
