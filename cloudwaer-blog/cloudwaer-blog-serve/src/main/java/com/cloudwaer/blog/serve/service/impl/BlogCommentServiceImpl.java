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
import com.cloudwaer.blog.serve.service.BlogCommentService;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	@Autowired
	private BlogCommentMapper commentMapper;

	@Autowired
	private BlogArticleMapper articleMapper;

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
		comment.setNickname(username);
		comment.setContent(dto.getContent());
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
			.selectList(new LambdaQueryWrapper<BlogComment>().eq(BlogComment::getArticleId, articleId)
				.eq(BlogComment::getAuditStatus, AUDIT_APPROVED)
				.orderByAsc(BlogComment::getCreateTime));
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
		return new ArrayList<>(topMap.values());
	}

	@Override
	public PageResult<CommentDTO> page(Long current, Long size, Long articleId, Integer auditStatus, String keyword) {
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
		commentMapper.deleteById(id);
		if (existing.getAuditStatus() != null && existing.getAuditStatus() == AUDIT_APPROVED) {
			articleMapper.decrementComment(existing.getArticleId());
			articleMapper.refreshHeat(existing.getArticleId());
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
		return dto;
	}

}
