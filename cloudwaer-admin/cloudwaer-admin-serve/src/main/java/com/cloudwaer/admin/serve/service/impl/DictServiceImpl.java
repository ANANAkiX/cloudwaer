package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.admin.serve.entity.Dict;
import com.cloudwaer.admin.serve.mapper.DictMapper;
import com.cloudwaer.admin.serve.service.DictCacheService;
import com.cloudwaer.admin.serve.service.DictService;
import com.cloudwaer.common.core.constant.CommonConstants;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictCacheService dictCacheService;

    @Override
    public void rebuildCache() {
        LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>();
        qw.eq(Dict::getStatus, CommonConstants.STATUS_VALID)
          .orderByAsc(Dict::getType)
          .orderByAsc(Dict::getSort)
          .orderByAsc(Dict::getCode);
        List<Dict> list = dictMapper.selectList(qw);
        Map<String, List<DictDTO>> dictMap = toGroupedDTO(list);
        dictCacheService.cacheAll(dictMap);
    }

    @Override
    public List<DictDTO> getByType(String type) {
        List<DictDTO> fromCache = dictCacheService.getByTypeFromCache(type);
        if (fromCache != null) {
            return fromCache;
        }
        rebuildCache();
        fromCache = dictCacheService.getByTypeFromCache(type);
        return fromCache != null ? fromCache : new ArrayList<>();
    }

    @Override
    public Map<String, List<DictDTO>> getAllFromCache() {
        return dictCacheService.getAll();
    }

    private Map<String, List<DictDTO>> toGroupedDTO(List<Dict> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
                .filter(Objects::nonNull)
                .map(this::toDTO)
                .collect(Collectors.groupingBy(DictDTO::getType, Collectors.collectingAndThen(Collectors.toList(), l -> {
                    l.sort(Comparator.comparing(DictDTO::getSort, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(DictDTO::getCode, Comparator.nullsLast(String::compareTo)));
                    return l;
                })));
    }

    private DictDTO toDTO(Dict d) {
        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }

    // ===== 管理端 =====
    @Override
    public PageResult<DictDTO> page(PageDTO pageDTO, String type) {
        Page<Dict> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>();
        if (type != null && !type.trim().isEmpty()) {
            qw.eq(Dict::getType, type.trim());
        }
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().trim().isEmpty()) {
            String kw = pageDTO.getKeyword().trim();
            qw.and(w -> w.like(Dict::getLabel, kw).or().like(Dict::getCode, kw).or().like(Dict::getValue, kw));
        }
        qw.ne(Dict::getStatus, CommonConstants.STATUS_DELETE)
          .orderByAsc(Dict::getType)
          .orderByAsc(Dict::getSort)
          .orderByAsc(Dict::getCode);
        IPage<Dict> pr = dictMapper.selectPage(page, qw);
        List<DictDTO> list = pr.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
    }

    @Override
    public DictDTO detail(Long id) {
        Dict d = dictMapper.selectById(id);
        if (d == null) return null;
        return toDTO(d);
    }

    @Override
    public Boolean save(DictDTO dto) {
        Dict entity = new Dict();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getStatus() == null) {
            entity.setStatus(CommonConstants.STATUS_VALID);
        }
        int r = dictMapper.insert(entity);
        // 重建缓存
        rebuildCache();
        return r > 0;
    }

    @Override
    public Boolean update(DictDTO dto) {
        Dict entity = new Dict();
        BeanUtils.copyProperties(dto, entity);
        int r = dictMapper.updateById(entity);
        rebuildCache();
        return r > 0;
    }

    @Override
    public Boolean delete(Long id) {
        if (id == null) return false;
        // 逻辑删除由 BaseEntity @TableLogic 管控，这里直接更新状态为删除
        Dict d = dictMapper.selectById(id);
        if (d == null) return false;
        d.setStatus(CommonConstants.STATUS_DELETE);
        int r = dictMapper.updateById(d);
        rebuildCache();
        return r > 0;
    }
}
