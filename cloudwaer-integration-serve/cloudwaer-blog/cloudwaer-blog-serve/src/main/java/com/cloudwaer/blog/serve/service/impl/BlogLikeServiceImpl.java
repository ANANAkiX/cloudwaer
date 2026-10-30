package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.blog.api.dto.LikeToggleDTO;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import com.cloudwaer.blog.serve.entity.BlogComment;
import com.cloudwaer.blog.serve.entity.BlogLike;
import com.cloudwaer.blog.serve.entity.BlogTopic;
import com.cloudwaer.blog.serve.entity.BlogTopicReply;
import com.cloudwaer.blog.serve.mapper.BlogArticleMapper;
import com.cloudwaer.blog.serve.mapper.BlogCommentMapper;
import com.cloudwaer.blog.serve.mapper.BlogLikeMapper;
import com.cloudwaer.blog.serve.mapper.BlogTopicMapper;
import com.cloudwaer.blog.serve.mapper.BlogTopicReplyMapper;
import com.cloudwaer.blog.serve.service.BlogCommentService;
import com.cloudwaer.blog.serve.service.BlogLikeService;
import com.cloudwaer.blog.serve.service.BlogMessageNotifier;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogLikeServiceImpl implements BlogLikeService {

	@Autowired
	private BlogLikeMapper likeMapper;

	@Autowired
	private BlogArticleMapper articleMapper;

	@Autowired
	private BlogCommentMapper commentMapper;

	@Autowired
	private BlogTopicMapper topicMapper;

	@Autowired
	private BlogTopicReplyMapper topicReplyMapper;

	@Autowired
	private BlogCommentService commentService;

	@Autowired
	private BlogMessageNotifier blogMessageNotifier;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public LikeToggleDTO toggle(Integer targetType, Long targetId) {
		if (targetType == null || targetId == null) {
			throw new BusinessException("参数错误");
		}
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录后点赞");
		}

		BlogLike existing = likeMapper
			.selectOne(new LambdaQueryWrapper<BlogLike>().eq(BlogLike::getTargetType, targetType)
				.eq(BlogLike::getTargetId, targetId)
				.eq(BlogLike::getUserId, userId)
				.last("LIMIT 1"));

		if (existing != null) {
			likeMapper.physicalDelete(targetType, targetId, userId);
			applyCounter(targetType, targetId, false);
			LikeToggleDTO dto = new LikeToggleDTO();
			dto.setLiked(false);
			dto.setLikeCount(currentCount(targetType, targetId));
			if (targetType == TARGET_ARTICLE) {
				articleMapper.refreshHeat(targetId);
			}
			else if (targetType == TARGET_COMMENT) {
				// 点赞变化 → 评论质量分重算（排序键随事件更新）
				commentService.refreshQualityScore(targetId);
			}
			return dto;
		}

		// 校验目标存在
		assertTargetExists(targetType, targetId);

		BlogLike like = new BlogLike();
		like.setTargetType(targetType);
		like.setTargetId(targetId);
		like.setUserId(userId);
		likeMapper.insert(like);
		applyCounter(targetType, targetId, true);

		// 站内信：点赞通知目标作者（自己赞自己不发）
		notifyLike(targetType, targetId, userId);

		LikeToggleDTO dto = new LikeToggleDTO();
		dto.setLiked(true);
		dto.setLikeCount(currentCount(targetType, targetId));
		if (targetType == TARGET_ARTICLE) {
			articleMapper.refreshHeat(targetId);
		}
		else if (targetType == TARGET_COMMENT) {
			commentService.refreshQualityScore(targetId);
		}
		return dto;
	}

	/**
	 * 点赞站内信：文章 → 作者；评论 → 评论人（话题/回复暂不发）
	 */
	private void notifyLike(Integer targetType, Long targetId, Long likerId) {
		try {
			String likerName = SecurityContextUtil.getCurrentUsername();
			if (targetType == TARGET_ARTICLE) {
				BlogArticle article = articleMapper.selectById(targetId);
				if (article != null) {
					blogMessageNotifier.notifyArticleLiked(article.getAuthorId(), likerId, likerName, targetId,
							article.getTitle());
				}
			}
			else if (targetType == TARGET_COMMENT) {
				BlogComment comment = commentMapper.selectById(targetId);
				if (comment != null) {
					String content = comment.getContent();
					String snippet = content == null ? ""
							: (content.length() > 30 ? content.substring(0, 30) + "…" : content);
					blogMessageNotifier.notifyCommentLiked(comment.getUserId(), likerId, likerName,
							comment.getArticleId(), snippet);
				}
			}
		}
		catch (Exception e) {
			log.warn("点赞站内信发送失败（不影响点赞）: {}", e.getMessage());
		}
	}

	@Override
	public Boolean liked(Integer targetType, Long targetId) {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null || targetType == null || targetId == null) {
			return Boolean.FALSE;
		}
		Long count = likeMapper.selectCount(new LambdaQueryWrapper<BlogLike>().eq(BlogLike::getTargetType, targetType)
			.eq(BlogLike::getTargetId, targetId)
			.eq(BlogLike::getUserId, userId));
		return count != null && count > 0;
	}

	private void assertTargetExists(Integer targetType, Long targetId) {
		boolean exists = switch (targetType) {
			case TARGET_ARTICLE -> articleMapper.selectById(targetId) != null;
			case TARGET_COMMENT -> commentMapper.selectById(targetId) != null;
			case TARGET_TOPIC -> topicMapper.selectById(targetId) != null;
			case TARGET_TOPIC_REPLY -> topicReplyMapper.selectById(targetId) != null;
			default -> false;
		};
		if (!exists) {
			throw new BusinessException("点赞目标不存在");
		}
	}

	private void applyCounter(Integer targetType, Long targetId, boolean increment) {
		switch (targetType) {
			case TARGET_ARTICLE -> {
				if (increment) {
					articleMapper.incrementLike(targetId);
				}
				else {
					articleMapper.decrementLike(targetId);
				}
			}
			case TARGET_COMMENT -> {
				if (increment) {
					commentMapper.incrementLike(targetId);
				}
				else {
					commentMapper.decrementLike(targetId);
				}
			}
			case TARGET_TOPIC -> {
				if (increment) {
					topicMapper.incrementLike(targetId);
				}
				else {
					topicMapper.decrementLike(targetId);
				}
			}
			case TARGET_TOPIC_REPLY -> {
				BlogTopicReply reply = topicReplyMapper.selectById(targetId);
				if (reply != null) {
					if (increment) {
						reply.setLikeCount((reply.getLikeCount() == null ? 0 : reply.getLikeCount()) + 1);
					}
					else {
						reply.setLikeCount(Math.max((reply.getLikeCount() == null ? 0 : reply.getLikeCount()) - 1, 0));
					}
					topicReplyMapper.updateById(reply);
				}
			}
			default -> throw new BusinessException("不支持的点赞类型");
		}
	}

	private Integer currentCount(Integer targetType, Long targetId) {
		return switch (targetType) {
			case TARGET_ARTICLE -> {
				BlogArticle a = articleMapper.selectById(targetId);
				yield a == null ? 0 : nvl(a.getLikeCount());
			}
			case TARGET_COMMENT -> {
				BlogComment c = commentMapper.selectById(targetId);
				yield c == null ? 0 : nvl(c.getLikeCount());
			}
			case TARGET_TOPIC -> {
				BlogTopic t = topicMapper.selectById(targetId);
				yield t == null ? 0 : nvl(t.getLikeCount());
			}
			case TARGET_TOPIC_REPLY -> {
				BlogTopicReply r = topicReplyMapper.selectById(targetId);
				yield r == null ? 0 : nvl(r.getLikeCount());
			}
			default -> 0;
		};
	}

	private int nvl(Integer v) {
		return v == null ? 0 : v;
	}

}
