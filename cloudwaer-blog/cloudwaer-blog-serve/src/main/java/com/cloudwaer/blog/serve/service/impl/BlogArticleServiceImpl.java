package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.ArticleSaveDTO;
import com.cloudwaer.blog.api.dto.ArticleTagRefDTO;
import com.cloudwaer.blog.api.dto.CollectionDTO;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import com.cloudwaer.blog.serve.entity.BlogArticleTag;
import com.cloudwaer.blog.serve.entity.BlogCollection;
import com.cloudwaer.blog.serve.entity.BlogTag;
import com.cloudwaer.blog.serve.mapper.BlogArticleMapper;
import com.cloudwaer.blog.serve.mapper.BlogArticleTagMapper;
import com.cloudwaer.blog.serve.mapper.BlogCollectionMapper;
import com.cloudwaer.blog.serve.mapper.BlogTagMapper;
import com.cloudwaer.blog.serve.service.BlogArticleService;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 博客文章服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogArticleServiceImpl implements BlogArticleService {

	/**
	 * ADMIN 角色ID（cloudwaer 种子 sys_role.id = 1）
	 */
	private static final Long ADMIN_ROLE_ID = 1L;

	@Autowired
	private BlogArticleMapper blogArticleMapper;

	@Autowired
	private BlogArticleTagMapper articleTagMapper;

	@Autowired
	private BlogTagMapper tagMapper;

	@Autowired
	private BlogCollectionMapper collectionMapper;

	@Override
	public PageResult<ArticleDTO> page(Long current, Long size, String keyword, Integer publishStatus, Long authorId,
			Long categoryId, Long tagId) {
		Page<BlogArticle> page = new Page<>(current, size);

		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogArticle::getTitle, keyword);
		}
		if (publishStatus != null) {
			wrapper.eq(BlogArticle::getPublishStatus, publishStatus);
		}
		if (categoryId != null) {
			wrapper.eq(BlogArticle::getCategoryId, categoryId);
		}
		if (tagId != null) {
			wrapper.inSql(BlogArticle::getId,
					"SELECT article_id FROM blog_article_tag WHERE tag_id = " + tagId + " AND status = 1");
		}
		// 行级隔离：非 ADMIN 强制只看本人
		if (!isAdmin()) {
			wrapper.eq(BlogArticle::getAuthorId, currentUserId());
		}
		else if (authorId != null) {
			wrapper.eq(BlogArticle::getAuthorId, authorId);
		}
		wrapper.orderByDesc(BlogArticle::getIsTop).orderByDesc(BlogArticle::getCreateTime);

		IPage<BlogArticle> pr = blogArticleMapper.selectPage(page, wrapper);
		List<ArticleDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		// 列表不下发正文，避免 longtext 拖垮分页
		list.forEach(a -> a.setContent(null));
		fillTags(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public PageResult<ArticleDTO> myPage(Long current, Long size, String keyword, Integer publishStatus) {
		Page<BlogArticle> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogArticle::getTitle, keyword);
		}
		if (publishStatus != null) {
			wrapper.eq(BlogArticle::getPublishStatus, publishStatus);
		}
		// 创作中心强制仅本人
		wrapper.eq(BlogArticle::getAuthorId, currentUserId());
		wrapper.orderByDesc(BlogArticle::getIsTop).orderByDesc(BlogArticle::getCreateTime);

		IPage<BlogArticle> pr = blogArticleMapper.selectPage(page, wrapper);
		List<ArticleDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		list.forEach(a -> a.setContent(null));
		fillTags(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public ArticleDTO detail(Long id) {
		BlogArticle article = requireOwnedArticle(id);
		ArticleDTO dto = toDto(article);
		fillTags(Collections.singletonList(dto));
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveArticle(ArticleSaveDTO saveDTO) {
		BlogArticle article = new BlogArticle();
		BeanUtils.copyProperties(saveDTO, article);
		article.setId(null);
		Long userId = currentUserId();
		article.setAuthorId(userId);
		java.util.Map<String, Object> tokenData = SecurityContextUtil.getCurrentTokenData();
		String display = SecurityContextUtil.getCurrentUsername();
		String avatar = null;
		if (tokenData != null) {
			Object nick = tokenData.get("nickname");
			if (nick != null && StringUtils.hasText(String.valueOf(nick))) {
				display = String.valueOf(nick);
			}
			Object av = tokenData.get("avatar");
			if (av != null && StringUtils.hasText(String.valueOf(av))) {
				avatar = String.valueOf(av);
			}
		}
		article.setAuthorName(display);
		article.setAuthorAvatar(avatar);
		if (article.getPublishStatus() == null) {
			article.setPublishStatus(0);
		}
		if (article.getPublishStatus() == 1) {
			article.setPublishTime(LocalDateTime.now());
		}
		normalizeCollectionBinding(article, saveDTO);
		blogArticleMapper.insert(article);
		replaceTags(article.getId(), saveDTO.getTagIds());
		refreshCollectionCount(article.getCollectionId());
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateArticle(ArticleSaveDTO saveDTO) {
		if (saveDTO.getId() == null) {
			throw new BusinessException("文章ID不能为空");
		}
		BlogArticle existing = requireOwnedArticle(saveDTO.getId());

		existing.setTitle(saveDTO.getTitle());
		existing.setSummary(saveDTO.getSummary());
		existing.setContent(saveDTO.getContent());
		existing.setCoverUrl(saveDTO.getCoverUrl());
		existing.setCategoryId(saveDTO.getCategoryId());
		if (saveDTO.getPublishStatus() != null) {
			existing.setPublishStatus(saveDTO.getPublishStatus());
			if (saveDTO.getPublishStatus() == 1 && existing.getPublishTime() == null) {
				existing.setPublishTime(LocalDateTime.now());
			}
		}
		if (saveDTO.getIsTop() != null) {
			existing.setIsTop(saveDTO.getIsTop());
		}
		if (saveDTO.getIsRecommend() != null) {
			existing.setIsRecommend(saveDTO.getIsRecommend());
		}
		Long oldCollectionId = existing.getCollectionId();
		normalizeCollectionBinding(existing, saveDTO);

		blogArticleMapper.updateById(existing);
		if (existing.getCollectionId() == null) {
			// updateById 忽略 null 字段，需显式清空合集
			blogArticleMapper.unsetCollection(existing.getId());
		}
		replaceTags(existing.getId(), saveDTO.getTagIds());
		refreshCollectionCount(oldCollectionId);
		refreshCollectionCount(existing.getCollectionId());
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteArticle(Long id) {
		BlogArticle existing = requireOwnedArticle(id);
		blogArticleMapper.deleteById(id);
		articleTagMapper.physicalDeleteByArticleId(id);
		return Boolean.TRUE;
	}

	@Override
	public void fillTags(List<ArticleDTO> articles) {
		if (articles == null || articles.isEmpty()) {
			return;
		}
		List<Long> articleIds = articles.stream().map(ArticleDTO::getId).collect(Collectors.toList());
		List<BlogArticleTag> relations = articleTagMapper
			.selectList(new LambdaQueryWrapper<BlogArticleTag>().in(BlogArticleTag::getArticleId, articleIds));
		if (relations.isEmpty()) {
			articles.forEach(a -> a.setTags(new ArrayList<>()));
			return;
		}

		List<Long> tagIds = relations.stream().map(BlogArticleTag::getTagId).distinct().collect(Collectors.toList());
		Map<Long, String> tagNameMap = tagMapper.selectBatchIds(tagIds)
			.stream()
			.collect(Collectors.toMap(BlogTag::getId, BlogTag::getName));

		Map<Long, List<ArticleTagRefDTO>> grouped = relations.stream()
			.filter(r -> tagNameMap.containsKey(r.getTagId()))
			.collect(Collectors.groupingBy(BlogArticleTag::getArticleId, Collectors
				.mapping(r -> new ArticleTagRefDTO(r.getTagId(), tagNameMap.get(r.getTagId())), Collectors.toList())));

		articles.forEach(a -> a.setTags(grouped.getOrDefault(a.getId(), new ArrayList<>())));
	}

	@Override
	public PageResult<ArticleDTO> publicPage(Long current, Long size, String keyword, Long categoryId, Long tagId) {
		Page<BlogArticle> page = new Page<>(current, size);

		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogArticle::getPublishStatus, 1);
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogArticle::getTitle, keyword);
		}
		if (categoryId != null) {
			wrapper.eq(BlogArticle::getCategoryId, categoryId);
		}
		if (tagId != null) {
			wrapper.inSql(BlogArticle::getId,
					"SELECT article_id FROM blog_article_tag WHERE tag_id = " + tagId + " AND status = 1");
		}
		wrapper.orderByDesc(BlogArticle::getIsTop)
			.orderByDesc(BlogArticle::getHeatScore)
			.orderByDesc(BlogArticle::getCreateTime);

		IPage<BlogArticle> pr = blogArticleMapper.selectPage(page, wrapper);
		List<ArticleDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		list.forEach(a -> a.setContent(null));
		fillTags(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public ArticleDTO publicDetail(Long id) {
		BlogArticle article = blogArticleMapper.selectById(id);
		if (article == null || article.getPublishStatus() == null || article.getPublishStatus() != 1) {
			throw new BusinessException("文章不存在");
		}
		blogArticleMapper.incrementView(id);
		article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
		blogArticleMapper.refreshHeat(id);
		ArticleDTO dto = toDto(article);
		fillTags(Collections.singletonList(dto));
		fillCollection(dto, id);
		return dto;
	}

	/**
	 * 详情填充合集摘要与合集内其它文章
	 */
	private void fillCollection(ArticleDTO dto, Long articleId) {
		if (dto.getCollectionId() == null) {
			return;
		}
		BlogCollection collection = collectionMapper.selectById(dto.getCollectionId());
		if (collection == null) {
			return;
		}
		CollectionDTO c = new CollectionDTO();
		BeanUtils.copyProperties(collection, c);
		dto.setCollection(c);
		// 合集列表含当前文章，便于侧栏选中与就地切换
		dto.setCollectionArticles(loadCollectionSiblings(collection.getId(), null));
	}

	/**
	 * 合集内已发布文章（手动排序优先，否则发布时间倒序）；excludeArticleId 传 null 表示含全部
	 */
	private List<ArticleDTO> loadCollectionSiblings(Long collectionId, Long excludeArticleId) {
		LambdaQueryWrapper<BlogArticle> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogArticle::getCollectionId, collectionId)
			.eq(BlogArticle::getPublishStatus, 1)
			.ne(excludeArticleId != null, BlogArticle::getId, excludeArticleId)
			.last("ORDER BY ISNULL(collection_sort) ASC, collection_sort ASC, publish_time DESC LIMIT 50");
		List<ArticleDTO> siblings = blogArticleMapper.selectList(wrapper).stream().map(a -> {
			ArticleDTO item = toDto(a);
			item.setContent(null);
			return item;
		}).collect(Collectors.toList());
		fillTags(siblings);
		return siblings;
	}

	/**
	 * 规范化合集绑定：校验归属；0/空字符串语义由前端传 null 表示移出
	 */
	private void normalizeCollectionBinding(BlogArticle article, ArticleSaveDTO saveDTO) {
		Long collectionId = saveDTO.getCollectionId();
		if (collectionId == null || collectionId == 0L) {
			article.setCollectionId(null);
			article.setCollectionSort(null);
			return;
		}
		BlogCollection collection = collectionMapper.selectById(collectionId);
		if (collection == null) {
			throw new BusinessException("合集不存在");
		}
		if (!isAdmin() && !Objects.equals(collection.getAuthorId(), article.getAuthorId())) {
			throw new BusinessException("不能将文章放入他人合集");
		}
		article.setCollectionId(collectionId);
		article.setCollectionSort(saveDTO.getCollectionSort());
	}

	private void refreshCollectionCount(Long collectionId) {
		if (collectionId == null) {
			return;
		}
		collectionMapper.refreshArticleCount(collectionId);
	}

	@Override
	public List<ArticleDTO> archive() {
		List<BlogArticle> list = blogArticleMapper
			.selectList(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getPublishStatus, 1)
				.orderByDesc(BlogArticle::getPublishTime));
		List<ArticleDTO> result = list.stream().map(this::toDto).collect(Collectors.toList());
		result.forEach(a -> a.setContent(null));
		return result;
	}

	@Override
	public List<ArticleDTO> hot(int limit) {
		int size = Math.max(1, Math.min(limit, 20));
		Page<BlogArticle> page = new Page<>(1, size);
		IPage<BlogArticle> pr = blogArticleMapper.selectPage(page,
				new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getPublishStatus, 1)
					.orderByDesc(BlogArticle::getHeatScore)
					.orderByDesc(BlogArticle::getViewCount));
		List<ArticleDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		list.forEach(a -> a.setContent(null));
		return list;
	}

	/**
	 * 全量替换文章标签关联（物理删除后重建，避免逻辑删除残留撞唯一索引）
	 */
	private void replaceTags(Long articleId, List<Long> tagIds) {
		articleTagMapper.physicalDeleteByArticleId(articleId);
		if (tagIds == null || tagIds.isEmpty()) {
			return;
		}
		List<Long> distinctIds = tagIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
		if (distinctIds.isEmpty()) {
			return;
		}
		long existCount = tagMapper.selectCount(new LambdaQueryWrapper<BlogTag>().in(BlogTag::getId, distinctIds));
		if (existCount != distinctIds.size()) {
			throw new BusinessException("存在无效标签");
		}
		distinctIds.forEach(tagId -> {
			BlogArticleTag relation = new BlogArticleTag();
			relation.setArticleId(articleId);
			relation.setTagId(tagId);
			articleTagMapper.insert(relation);
		});
	}

	private ArticleDTO toDto(BlogArticle article) {
		ArticleDTO dto = new ArticleDTO();
		BeanUtils.copyProperties(article, dto);
		return dto;
	}

	/**
	 * 是否 ADMIN（种子约定 sys_role.id=1）
	 */
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

	/**
	 * 加载文章并做行级归属校验：ADMIN 可管全部；其他人仅本人
	 */
	private BlogArticle requireOwnedArticle(Long id) {
		BlogArticle article = blogArticleMapper.selectById(id);
		if (article == null) {
			throw new BusinessException("文章不存在");
		}
		if (!isAdmin() && !Objects.equals(article.getAuthorId(), currentUserId())) {
			throw new BusinessException("无权操作他人文章");
		}
		return article;
	}

}
