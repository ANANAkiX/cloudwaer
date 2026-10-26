package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.blog.api.dto.BloggerStatusDTO;
import com.cloudwaer.blog.api.dto.BloggerSuspendDTO;
import com.cloudwaer.blog.serve.entity.BlogBloggerStatus;
import com.cloudwaer.blog.serve.mapper.BlogBloggerStatusMapper;
import com.cloudwaer.blog.serve.service.BloggerManageService;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 博客主管理服务实现（方案B：blog 侧状态表 + 发布链路拦截 + 到期惰性恢复）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BloggerManageServiceImpl implements BloggerManageService {

	/**
	 * 暂停生效中
	 */
	private static final int REVOKED_NO = 0;

	/**
	 * 已解除
	 */
	private static final int REVOKED_YES = 1;

	@Autowired
	private BlogBloggerStatusMapper statusMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean suspend(BloggerSuspendDTO dto) {
		BlogBloggerStatus existing = latestOf(dto.getUserId());
		String handleUser = SecurityContextUtil.getCurrentUsername();
		if (existing == null) {
			existing = new BlogBloggerStatus();
			existing.setUserId(dto.getUserId());
			existing.setSuspendUntil(dto.getSuspendUntil());
			existing.setSuspendReason(dto.getReason());
			existing.setRevoked(REVOKED_NO);
			existing.setHandleUser(handleUser);
			statusMapper.insert(existing);
		}
		else {
			// 同一行续期/改原因，保证 user_id 唯一
			existing.setSuspendUntil(dto.getSuspendUntil());
			existing.setSuspendReason(dto.getReason());
			existing.setRevoked(REVOKED_NO);
			existing.setHandleUser(handleUser);
			statusMapper.updateById(existing);
		}
		log.info("博客主已暂停: userId={}, until={}, by={}", dto.getUserId(), dto.getSuspendUntil(), handleUser);
		return Boolean.TRUE;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean release(Long userId) {
		BlogBloggerStatus existing = latestOf(userId);
		if (existing != null && (existing.getRevoked() == null || existing.getRevoked() == REVOKED_NO)) {
			existing.setRevoked(REVOKED_YES);
			existing.setHandleUser(SecurityContextUtil.getCurrentUsername());
			statusMapper.updateById(existing);
			log.info("博客主暂停已解除: userId={}", userId);
		}
		// 幂等：未在暂停中也算成功，避免竞态下误报
		return Boolean.TRUE;
	}

	@Override
	public List<BloggerStatusDTO> listByUserIds(List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> ids = userIds.stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
		if (ids.isEmpty()) {
			return new ArrayList<>();
		}
		List<BlogBloggerStatus> rows = statusMapper
			.selectList(new LambdaQueryWrapper<BlogBloggerStatus>().in(BlogBloggerStatus::getUserId, ids)
				.orderByDesc(BlogBloggerStatus::getUpdateTime));
		// 每人只取最新一行
		Map<Long, BlogBloggerStatus> latest = rows.stream()
			.collect(Collectors.toMap(BlogBloggerStatus::getUserId, r -> r, (a, b) -> a));
		List<BloggerStatusDTO> result = new ArrayList<>();
		for (BlogBloggerStatus row : latest.values()) {
			result.add(toDto(row));
		}
		return result;
	}

	@Override
	public BloggerStatusDTO myStatus() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录");
		}
		List<BloggerStatusDTO> list = listByUserIds(List.of(userId));
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public String blockedReason(Long userId) {
		if (userId == null) {
			return null;
		}
		BlogBloggerStatus row = latestOf(userId);
		if (row == null || (row.getRevoked() != null && row.getRevoked() == REVOKED_YES)) {
			return null;
		}
		LocalDateTime until = row.getSuspendUntil();
		String reason = row.getSuspendReason();
		String reasonText = (reason == null || reason.isBlank()) ? "" : "（原因：" + reason + "）";
		if (until == null) {
			return "博客主权限已被暂停" + reasonText + "，请联系管理员解除后再发布";
		}
		// 到期即视为恢复（惰性判断，无需定时任务）
		if (until.isAfter(LocalDateTime.now())) {
			return "博客主权限已暂停至 " + until.toString().replace('T', ' ').substring(0, 16) + reasonText + "，到期后可继续发布";
		}
		return null;
	}

	private BlogBloggerStatus latestOf(Long userId) {
		if (userId == null) {
			return null;
		}
		return statusMapper
			.selectOne(new LambdaQueryWrapper<BlogBloggerStatus>().eq(BlogBloggerStatus::getUserId, userId)
				.orderByDesc(BlogBloggerStatus::getUpdateTime)
				.last("LIMIT 1"));
	}

	private BloggerStatusDTO toDto(BlogBloggerStatus entity) {
		BloggerStatusDTO dto = new BloggerStatusDTO();
		BeanUtils.copyProperties(entity, dto);
		boolean active = entity.getRevoked() == null || entity.getRevoked() != REVOKED_YES;
		LocalDateTime until = entity.getSuspendUntil();
		// 惰性：已到期即视为未暂停
		boolean notExpired = until == null || until.isAfter(LocalDateTime.now());
		dto.setSuspended(active && notExpired);
		return dto;
	}

}
