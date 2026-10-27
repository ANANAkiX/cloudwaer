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
import com.cloudwaer.blog.serve.service.BlogLikeService;
import com.cloudwaer.blog.serve.service.BlogSettingService;
import com.cloudwaer.blog.serve.service.BloggerManageService;
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

	/**
	 * 发布状态：草稿
	 */
	private static final int PUBLISH_DRAFT = 0;

	/**
	 * 发布状态：已发布（前台唯一可见态）
	 */
	private static final int PUBLISH_ONLINE = 1;

	/**
	 * 发布状态：已下线
	 */
	private static final int PUBLISH_OFFLINE = 2;

	/**
	 * 发布状态：待审核（发布中间态，前台不可见）
	 */
	private static final int PUBLISH_PENDING = 3;

	/**
	 * 审核结果：通过
	 */
	private static final int AUDIT_APPROVED = 1;

	/**
	 * 审核结果：拒绝
	 */
	private static final int AUDIT_REJECTED = 2;

	@Autowired
	private BlogArticleMapper blogArticleMapper;

	@Autowired
	private BlogSettingService blogSettingService;

	@Autowired
	private BloggerManageService bloggerManageService;

	@Autowired
	private BlogLikeService blogLikeService;

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
	public Integer saveArticle(ArticleSaveDTO saveDTO) {
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
			article.setPublishStatus(PUBLISH_DRAFT);
		}
		article.setPublishStatus(resolvePublishTarget(article.getPublishStatus(), PUBLISH_DRAFT, userId));
		if (article.getPublishStatus() == PUBLISH_ONLINE) {
			article.setPublishTime(LocalDateTime.now());
		}
		normalizeCollectionBinding(article, saveDTO);
		blogArticleMapper.insert(article);
		replaceTags(article.getId(), saveDTO.getTagIds());
		refreshCollectionCount(article.getCollectionId());
		return article.getPublishStatus();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateArticle(ArticleSaveDTO saveDTO) {
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
			int current = existing.getPublishStatus() == null ? PUBLISH_DRAFT : existing.getPublishStatus();
			int target = resolvePublishTarget(saveDTO.getPublishStatus(), current, existing.getAuthorId());
			// 重新提交发布时清掉上次拒绝意见；内容编辑不改变已有审核结论
			if (target == PUBLISH_PENDING && current != PUBLISH_PENDING) {
				existing.setAuditRemark(null);
			}
			existing.setPublishStatus(target);
			if (target == PUBLISH_ONLINE && existing.getPublishTime() == null) {
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
		return existing.getPublishStatus();
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
	@Transactional(rollbackFor = Exception.class)
	public Boolean auditArticle(Long id, Integer auditStatus, String remark) {
		if (!isAdmin()) {
			throw new BusinessException("仅管理员可审核文章");
		}
		if (auditStatus == null || (auditStatus != AUDIT_APPROVED && auditStatus != AUDIT_REJECTED)) {
			throw new BusinessException("非法审核结果");
		}
		BlogArticle article = blogArticleMapper.selectById(id);
		if (article == null) {
			throw new BusinessException("文章不存在");
		}
		int current = article.getPublishStatus() == null ? PUBLISH_DRAFT : article.getPublishStatus();
		if (current != PUBLISH_PENDING) {
			throw new BusinessException("仅待审核文章可审核");
		}
		if (auditStatus == AUDIT_APPROVED) {
			// 暂停期作者的文章不允许放行上线
			String blocked = bloggerManageService.blockedReason(article.getAuthorId());
			if (blocked != null) {
				throw new BusinessException("该作者处于暂停期，无法通过：" + blocked);
			}
			article.setPublishStatus(PUBLISH_ONLINE);
			article.setAuditRemark(null);
			if (article.getPublishTime() == null) {
				article.setPublishTime(LocalDateTime.now());
			}
		}
		else {
			article.setPublishStatus(PUBLISH_DRAFT);
			article.setAuditRemark(StringUtils.hasText(remark) ? remark : "未通过审核");
		}
		blogArticleMapper.updateById(article);
		if (auditStatus == AUDIT_APPROVED) {
			blogArticleMapper.refreshHeat(id);
		}
		log.info("文章审核完成: id={}, result={}, authorId={}", id, auditStatus, article.getAuthorId());
		return Boolean.TRUE;
	}

	/**
	 * 计算落库发布状态。
	 *
	 * <p>
	 * 仅当「请求上线」且非 ADMIN 且站点开关开启时进入待审核(3)；已发布文章再次编辑保持已发布不重审。 上线动作（草稿/已下线 →
	 * 发布）前，先拦截处于暂停期的博客主。
	 * </p>
	 * @param requested 请求的发布状态
	 * @param current 当前库内状态（新建文章传草稿）
	 * @param authorId 作者ID（暂停判定用）
	 * @return 目标发布状态
	 */
	private int resolvePublishTarget(int requested, int current, Long authorId) {
		if (requested != PUBLISH_ONLINE && requested != PUBLISH_PENDING) {
			return requested;
		}
		if (current == PUBLISH_PENDING) {
			// 仍在队列中，编辑内容不改变审核状态
			return PUBLISH_PENDING;
		}
		if (current == PUBLISH_ONLINE) {
			// 已通过：编辑直接生效，不重审
			return PUBLISH_ONLINE;
		}
		// 草稿 / 已下线 → 发布动作：暂停中的博客主一律拦截
		String blocked = bloggerManageService.blockedReason(authorId);
		if (blocked != null) {
			throw new BusinessException(blocked);
		}
		if (isAdmin() || !isArticleAuditRequired()) {
			return PUBLISH_ONLINE;
		}
		return PUBLISH_PENDING;
	}

	/**
	 * 站点开关：非管理员发布是否需审核（缺省开）
	 */
	private boolean isArticleAuditRequired() {
		Integer flag = blogSettingService.getSettings().getRequireArticleAudit();
		return flag == null || flag != 0;
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
		// 回填「我是否赞过」：匿名直接 false（liked() 内部不查库）
		dto.setLiked(Boolean.TRUE.equals(blogLikeService.liked(BlogLikeService.TARGET_ARTICLE, id)));
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
