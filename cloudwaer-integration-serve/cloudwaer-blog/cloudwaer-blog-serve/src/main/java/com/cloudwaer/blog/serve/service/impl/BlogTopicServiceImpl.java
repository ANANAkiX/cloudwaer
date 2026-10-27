package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.blog.api.dto.TopicDTO;
import com.cloudwaer.blog.api.dto.TopicReplyDTO;
import com.cloudwaer.blog.api.dto.TopicReplySaveDTO;
import com.cloudwaer.blog.api.dto.TopicSaveDTO;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import com.cloudwaer.blog.serve.entity.BlogLike;
import com.cloudwaer.blog.serve.entity.BlogTopic;
import com.cloudwaer.blog.serve.entity.BlogTopicReply;
import com.cloudwaer.blog.serve.mapper.BlogArticleMapper;
import com.cloudwaer.blog.serve.mapper.BlogLikeMapper;
import com.cloudwaer.blog.serve.mapper.BlogTopicMapper;
import com.cloudwaer.blog.serve.mapper.BlogTopicReplyMapper;
import com.cloudwaer.blog.serve.service.BlogLikeService;
// TARGET_TOPIC 常量来自 BlogLikeService
import com.cloudwaer.blog.serve.service.BlogTopicService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 微论坛服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogTopicServiceImpl implements BlogTopicService {

	@Autowired
	private BlogTopicMapper topicMapper;

	@Autowired
	private BlogTopicReplyMapper replyMapper;

	@Autowired
	private BlogLikeMapper likeMapper;

	@Autowired
	private BlogArticleMapper articleMapper;

	@Override
	public PageResult<TopicDTO> publicPage(Long current, Long size, String keyword, Boolean essenceOnly) {
		Page<BlogTopic> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogTopic> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.and(w -> w.like(BlogTopic::getTitle, keyword).or().like(BlogTopic::getContent, keyword));
		}
		if (Boolean.TRUE.equals(essenceOnly)) {
			wrapper.eq(BlogTopic::getIsEssence, 1);
		}
		wrapper.orderByDesc(BlogTopic::getIsTop).orderByDesc(BlogTopic::getLastReplyTime).orderByDesc(BlogTopic::getId);

		IPage<BlogTopic> pr = topicMapper.selectPage(page, wrapper);
		List<TopicDTO> list = pr.getRecords().stream().map(t -> toDto(t, false)).collect(Collectors.toList());
		fillLiked(list);
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	public TopicDTO publicDetail(Long id) {
		BlogTopic topic = topicMapper.selectById(id);
		if (topic == null) {
			throw new BusinessException("话题不存在");
		}
		topicMapper.incrementView(id);
		topic.setViewCount((topic.getViewCount() == null ? 0 : topic.getViewCount()) + 1);
		return toDto(topic, true);
	}

	@Override
	public List<TopicReplyDTO> listReplies(Long topicId) {
		List<BlogTopicReply> all = replyMapper
			.selectList(new LambdaQueryWrapper<BlogTopicReply>().eq(BlogTopicReply::getTopicId, topicId)
				.orderByAsc(BlogTopicReply::getCreateTime));
		if (all.isEmpty()) {
			return new ArrayList<>();
		}
		Map<Long, TopicReplyDTO> topMap = new LinkedHashMap<>();
		List<TopicReplyDTO> replies = new ArrayList<>();
		for (BlogTopicReply r : all) {
			TopicReplyDTO dto = toReplyDto(r);
			if (r.getParentId() == null || r.getParentId() == 0L) {
				topMap.put(r.getId(), dto);
			}
			else {
				replies.add(dto);
			}
		}
		for (TopicReplyDTO reply : replies) {
			TopicReplyDTO root = topMap.get(reply.getRootId() != null ? reply.getRootId() : 0L);
			if (root != null) {
				root.getReplies().add(reply);
			}
		}
		return new ArrayList<>(topMap.values());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long saveTopic(TopicSaveDTO dto) {
		Long userId = currentUserId();
		BlogTopic topic = new BlogTopic();
		topic.setTitle(dto.getTitle().trim());
		topic.setContent(dto.getContent());
		topic.setUserId(userId);
		topic.setNickname(SecurityContextUtil.getCurrentUsername());
		topic.setViewCount(0);
		topic.setLikeCount(0);
		topic.setReplyCount(0);
		topic.setIsTop(0);
		topic.setIsEssence(0);
		topicMapper.insert(topic);
		return topic.getId();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long reply(TopicReplySaveDTO dto) {
		Long userId = currentUserId();
		BlogTopic topic = topicMapper.selectById(dto.getTopicId());
		if (topic == null) {
			throw new BusinessException("话题不存在");
		}

		Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
		Long rootId = dto.getRootId() == null ? 0L : dto.getRootId();
		if (parentId > 0) {
			BlogTopicReply parent = replyMapper.selectById(parentId);
			if (parent == null) {
				throw new BusinessException("父回复不存在");
			}
			if (!topic.getId().equals(parent.getTopicId())) {
				throw new BusinessException("父回复不属于当前话题");
			}
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

		BlogTopicReply entity = new BlogTopicReply();
		entity.setTopicId(topic.getId());
		entity.setParentId(parentId);
		entity.setRootId(rootId);
		entity.setUserId(userId);
		entity.setNickname(SecurityContextUtil.getCurrentUsername());
		entity.setContent(dto.getContent());
		entity.setLikeCount(0);
		replyMapper.insert(entity);
		topicMapper.incrementReply(topic.getId());
		return entity.getId();
	}

	@Override
	public PageResult<TopicDTO> adminPage(Long current, Long size, String keyword, Integer isTop, Integer isEssence) {
		Page<BlogTopic> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogTopic> wrapper = new LambdaQueryWrapper<>();
		if (StringUtils.hasText(keyword)) {
			wrapper.like(BlogTopic::getTitle, keyword);
		}
		if (isTop != null) {
			wrapper.eq(BlogTopic::getIsTop, isTop);
		}
		if (isEssence != null) {
			wrapper.eq(BlogTopic::getIsEssence, isEssence);
		}
		wrapper.orderByDesc(BlogTopic::getCreateTime);
		IPage<BlogTopic> pr = topicMapper.selectPage(page, wrapper);
		List<TopicDTO> list = pr.getRecords().stream().map(t -> toDto(t, false)).collect(Collectors.toList());
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean adminUpdateFlags(Long id, Integer isTop, Integer isEssence) {
		BlogTopic existing = topicMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("话题不存在");
		}
		if (isTop != null) {
			existing.setIsTop(isTop);
		}
		if (isEssence != null) {
			existing.setIsEssence(isEssence);
		}
		topicMapper.updateById(existing);
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean adminDelete(Long id) {
		BlogTopic existing = topicMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("话题不存在");
		}
		topicMapper.deleteById(id);
		replyMapper.physicalDeleteByTopicId(id);
		return Boolean.TRUE;
	}

	@Override
	public int refreshArticleHeat() {
		List<BlogArticle> list = articleMapper.selectList(
				new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getPublishStatus, 1).select(BlogArticle::getId));
		int n = 0;
		for (BlogArticle a : list) {
			n += articleMapper.refreshHeat(a.getId());
		}
		return n;
	}

	private void fillLiked(List<TopicDTO> list) {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null || list == null || list.isEmpty()) {
			list.forEach(t -> t.setLiked(Boolean.FALSE));
			return;
		}
		List<Long> ids = list.stream().map(TopicDTO::getId).collect(Collectors.toList());
		List<Long> likedIds = likeMapper
			.selectList(new LambdaQueryWrapper<BlogLike>().eq(BlogLike::getTargetType, BlogLikeService.TARGET_TOPIC)
				.eq(BlogLike::getUserId, userId)
				.in(BlogLike::getTargetId, ids))
			.stream()
			.map(BlogLike::getTargetId)
			.collect(Collectors.toList());
		list.forEach(t -> t.setLiked(likedIds.contains(t.getId())));
	}

	private Long currentUserId() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录");
		}
		return userId;
	}

	private TopicDTO toDto(BlogTopic entity, boolean withContent) {
		TopicDTO dto = new TopicDTO();
		BeanUtils.copyProperties(entity, dto);
		if (!withContent) {
			dto.setContent(null);
		}
		return dto;
	}

	private TopicReplyDTO toReplyDto(BlogTopicReply entity) {
		TopicReplyDTO dto = new TopicReplyDTO();
		BeanUtils.copyProperties(entity, dto);
		if (dto.getReplies() == null) {
			dto.setReplies(new ArrayList<>());
		}
		return dto;
	}

}
