package com.cloudwaer.wiki.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.wiki.api.dto.WikiDocDTO;
import com.cloudwaer.wiki.api.dto.WikiDocSaveDTO;
import com.cloudwaer.wiki.serve.entity.WikiDoc;
import com.cloudwaer.wiki.serve.entity.WikiSpace;
import com.cloudwaer.wiki.serve.mapper.WikiDocMapper;
import com.cloudwaer.wiki.serve.mapper.WikiSpaceMapper;
import com.cloudwaer.wiki.serve.service.WikiDocService;
import java.util.ArrayList;
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
public class WikiDocServiceImpl implements WikiDocService {

	private final WikiDocMapper docMapper;

	private final WikiSpaceMapper spaceMapper;

	@Override
	public List<WikiDocDTO> tree(Long spaceId) {
		List<WikiDoc> all = docMapper.selectList(new LambdaQueryWrapper<WikiDoc>().eq(WikiDoc::getSpaceId, spaceId)
			.orderByAsc(WikiDoc::getSort)
			.orderByAsc(WikiDoc::getId));
		Map<Long, WikiDocDTO> dtoMap = all.stream().collect(Collectors.toMap(WikiDoc::getId, this::toDto));
		List<WikiDocDTO> roots = new ArrayList<>();
		for (WikiDoc e : all) {
			WikiDocDTO dto = dtoMap.get(e.getId());
			Long parentId = e.getParentId() == null ? 0L : e.getParentId();
			if (parentId == 0L || !dtoMap.containsKey(parentId)) {
				roots.add(dto);
			}
			else {
				dtoMap.get(parentId).getChildren().add(dto);
			}
		}
		return roots;
	}

	@Override
	public WikiDocDTO detail(Long id) {
		WikiDoc entity = docMapper.selectById(id);
		if (entity == null) {
			throw new BusinessException("文档不存在");
		}
		return toDto(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean save(WikiDocSaveDTO dto) {
		requireSpace(dto.getSpaceId());
		WikiDoc entity = new WikiDoc();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getParentId() == null) {
			entity.setParentId(0L);
		}
		if (entity.getSort() == null) {
			entity.setSort(0);
		}
		entity.setAuthorId(SecurityContextUtil.getCurrentUserId());
		entity.setAuthorName(SecurityContextUtil.getCurrentUsername());
		if (!StringUtils.hasText(entity.getSummary()) && StringUtils.hasText(entity.getContent())) {
			String plain = entity.getContent().replaceAll("\\s+", " ").trim();
			entity.setSummary(plain.length() > 200 ? plain.substring(0, 200) : plain);
		}
		return docMapper.insert(entity) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean update(WikiDocSaveDTO dto) {
		if (dto.getId() == null) {
			throw new BusinessException("文档ID不能为空");
		}
		if (docMapper.selectById(dto.getId()) == null) {
			throw new BusinessException("文档不存在");
		}
		requireSpace(dto.getSpaceId());
		WikiDoc entity = new WikiDoc();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getParentId() == null) {
			entity.setParentId(0L);
		}
		return docMapper.updateById(entity) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean delete(Long id) {
		long children = docMapper.selectCount(new LambdaQueryWrapper<WikiDoc>().eq(WikiDoc::getParentId, id));
		if (children > 0) {
			throw new BusinessException("请先删除子文档");
		}
		return docMapper.deleteById(id) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean move(Long id, Long parentId, Integer sort) {
		WikiDoc entity = docMapper.selectById(id);
		if (entity == null) {
			throw new BusinessException("文档不存在");
		}
		if (parentId != null && parentId != 0L && docMapper.selectById(parentId) == null) {
			throw new BusinessException("目标父文档不存在");
		}
		WikiDoc upd = new WikiDoc();
		upd.setId(id);
		if (parentId != null) {
			upd.setParentId(parentId == 0L ? 0L : parentId);
		}
		if (sort != null) {
			upd.setSort(sort);
		}
		return docMapper.updateById(upd) > 0;
	}

	private void requireSpace(Long spaceId) {
		if (spaceId == null || spaceMapper.selectById(spaceId) == null) {
			throw new BusinessException("空间不存在");
		}
	}

	private WikiDocDTO toDto(WikiDoc entity) {
		WikiDocDTO dto = new WikiDocDTO();
		BeanUtils.copyProperties(entity, dto);
		if (dto.getChildren() == null) {
			dto.setChildren(new ArrayList<>());
		}
		return dto;
	}

}
