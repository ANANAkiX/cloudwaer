package com.cloudwaer.admin.serve.service;

import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;
import java.util.Map;

public interface DictService {
    /** 从DB加载所有有效字典并写入缓存 */
    void rebuildCache();

    /** 按类型获取字典（优先读缓存，缓存缺失时重建） */
    List<DictDTO> getByType(String type);

    /** 读取全部（仅内部使用/调试） */
    Map<String, List<DictDTO>> getAllFromCache();

    // ==== 管理端 CRUD ====
    PageResult<DictDTO> page(PageDTO pageDTO, String type);
    DictDTO detail(Long id);
    Boolean save(DictDTO dto);
    Boolean update(DictDTO dto);
    Boolean delete(Long id);
}
