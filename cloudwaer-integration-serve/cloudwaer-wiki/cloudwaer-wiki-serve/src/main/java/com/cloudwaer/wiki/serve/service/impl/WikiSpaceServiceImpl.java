package com.cloudwaer.wiki.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.wiki.api.dto.WikiSpaceDTO;
import com.cloudwaer.wiki.api.dto.WikiSpaceSaveDTO;
import com.cloudwaer.wiki.serve.entity.WikiDoc;
import com.cloudwaer.wiki.serve.entity.WikiSpace;
import com.cloudwaer.wiki.serve.mapper.WikiDocMapper;
import com.cloudwaer.wiki.serve.mapper.WikiSpaceMapper;
import com.cloudwaer.wiki.serve.service.WikiSpaceService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WikiSpaceServiceImpl implements WikiSpaceService {

	private final WikiSpaceMapper spaceMapper;

	private final WikiDocMapper docMapper;

	@Override
	public PageResult<WikiSpaceDTO> page(Long current, Long size, String keyword) {
		LambdaQueryWrapper<WikiSpace> qw = new LambdaQueryWrapper<WikiSpace>()
			.like(StringUtils.hasText(keyword), WikiSpace::getName, keyword)
			.orderByAsc(WikiSpace::getSort)
			.orderByDesc(WikiSpace::getUpdateTime);
		Page<WikiSpace> page = spaceMapper.selectPage(new Page<>(current, size), qw);
		List<WikiSpaceDTO> records = page.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		fillDocCounts(records);
		return new PageResult<>(records, page.getTotal(), current, size);
	}

	@Override
	public List<WikiSpaceDTO> list() {
		List<WikiSpaceDTO> list = spaceMapper
			.selectList(new LambdaQueryWrapper<WikiSpace>().orderByAsc(WikiSpace::getSort))
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
		fillDocCounts(list);
		return list;
	}

	@Override
	public WikiSpaceDTO detail(Long id) {
		WikiSpace entity = spaceMapper.selectById(id);
		if (entity == null) {
			throw new BusinessException("空间不存在");
		}
		return toDto(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean save(WikiSpaceSaveDTO dto) {
		checkSlug(dto.getSlug(), null);
		WikiSpace entity = new WikiSpace();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getPublicRead() == null) {
			entity.setPublicRead(0);
		}
		if (entity.getSort() == null) {
			entity.setSort(0);
		}
		return spaceMapper.insert(entity) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean update(WikiSpaceSaveDTO dto) {
		if (dto.getId() == null) {
			throw new BusinessException("空间ID不能为空");
		}
		if (spaceMapper.selectById(dto.getId()) == null) {
			throw new BusinessException("空间不存在");
		}
		checkSlug(dto.getSlug(), dto.getId());
		WikiSpace entity = new WikiSpace();
		BeanUtils.copyProperties(dto, entity);
		return spaceMapper.updateById(entity) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean delete(Long id) {
		long docs = docMapper.selectCount(new LambdaQueryWrapper<WikiDoc>().eq(WikiDoc::getSpaceId, id));
		if (docs > 0) {
			throw new BusinessException("空间下仍有文档，请先清空");
		}
		return spaceMapper.deleteById(id) > 0;
	}

	private void checkSlug(String slug, Long excludeId) {
		LambdaQueryWrapper<WikiSpace> qw = new LambdaQueryWrapper<WikiSpace>().eq(WikiSpace::getSlug, slug);
		if (excludeId != null) {
			qw.ne(WikiSpace::getId, excludeId);
		}
		if (spaceMapper.selectCount(qw) > 0) {
			throw new BusinessException("别名已存在");
		}
	}

	private void fillDocCounts(List<WikiSpaceDTO> list) {
		if (list.isEmpty()) {
			return;
		}
		Map<Long, Long> counts = docMapper.selectList(new LambdaQueryWrapper<WikiDoc>().select(WikiDoc::getSpaceId))
			.stream()
			.collect(Collectors.groupingBy(WikiDoc::getSpaceId, Collectors.counting()));
		list.forEach(d -> d.setDocCount(counts.getOrDefault(d.getId(), 0L).intValue()));
	}

	private WikiSpaceDTO toDto(WikiSpace entity) {
		WikiSpaceDTO dto = new WikiSpaceDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

}
