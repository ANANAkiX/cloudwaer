package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.blog.api.dto.CommentDTO;
import com.cloudwaer.blog.api.dto.CommentSaveDTO;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import com.cloudwaer.blog.serve.entity.BlogComment;
import com.cloudwaer.blog.serve.mapper.BlogArticleMapper;
import com.cloudwaer.blog.serve.mapper.BlogCommentMapper;
import com.cloudwaer.blog.serve.mapper.BlogLikeMapper;
import com.cloudwaer.blog.serve.entity.BlogLike;
import com.cloudwaer.blog.serve.service.BlogCommentService;
import com.cloudwaer.blog.serve.service.BlogLikeService;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 博客评论服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogCommentServiceImpl implements BlogCommentService {

	/**
	 * 审核状态：待审核
	 */
	private static final int AUDIT_PENDING = 0;

	/**
	 * 审核状态：已通过
	 */
	private static final int AUDIT_APPROVED = 1;

	/**
	 * 审核状态：已拒绝
	 */
	private static final int AUDIT_REJECTED = 2;

	/**
	 * ADMIN 角色ID（种子 sys_role.id=1）
	 */
	private static final Long ADMIN_ROLE_ID = 1L;

	/**
	 * 前台默认顶级评论页大小
	 */
	private static final long DEFAULT_TOP_SIZE = 5L;

	/**
	 * 前台默认每条附带子评论数
	 */
	private static final long DEFAULT_CHILD_SIZE = 3L;

	/**
	 * 前台展开回复默认每次加载条数
	 */
	private static final long DEFAULT_REPLY_PAGE = 10L;

	/**
	 * 评论附图最大张数
	 */
	private static final int MAX_IMAGES = 3;

	/**
	 * 附图路径白名单：仅本站上传目录
	 */
	private static final Pattern IMAGE_PATH = Pattern.compile("^/blog/uploads/[A-Za-z0-9._-]+$");

	/**
	 * 评论附图JSON序列化
	 */
	private static final ObjectMapper IMAGE_MAPPER = new ObjectMapper();

	@Autowired
	private BlogCommentMapper commentMapper;

	@Autowired
	private BlogArticleMapper articleMapper;

	@Autowired
	private BlogLikeMapper likeMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addComment(CommentSaveDTO dto, String clientIp) {
		BlogArticle article = articleMapper.selectById(dto.getArticleId());
		if (article == null || article.getStatus() == null || article.getStatus() != 1) {
			throw new BusinessException("文章不存在");
		}
		if (article.getPublishStatus() == null || article.getPublishStatus() != 1) {
			throw new BusinessException("仅已发布文章可评论");
		}

		Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
		Long rootId = dto.getRootId() == null ? 0L : dto.getRootId();

		if (parentId > 0) {
			BlogComment parent = commentMapper.selectById(parentId);
			if (parent == null || parent.getAuditStatus() == null || parent.getAuditStatus() != AUDIT_APPROVED) {
				throw new BusinessException("父评论不存在或未通过审核");
			}
			if (!article.getId().equals(parent.getArticleId())) {
				throw new BusinessException("父评论不属于当前文章");
			}
			// 回复顶级评论：root_id 取该评论自身；回复楼中楼：沿用其 root_id
			if (parent.getParentId() == null || parent.getParentId() == 0L) {
				rootId = parent.getId();
			}
			else {
				rootId = parent.getRootId();
			}
		}
		else {
			parentId = 0L;
			rootId = 0L;
		}

		Long userId = SecurityContextUtil.getCurrentUserId();
		String username = SecurityContextUtil.getCurrentUsername();
		if (userId == null || !StringUtils.hasText(username)) {
			throw new BusinessException("请先登录后再评论");
		}

		BlogComment comment = new BlogComment();
		comment.setArticleId(article.getId());
		comment.setParentId(parentId);
		comment.setRootId(rootId);
		comment.setUserId(userId);
		// 展示昵称优先（不落账号名）；历史Token无 nickname 时兜底 username
		String display = username;
		String avatar = null;
		Map<String, Object> tokenData = SecurityContextUtil.getCurrentTokenData();
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
		comment.setNickname(display);
		comment.setAvatar(avatar);
		comment.setContent(dto.getContent());
		comment.setImages(serializeImages(dto.getImages()));
		comment.setLikeCount(0);
		comment.setAuditStatus(AUDIT_PENDING);
		if (StringUtils.hasText(clientIp)) {
			comment.setIp(clientIp);
		}
		commentMapper.insert(comment);
		log.info("评论已提交待审核: id={}, articleId={}, userId={}", comment.getId(), article.getId(), userId);
		return comment.getId();
	}

	@Override
	public List<CommentDTO> listByArticle(Long articleId) {
		List<BlogComment> all = commentMapper
			.selectList(applyOrder(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getArticleId, articleId)
				.eq(BlogComment::getAuditStatus, AUDIT_APPROVED)));
		if (all.isEmpty()) {
			return new ArrayList<>();
		}

		Map<Long, CommentDTO> topMap = new LinkedHashMap<>();
		List<CommentDTO> replies = new ArrayList<>();
		for (BlogComment c : all) {
			CommentDTO dto = toDto(c);
			if (c.getParentId() == null || c.getParentId() == 0L) {
				topMap.put(c.getId(), dto);
			}
			else {
				replies.add(dto);
			}
		}
		for (CommentDTO reply : replies) {
			CommentDTO root = topMap.get(reply.getRootId() != null ? reply.getRootId() : 0L);
			if (root != null) {
				root.getReplies().add(reply);
			}
		}
		List<CommentDTO> tops = new ArrayList<>(topMap.values());
		List<CommentDTO> flat = new ArrayList<>(tops);
		for (CommentDTO t : tops) {
			flat.addAll(t.getReplies());
		}
		fillLiked(flat);
		return tops;
	}

	@Override
	public PageResult<CommentDTO> listPage(Long articleId, Long current, Long size, Long childSize) {
		long cur = current == null || current < 1 ? 1L : current;
		long pageSize = size == null || size < 1 ? DEFAULT_TOP_SIZE : size;
		long child = childSize == null || childSize < 1 ? DEFAULT_CHILD_SIZE : childSize;

		Page<BlogComment> page = new Page<>(cur, pageSize);
		// 排序统一走 applyOrder：质量分优先，同分按创建时间（沙发在前）
		IPage<BlogComment> pr = commentMapper.selectPage(page,
				applyOrder(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getArticleId, articleId)
					.eq(BlogComment::getAuditStatus, AUDIT_APPROVED)
					.eq(BlogComment::getParentId, 0L)));

		List<CommentDTO> tops = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		if (!tops.isEmpty()) {
			List<Long> rootIds = tops.stream().map(CommentDTO::getId).collect(Collectors.toList());
			List<BlogComment> allReplies = commentMapper
				.selectList(applyOrder(new LambdaQueryWrapper<BlogComment>().in(BlogComment::getRootId, rootIds)
					.eq(BlogComment::getAuditStatus, AUDIT_APPROVED)));
			Map<Long, List<CommentDTO>> byRoot = new LinkedHashMap<>();
			for (BlogComment r : allReplies) {
				byRoot.computeIfAbsent(r.getRootId(), k -> new ArrayList<>()).add(toDto(r));
			}
			for (CommentDTO top : tops) {
				List<CommentDTO> replies = byRoot.getOrDefault(top.getId(), Collections.emptyList());
				top.setReplyTotal(replies.size());
				top.setReplies(replies.size() > child ? new ArrayList<>(replies.subList(0, (int) child))
						: new ArrayList<>(replies));
			}
		}
		List<CommentDTO> flat = new ArrayList<>(tops);
		for (CommentDTO t : tops) {
			flat.addAll(t.getReplies());
		}
		fillLiked(flat);
		return new PageResult<>(tops, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public PageResult<CommentDTO> listReplies(Long rootId, Long offset, Long size) {
		if (rootId == null || rootId <= 0) {
			throw new BusinessException("缺少根评论ID");
		}
		long off = offset == null || offset < 0 ? 0L : offset;
		long pageSize = size == null || size < 1 ? DEFAULT_REPLY_PAGE : size;
		// 展开更多：按已展示条数 OFFSET 拉取，每次固定 +N（排序键与顶级一致，翻页不重不漏）
		List<BlogComment> list = commentMapper
			.selectList(applyOrder(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getRootId, rootId)
				.eq(BlogComment::getAuditStatus, AUDIT_APPROVED)).last("LIMIT " + pageSize + " OFFSET " + off));
		Long total = commentMapper.selectCount(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getRootId, rootId)
			.eq(BlogComment::getAuditStatus, AUDIT_APPROVED));
		List<CommentDTO> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
		fillLiked(dtos);
		return new PageResult<>(dtos, total, off / pageSize + 1, pageSize);
	}

	@Override
	public PageResult<CommentDTO> page(Long current, Long size, Long articleId, Integer auditStatus, String keyword) {
		requireAdmin();
		Page<BlogComment> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogComment> wrapper = new LambdaQueryWrapper<>();
		if (articleId != null) {
			wrapper.eq(BlogComment::getArticleId, articleId);
		}
		if (auditStatus != null) {
			wrapper.eq(BlogComment::getAuditStatus, auditStatus);
		}
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogComment::getContent, keyword);
		}
		wrapper.orderByDesc(BlogComment::getCreateTime);

		IPage<BlogComment> pr = commentMapper.selectPage(page, wrapper);
		List<CommentDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		fillArticleTitles(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean audit(Long id, Integer auditStatus) {
		requireAdmin();
		if (auditStatus == null || (auditStatus != AUDIT_APPROVED && auditStatus != AUDIT_REJECTED)) {
			throw new BusinessException("非法审核结果");
		}
		BlogComment existing = commentMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("评论不存在");
		}
		Integer old = existing.getAuditStatus();
		if (old != null && old != AUDIT_PENDING) {
			throw new BusinessException("仅待审核评论可审核");
		}
		existing.setAuditStatus(auditStatus);
		commentMapper.updateById(existing);
		if (auditStatus == AUDIT_APPROVED) {
			articleMapper.incrementComment(existing.getArticleId());
			articleMapper.refreshHeat(existing.getArticleId());
			// 质量分：本条（点赞/存龄）与回复数受影响的父、根一并重算
			refreshQualityScore(existing.getId());
			refreshRelatedScores(existing);
		}
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteComment(Long id) {
		BlogComment existing = commentMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("评论不存在");
		}
		// 本人或管理员
		if (!isAdmin()) {
			Long userId = SecurityContextUtil.getCurrentUserId();
			if (userId == null || !userId.equals(existing.getUserId())) {
				throw new BusinessException("无权删除该评论");
			}
		}
		commentMapper.deleteById(id);
		if (existing.getAuditStatus() != null && existing.getAuditStatus() == AUDIT_APPROVED) {
			articleMapper.decrementComment(existing.getArticleId());
			articleMapper.refreshHeat(existing.getArticleId());
			// 回复数变化 → 父与根的质量分重算（被删行本身无需重算）
			refreshRelatedScores(existing);
		}
		// 连带删除楼中楼回复（同一根评论下尚未通过的也一并逻辑删除）
		List<BlogComment> children = commentMapper
			.selectList(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getRootId, id));
		for (BlogComment child : children) {
			commentMapper.deleteById(child.getId());
			if (child.getAuditStatus() != null && child.getAuditStatus() == AUDIT_APPROVED) {
				articleMapper.decrementComment(child.getArticleId());
				articleMapper.refreshHeat(child.getArticleId());
			}
		}
		return Boolean.TRUE;
	}

	private void requireAdmin() {
		if (!isAdmin()) {
			throw new BusinessException("仅管理员可操作");
		}
	}

	/**
	 * 前台排序策略开关：true=质量分优先（离线落库、事件触发重算，排序键稳定）；false=纯时间序回退
	 */
	private static final boolean ORDER_BY_QUALITY = true;

	/**
	 * 前台列表统一排序：质量分降序 → 创建时间升序（同分保持沙发在前）
	 *
	 * <p>
	 * 全部分页/树接口共用，保证 offset 翻页的排序键一致。
	 * </p>
	 */
	private LambdaQueryWrapper<BlogComment> applyOrder(LambdaQueryWrapper<BlogComment> wrapper) {
		if (ORDER_BY_QUALITY) {
			wrapper.orderByDesc(BlogComment::getQualityScore);
		}
		wrapper.orderByAsc(BlogComment::getCreateTime);
		return wrapper;
	}

	@Override
	public void refreshQualityScore(Long commentId) {
		if (commentId == null || commentId <= 0) {
			return;
		}
		BlogComment c = commentMapper.selectById(commentId);
		if (c == null) {
			// 逻辑删除/不存在：静默跳过（删除链路会引用已删行）
			return;
		}
		long likes = c.getLikeCount() == null ? 0 : c.getLikeCount();
		long replies = countReplies(c);
		long ageDays = 0;
		if (c.getCreateTime() != null) {
			ageDays = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(c.getCreateTime().toLocalDate(),
					java.time.LocalDate.now()));
		}
		// 初版算分：(点赞*2 + 回复数) / (1 + 存龄天数/30)，保留 4 位
		double score = (likes * 2 + replies) / (1 + ageDays / 30.0);
		c.setQualityScore(Math.round(score * 10000.0) / 10000.0);
		commentMapper.updateById(c);
	}

	/**
	 * 回复数：顶级评论取楼中楼总数（root_id 命中），楼中楼取直接回复数（parent_id 命中）
	 */
	private long countReplies(BlogComment c) {
		boolean topLevel = c.getParentId() == null || c.getParentId() == 0L;
		LambdaQueryWrapper<BlogComment> w = new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getAuditStatus,
				AUDIT_APPROVED);
		if (topLevel) {
			w.eq(BlogComment::getRootId, c.getId());
		}
		else {
			w.eq(BlogComment::getParentId, c.getId());
		}
		Long n = commentMapper.selectCount(w);
		return n == null ? 0 : n;
	}

	/**
	 * 回复数受影响的关联行：直接父 + 楼中楼根（两者可能相同/为空）
	 */
	private void refreshRelatedScores(BlogComment c) {
		Long parentId = c.getParentId();
		if (parentId != null && parentId > 0) {
			refreshQualityScore(parentId);
		}
		Long rootId = c.getRootId();
		if (rootId != null && rootId > 0 && !rootId.equals(parentId)) {
			refreshQualityScore(rootId);
		}
	}

	private boolean isAdmin() {
		List<Long> roleIds = SecurityContextUtil.getCurrentRoleIds();
		return roleIds != null && roleIds.contains(ADMIN_ROLE_ID);
	}

	private void fillArticleTitles(List<CommentDTO> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		List<Long> ids = list.stream().map(CommentDTO::getArticleId).distinct().collect(Collectors.toList());
		if (ids.isEmpty()) {
			return;
		}
		Map<Long, String> titles = articleMapper.selectBatchIds(ids)
			.stream()
			.filter(a -> a.getTitle() != null)
			.collect(Collectors.toMap(BlogArticle::getId, BlogArticle::getTitle, (a, b) -> a));
		list.forEach(c -> c.setArticleTitle(titles.get(c.getArticleId())));
	}

	private CommentDTO toDto(BlogComment entity) {
		CommentDTO dto = new CommentDTO();
		BeanUtils.copyProperties(entity, dto);
		if (dto.getReplies() == null) {
			dto.setReplies(new ArrayList<>());
		}
		dto.setImages(parseImages(entity.getImages()));
		dto.setLiked(Boolean.FALSE);
		return dto;
	}

	/**
	 * 批量回填「我是否赞过」：登录后一次查询整页；匿名保持 false 不查库
	 */
	private void fillLiked(List<CommentDTO> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return;
		}
		List<Long> ids = list.stream().map(CommentDTO::getId).filter(Objects::nonNull).collect(Collectors.toList());
		if (ids.isEmpty()) {
			return;
		}
		Set<Long> likedIds = likeMapper
			.selectList(new LambdaQueryWrapper<BlogLike>().eq(BlogLike::getTargetType, BlogLikeService.TARGET_COMMENT)
				.in(BlogLike::getTargetId, ids)
				.eq(BlogLike::getUserId, userId))
			.stream()
			.map(BlogLike::getTargetId)
			.collect(Collectors.toSet());
		list.forEach(dto -> dto.setLiked(likedIds.contains(dto.getId())));
	}

	/**
	 * 校验并序列化附图：仅允许本站 /blog/uploads/** 路径
	 */
	private String serializeImages(List<String> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}
		List<String> cleaned = images.stream()
			.filter(StringUtils::hasText)
			.map(String::trim)
			.distinct()
			.collect(Collectors.toList());
		if (cleaned.size() > MAX_IMAGES) {
			throw new BusinessException("评论图片最多" + MAX_IMAGES + "张");
		}
		for (String path : cleaned) {
			if (!IMAGE_PATH.matcher(path).matches()) {
				throw new BusinessException("评论图片路径不合法");
			}
		}
		try {
			return IMAGE_MAPPER.writeValueAsString(cleaned);
		}
		catch (Exception e) {
			throw new BusinessException("评论图片处理失败");
		}
	}

	private List<String> parseImages(String raw) {
		if (!StringUtils.hasText(raw)) {
			return new ArrayList<>();
		}
		try {
			return IMAGE_MAPPER.readValue(raw, new TypeReference<List<String>>() {
			});
		}
		catch (Exception e) {
			log.warn("评论附图JSON解析失败: {}", raw);
			return new ArrayList<>();
		}
	}

}
