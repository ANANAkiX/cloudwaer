package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.CollectionArticleSortDTO;
import com.cloudwaer.blog.api.dto.CollectionDTO;
import com.cloudwaer.blog.api.dto.CollectionSaveDTO;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import com.cloudwaer.blog.serve.entity.BlogCollection;
import com.cloudwaer.blog.serve.mapper.BlogArticleMapper;
import com.cloudwaer.blog.serve.mapper.BlogCollectionMapper;
import com.cloudwaer.blog.serve.service.BlogArticleService;
import com.cloudwaer.blog.serve.service.BlogCollectionService;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 博客合集服务实现
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogCollectionServiceImpl implements BlogCollectionService {

	/**
	 * ADMIN 角色ID（cloudwaer 种子 sys_role.id = 1）
	 */
	private static final Long ADMIN_ROLE_ID = 1L;

	@Autowired
	private BlogCollectionMapper collectionMapper;

	@Autowired
	private BlogArticleMapper articleMapper;

	@Autowired
	private BlogArticleService blogArticleService;

	@Override
	public PageResult<CollectionDTO> page(Long current, Long size, String keyword, Long authorId) {
		Page<BlogCollection> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogCollection> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogCollection::getName, keyword);
		}
		if (!isAdmin()) {
			wrapper.eq(BlogCollection::getAuthorId, currentUserId());
		}
		else if (authorId != null) {
			wrapper.eq(BlogCollection::getAuthorId, authorId);
		}
		wrapper.orderByDesc(BlogCollection::getSort).orderByDesc(BlogCollection::getCreateTime);

		IPage<BlogCollection> pr = collectionMapper.selectPage(page, wrapper);
		List<CollectionDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public PageResult<CollectionDTO> myPage(Long current, Long size, String keyword) {
		Page<BlogCollection> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogCollection> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogCollection::getName, keyword);
		}
		// 创作中心强制仅本人
		wrapper.eq(BlogCollection::getAuthorId, currentUserId());
		wrapper.orderByDesc(BlogCollection::getSort).orderByDesc(BlogCollection::getCreateTime);
		IPage<BlogCollection> pr = collectionMapper.selectPage(page, wrapper);
		List<CollectionDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public CollectionDTO detail(Long id) {
		return toDto(requireOwned(id));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean save(CollectionSaveDTO dto) {
		assertNameUnique(dto.getName(), dto.getId());
		BlogCollection entity;
		if (dto.getId() == null) {
			entity = new BlogCollection();
			Long userId = currentUserId();
			entity.setAuthorId(userId);
			entity.setAuthorName(SecurityContextUtil.getCurrentUsername());
			entity.setArticleCount(0);
		}
		else {
			entity = requireOwned(dto.getId());
		}
		entity.setName(dto.getName().trim());
		entity.setDescription(dto.getDescription());
		entity.setCoverUrl(dto.getCoverUrl());
		if (dto.getSort() != null) {
			entity.setSort(dto.getSort());
		}
		if (dto.getIsFeatured() != null) {
			entity.setIsFeatured(dto.getIsFeatured());
		}
		if (dto.getVisibility() != null) {
			entity.setVisibility(dto.getVisibility());
		}
		entity.setExtra(dto.getExtra());
		if (entity.getId() == null) {
			collectionMapper.insert(entity);
		}
		else {
			collectionMapper.updateById(entity);
		}
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean delete(Long id) {
		BlogCollection entity = requireOwned(id);
		// 删除合集：解除旗下文章绑定，不删文章
		articleMapper.unsetCollectionByCollectionId(entity.getId());
		collectionMapper.deleteById(entity.getId());
		return Boolean.TRUE;
	}

	@Override
	public List<CollectionDTO> myOptions() {
		Long userId = currentUserId();
		List<BlogCollection> list = collectionMapper
			.selectList(new LambdaQueryWrapper<BlogCollection>().eq(BlogCollection::getAuthorId, userId)
				.orderByDesc(BlogCollection::getSort)
				.orderByDesc(BlogCollection::getCreateTime));
		return list.stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean sortArticles(CollectionArticleSortDTO dto) {
		requireOwned(dto.getCollectionId());
		if (dto.getArticleIds() == null || dto.getArticleIds().isEmpty()) {
			return Boolean.TRUE;
		}
		List<Long> ids = dto.getArticleIds().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
		long owned = articleMapper
			.selectCount(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getCollectionId, dto.getCollectionId())
				.in(BlogArticle::getId, ids));
		if (owned != ids.size()) {
			throw new BusinessException("存在不属于该合集的文章");
		}
		for (int i = 0; i < ids.size(); i++) {
			BlogArticle update = new BlogArticle();
			update.setId(ids.get(i));
			update.setCollectionSort(i + 1);
			articleMapper.updateById(update);
		}
		return Boolean.TRUE;
	}

	@Override
	public List<ArticleDTO> publicArticles(Long collectionId, Long excludeArticleId) {
		if (collectionId == null) {
			return Collections.emptyList();
		}
		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogArticle::getCollectionId, collectionId)
			.eq(BlogArticle::getPublishStatus, 1)
			.ne(excludeArticleId != null, BlogArticle::getId, excludeArticleId)
			.last("ORDER BY ISNULL(collection_sort) ASC, collection_sort ASC, publish_time DESC LIMIT 50");
		List<BlogArticle> list = articleMapper.selectList(wrapper);
		List<ArticleDTO> result = list.stream().map(a -> {
			ArticleDTO dto = new ArticleDTO();
			BeanUtils.copyProperties(a, dto);
			dto.setContent(null);
			return dto;
		}).collect(Collectors.toList());
		blogArticleService.fillTags(result);
		return result;
	}

	@Override
	public PageResult<ArticleDTO> myArticles(Long collectionId, Long current, Long size) {
		if (collectionId == null) {
			throw new BusinessException("合集ID不能为空");
		}
		requireOwned(collectionId);
		Page<BlogArticle> page = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogArticle::getCollectionId, collectionId)
			.eq(BlogArticle::getAuthorId, currentUserId())
			.last("ORDER BY ISNULL(collection_sort) ASC, collection_sort ASC, create_time DESC");
		IPage<BlogArticle> pr = articleMapper.selectPage(page, wrapper);
		List<ArticleDTO> list = pr.getRecords().stream().map(a -> {
			ArticleDTO dto = new ArticleDTO();
			BeanUtils.copyProperties(a, dto);
			dto.setContent(null);
			return dto;
		}).collect(Collectors.toList());
		blogArticleService.fillTags(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean bindArticle(Long articleId, Long collectionId) {
		if (articleId == null) {
			throw new BusinessException("文章ID不能为空");
		}
		BlogArticle article = articleMapper.selectById(articleId);
		if (article == null || !Objects.equals(article.getAuthorId(), currentUserId())) {
			throw new BusinessException("文章不存在或无权操作");
		}
		Long oldId = article.getCollectionId();
		if (collectionId == null || collectionId == 0L) {
			articleMapper.unsetCollection(articleId);
			if (oldId != null) {
				collectionMapper.refreshArticleCount(oldId);
			}
			return Boolean.TRUE;
		}
		requireOwned(collectionId);
		article.setCollectionId(collectionId);
		articleMapper.updateById(article);
		if (oldId != null && !Objects.equals(oldId, collectionId)) {
			collectionMapper.refreshArticleCount(oldId);
		}
		collectionMapper.refreshArticleCount(collectionId);
		return Boolean.TRUE;
	}

	private void assertNameUnique(String name, Long id) {
		if (!StringUtils.hasText(name)) {
			throw new BusinessException("合集名称不能为空");
		}
		Long userId = currentUserId();
		if (!isAdmin()) {
			// 同一作者下名称唯一
			Long count = collectionMapper
				.selectCount(new LambdaQueryWrapper<BlogCollection>().eq(BlogCollection::getAuthorId, userId)
					.eq(BlogCollection::getName, name.trim())
					.ne(id != null, BlogCollection::getId, id));
			if (count != null && count > 0) {
				throw new BusinessException("已存在同名合集");
			}
		}
	}

	private BlogCollection requireOwned(Long id) {
		BlogCollection entity = collectionMapper.selectById(id);
		if (entity == null) {
			throw new BusinessException("合集不存在");
		}
		if (!isAdmin() && !Objects.equals(entity.getAuthorId(), currentUserId())) {
			throw new BusinessException("无权操作他人合集");
		}
		return entity;
	}

	private CollectionDTO toDto(BlogCollection entity) {
		CollectionDTO dto = new CollectionDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	private boolean isAdmin() {
		List<Long> roleIds = SecurityContextUtil.getCurrentRoleIds();
		return roleIds != null && roleIds.contains(ADMIN_ROLE_ID);
	}

	private Long currentUserId() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录");
		}
		return userId;
	}

}
