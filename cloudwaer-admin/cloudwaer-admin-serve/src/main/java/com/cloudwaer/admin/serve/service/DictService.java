package com.cloudwaer.admin.serve.service;

import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.admin.api.dto.DictItemDTO;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;
import java.util.Map;

public interface DictService {

	/** Reload all valid dictionaries into cache. */
	void rebuildCache();

	/** List items by dictionary type (prefer cache). */
	List<DictItemDTO> getItemsByType(String type);

	/** Read all cached dict items (type -> list). */
	Map<String, List<DictItemDTO>> getAllFromCache();

	// ==== Dict header CRUD ====
	PageResult<DictDTO> page(PageDTO pageDTO, String keyword, String type);

	DictDTO detail(Long id);

	Boolean save(DictDTO dto);

	Boolean update(DictDTO dto);

	Boolean delete(Long id);

	// ==== Dict item CRUD ====
	List<DictItemDTO> listItems(Long dictId);

	Boolean saveItem(DictItemDTO dto);

	Boolean updateItem(DictItemDTO dto);

	Boolean deleteItem(Long id);

}
