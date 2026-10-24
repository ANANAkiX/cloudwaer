package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.blog.api.dto.CategoryDTO;
import com.cloudwaer.blog.api.dto.CategorySaveDTO;
import com.cloudwaer.blog.api.dto.TagDTO;
import com.cloudwaer.blog.api.dto.TagSaveDTO;
import com.cloudwaer.blog.serve.entity.BlogCategory;
import com.cloudwaer.blog.serve.entity.BlogTag;
import com.cloudwaer.blog.serve.mapper.BlogArticleTagMapper;
import com.cloudwaer.blog.serve.mapper.BlogCategoryMapper;
import com.cloudwaer.blog.serve.mapper.BlogTagMapper;
import com.cloudwaer.blog.serve.service.BlogTaxonomyService;
import com.cloudwaer.common.core.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 博客分类/标签服务实现类
 *
 * @author cloudwaer
 */
@Service
public class BlogTaxonomyServiceImpl implements BlogTaxonomyService {

	@Autowired
	private BlogCategoryMapper categoryMapper;

	@Autowired
	private BlogTagMapper tagMapper;

	@Autowired
	private BlogArticleTagMapper articleTagMapper;

	@Override
	public List<CategoryDTO> listCategories() {
		Map<Long, Long> countMap = toLongMap(categoryMapper.countPublishedByCategory(), "categoryId", "articleCount");
		return categoryMapper.selectList(new LambdaQueryWrapper<BlogCategory>().orderByAsc(BlogCategory::getSort))
			.stream()
			.map(c -> {
				CategoryDTO dto = new CategoryDTO();
				BeanUtils.copyProperties(c, dto);
				dto.setArticleCount(countMap.getOrDefault(c.getId(), 0L));
				return dto;
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<TagDTO> listTags() {
		Map<Long, Long> useMap = toLongMap(articleTagMapper.countPublishedUseByTag(), "tagId", "useCount");
		return tagMapper.selectList(new LambdaQueryWrapper<BlogTag>().orderByAsc(BlogTag::getName)).stream().map(t -> {
			TagDTO dto = new TagDTO();
			BeanUtils.copyProperties(t, dto);
			dto.setUseCount(useMap.getOrDefault(t.getId(), 0L));
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<CategoryDTO> adminListCategories() {
		return categoryMapper.selectList(new LambdaQueryWrapper<BlogCategory>().orderByAsc(BlogCategory::getSort))
			.stream()
			.map(c -> {
				CategoryDTO dto = new CategoryDTO();
				BeanUtils.copyProperties(c, dto);
				return dto;
			})
			.collect(Collectors.toList());
	}

	@Override
	public List<TagDTO> adminListTags() {
		return tagMapper.selectList(new LambdaQueryWrapper<BlogTag>().orderByAsc(BlogTag::getName)).stream().map(t -> {
			TagDTO dto = new TagDTO();
			BeanUtils.copyProperties(t, dto);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveCategory(CategorySaveDTO dto) {
		assertNameUnique(BlogCategory.class, dto.getName(), dto.getId());
		if (dto.getId() == null) {
			BlogCategory entity = new BlogCategory();
			BeanUtils.copyProperties(dto, entity);
			if (entity.getSort() == null) {
				entity.setSort(0);
			}
			categoryMapper.insert(entity);
			return Boolean.TRUE;
		}
		BlogCategory existing = categoryMapper.selectById(dto.getId());
		if (existing == null) {
			throw new BusinessException("分类不存在");
		}
		existing.setName(dto.getName());
		existing.setSlug(dto.getSlug());
		existing.setDescription(dto.getDescription());
		if (dto.getSort() != null) {
			existing.setSort(dto.getSort());
		}
		categoryMapper.updateById(existing);
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteCategory(Long id) {
		BlogCategory existing = categoryMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("分类不存在");
		}
		if (categoryMapper.countArticles(id) > 0) {
			throw new BusinessException("分类仍被文章引用，无法删除");
		}
		categoryMapper.deleteById(id);
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveTag(TagSaveDTO dto) {
		assertNameUnique(BlogTag.class, dto.getName(), dto.getId());
		if (dto.getId() == null) {
			BlogTag entity = new BlogTag();
			BeanUtils.copyProperties(dto, entity);
			tagMapper.insert(entity);
			return Boolean.TRUE;
		}
		BlogTag existing = tagMapper.selectById(dto.getId());
		if (existing == null) {
			throw new BusinessException("标签不存在");
		}
		existing.setName(dto.getName());
		existing.setSlug(dto.getSlug());
		tagMapper.updateById(existing);
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteTag(Long id) {
		BlogTag existing = tagMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("标签不存在");
		}
		if (tagMapper.countRelations(id) > 0) {
			throw new BusinessException("标签仍被文章引用，无法删除");
		}
		tagMapper.deleteById(id);
		return Boolean.TRUE;
	}

	private void assertNameUnique(Class<?> entityClass, String name, Long excludeId) {
		if (!StringUtils.hasText(name)) {
			return;
		}
		boolean exists;
		if (BlogCategory.class.equals(entityClass)) {
			LambdaQueryWrapper<BlogCategory> wrapper = new LambdaQueryWrapper<BlogCategory>().eq(BlogCategory::getName,
					name);
			if (excludeId != null) {
				wrapper.ne(BlogCategory::getId, excludeId);
			}
			exists = categoryMapper.exists(wrapper);
		}
		else {
			LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getName, name);
			if (excludeId != null) {
				wrapper.ne(BlogTag::getId, excludeId);
			}
			exists = tagMapper.exists(wrapper);
		}
		if (exists) {
			throw new BusinessException("名称已存在");
		}
	}

	private Map<Long, Long> toLongMap(List<Map<String, Object>> rows, String keyCol, String valCol) {
		Map<Long, Long> result = new HashMap<>();
		if (rows == null) {
			return result;
		}
		for (Map<String, Object> row : rows) {
			Object key = firstNonNull(row.get(keyCol), row.get(underscore(keyCol)));
			Object val = firstNonNull(row.get(valCol), row.get(underscore(valCol)));
			if (key == null || val == null) {
				continue;
			}
			result.put(Long.valueOf(String.valueOf(key)), Long.valueOf(String.valueOf(val)));
		}
		return result;
	}

	private String underscore(String camel) {
		return camel.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
	}

	private Object firstNonNull(Object a, Object b) {
		return a != null ? a : b;
	}

}
