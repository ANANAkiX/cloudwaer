package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.admin.api.dto.DictItemDTO;
import com.cloudwaer.admin.serve.entity.Dict;
import com.cloudwaer.admin.serve.entity.DictItem;
import com.cloudwaer.admin.serve.mapper.DictItemMapper;
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
	private DictItemMapper dictItemMapper;

	@Autowired
	private DictCacheService dictCacheService;

	@Override
	public void rebuildCache() {
		LambdaQueryWrapper<Dict> dictQw = new LambdaQueryWrapper<>();
		dictQw.eq(Dict::getStatus, CommonConstants.STATUS_VALID).orderByAsc(Dict::getSort).orderByAsc(Dict::getType);
		List<Dict> dicts = dictMapper.selectList(dictQw);
		if (dicts == null || dicts.isEmpty()) {
			dictCacheService.cacheAll(new java.util.HashMap<>());
			return;
		}

		List<Long> dictIds = dicts.stream().map(Dict::getId).filter(Objects::nonNull).collect(Collectors.toList());
		LambdaQueryWrapper<DictItem> itemQw = new LambdaQueryWrapper<>();
		itemQw.in(DictItem::getDictId, dictIds)
			.eq(DictItem::getStatus, CommonConstants.STATUS_VALID)
			.orderByAsc(DictItem::getSort)
			.orderByAsc(DictItem::getCode);
		List<DictItem> items = dictItemMapper.selectList(itemQw);

		Map<Long, List<DictItemDTO>> itemsByDictId = items.stream()
			.filter(Objects::nonNull)
			.map(this::toItemDTO)
			.collect(Collectors.groupingBy(DictItemDTO::getDictId));

		Map<String, List<DictItemDTO>> dictMap = dicts.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Dict::getType,
					d -> sortItems(itemsByDictId.getOrDefault(d.getId(), new ArrayList<>())), (a, b) -> a));
		dictCacheService.cacheAll(dictMap);
	}

	@Override
	public List<DictItemDTO> getItemsByType(String type) {
		if (type == null || type.trim().isEmpty()) {
			return new ArrayList<>();
		}
		List<DictItemDTO> fromCache = dictCacheService.getByTypeFromCache(type.trim());
		if (fromCache != null) {
			return fromCache;
		}
		rebuildCache();
		fromCache = dictCacheService.getByTypeFromCache(type.trim());
		return fromCache != null ? fromCache : new ArrayList<>();
	}

	@Override
	public Map<String, List<DictItemDTO>> getAllFromCache() {
		return dictCacheService.getAll();
	}

	@Override
	public PageResult<DictDTO> page(PageDTO pageDTO, String keyword, String type) {
		Page<Dict> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<Dict> qw = new LambdaQueryWrapper<>();
		if (type != null && !type.trim().isEmpty()) {
			qw.eq(Dict::getType, type.trim());
		}
		String kw = keyword != null ? keyword.trim() : null;
		if (kw != null && !kw.isEmpty()) {
			qw.and(w -> w.like(Dict::getType, kw).or().like(Dict::getName, kw).or().like(Dict::getDescription, kw));
		}
		qw.ne(Dict::getStatus, CommonConstants.STATUS_DELETE).orderByAsc(Dict::getSort).orderByAsc(Dict::getType);
		IPage<Dict> pr = dictMapper.selectPage(page, qw);
		List<DictDTO> list = pr.getRecords().stream().map(this::toDictDTO).collect(Collectors.toList());
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public DictDTO detail(Long id) {
		Dict dict = dictMapper.selectById(id);
		if (dict == null) {
			return null;
		}
		DictDTO dto = toDictDTO(dict);
		dto.setItems(listItems(dict.getId()));
		return dto;
	}

	@Override
	public Boolean save(DictDTO dto) {
		Dict entity = new Dict();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getStatus() == null) {
			entity.setStatus(CommonConstants.STATUS_VALID);
		}
		int r = dictMapper.insert(entity);
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
		if (id == null) {
			return false;
		}
		Dict dict = dictMapper.selectById(id);
		if (dict == null) {
			return false;
		}
		dict.setStatus(CommonConstants.STATUS_DELETE);
		dictMapper.updateById(dict);
		LambdaUpdateWrapper<DictItem> uw = new LambdaUpdateWrapper<>();
		uw.eq(DictItem::getDictId, id).set(DictItem::getStatus, CommonConstants.STATUS_DELETE);
		dictItemMapper.update(null, uw);
		rebuildCache();
		return true;
	}

	@Override
	public List<DictItemDTO> listItems(Long dictId) {
		if (dictId == null) {
			return new ArrayList<>();
		}
		LambdaQueryWrapper<DictItem> qw = new LambdaQueryWrapper<>();
		qw.eq(DictItem::getDictId, dictId)
			.ne(DictItem::getStatus, CommonConstants.STATUS_DELETE)
			.orderByAsc(DictItem::getSort)
			.orderByAsc(DictItem::getCode);
		List<DictItem> items = dictItemMapper.selectList(qw);
		return sortItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
	}

	@Override
	public Boolean saveItem(DictItemDTO dto) {
		DictItem entity = new DictItem();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getStatus() == null) {
			entity.setStatus(CommonConstants.STATUS_VALID);
		}
		int r = dictItemMapper.insert(entity);
		rebuildCache();
		return r > 0;
	}

	@Override
	public Boolean updateItem(DictItemDTO dto) {
		DictItem entity = new DictItem();
		BeanUtils.copyProperties(dto, entity);
		int r = dictItemMapper.updateById(entity);
		rebuildCache();
		return r > 0;
	}

	@Override
	public Boolean deleteItem(Long id) {
		if (id == null) {
			return false;
		}
		DictItem item = dictItemMapper.selectById(id);
		if (item == null) {
			return false;
		}
		item.setStatus(CommonConstants.STATUS_DELETE);
		int r = dictItemMapper.updateById(item);
		rebuildCache();
		return r > 0;
	}

	private DictDTO toDictDTO(Dict dict) {
		DictDTO dto = new DictDTO();
		BeanUtils.copyProperties(dict, dto);
		return dto;
	}

	private DictItemDTO toItemDTO(DictItem item) {
		DictItemDTO dto = new DictItemDTO();
		BeanUtils.copyProperties(item, dto);
		return dto;
	}

	private List<DictItemDTO> sortItems(List<DictItemDTO> items) {
		if (items == null) {
			return new ArrayList<>();
		}
		return items.stream()
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(DictItemDTO::getSort, Comparator.nullsLast(Integer::compareTo))
				.thenComparing(DictItemDTO::getCode, Comparator.nullsLast(String::compareTo)))
			.collect(Collectors.toList());
	}

}
