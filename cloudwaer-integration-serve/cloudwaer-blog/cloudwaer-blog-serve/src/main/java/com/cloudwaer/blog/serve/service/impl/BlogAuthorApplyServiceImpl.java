package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.blog.api.dto.AuthorApplyDTO;
import com.cloudwaer.blog.serve.entity.BlogAuthorApply;
import com.cloudwaer.blog.serve.mapper.BlogAuthorApplyMapper;
import com.cloudwaer.blog.serve.service.BlogAuthorApplyService;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客主申请服务实现类（MVP 打标，角色分配走管理端用户页）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogAuthorApplyServiceImpl implements BlogAuthorApplyService {

	/**
	 * ADMIN 角色ID
	 */
	private static final Long ADMIN_ROLE_ID = 1L;

	/**
	 * GUEST 角色ID
	 */
	private static final Long GUEST_ROLE_ID = 3L;

	/**
	 * BLOGGER 角色ID
	 */
	private static final Long BLOGGER_ROLE_ID = 4L;

	/**
	 * 待处理
	 */
	private static final int STATUS_PENDING = 0;

	/**
	 * 已通过
	 */
	private static final int STATUS_APPROVED = 1;

	/**
	 * 已拒绝
	 */
	private static final int STATUS_REJECTED = 2;

	@Autowired
	private BlogAuthorApplyMapper applyMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long apply(String reason) {
		Long userId = SecurityContextUtil.getCurrentUserId();
		String username = SecurityContextUtil.getCurrentUsername();
		if (userId == null || !StringUtils.hasText(username)) {
			throw new BusinessException("请先登录");
		}

		// 已是博客主或管理员则无需申请
		List<Long> roleIds = SecurityContextUtil.getCurrentRoleIds();
		if (roleIds != null && (roleIds.contains(ADMIN_ROLE_ID) || roleIds.contains(BLOGGER_ROLE_ID))) {
			throw new BusinessException("当前账号已是博客主或管理员");
		}

		// 已有待处理申请则拒绝重复提交
		Long pending = applyMapper
			.selectCount(new LambdaQueryWrapper<BlogAuthorApply>().eq(BlogAuthorApply::getUserId, userId)
				.eq(BlogAuthorApply::getApplyStatus, STATUS_PENDING));
		if (pending != null && pending > 0) {
			throw new BusinessException("已有待处理的申请，请耐心等待审核");
		}

		// 仅访客（含无角色）可申请；BLOGGER/ADMIN 已在上方拦截
		if (roleIds != null && !roleIds.isEmpty() && !roleIds.contains(GUEST_ROLE_ID)) {
			// 非 GUEST 且非 BLOGGER/ADMIN 的杂角色仍允许申请（避免角色扩展时误伤）
			log.info("非标准访客角色申请博客主: userId={}, roleIds={}", userId, roleIds);
		}

		BlogAuthorApply entity = new BlogAuthorApply();
		entity.setUserId(userId);
		entity.setUsername(username);
		// Token 中无昵称字段，先冗余用户名；管理端展示以 username 为准
		entity.setNickname(username);
		entity.setReason(reason);
		entity.setApplyStatus(STATUS_PENDING);
		applyMapper.insert(entity);
		log.info("博客主申请已提交: id={}, userId={}", entity.getId(), userId);
		return entity.getId();
	}

	@Override
	public AuthorApplyDTO myApply() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录");
		}
		BlogAuthorApply latest = applyMapper
			.selectOne(new LambdaQueryWrapper<BlogAuthorApply>().eq(BlogAuthorApply::getUserId, userId)
				.orderByDesc(BlogAuthorApply::getCreateTime)
				.last("LIMIT 1"));
		if (latest == null) {
			return null;
		}
		return toDto(latest);
	}

	@Override
	public PageResult<AuthorApplyDTO> page(Long current, Long size, Integer applyStatus, String keyword) {
		Page<BlogAuthorApply> page = new Page<>(current, size);
		LambdaQueryWrapper<BlogAuthorApply> wrapper = new LambdaQueryWrapper<>();
		if (applyStatus != null) {
			wrapper.eq(BlogAuthorApply::getApplyStatus, applyStatus);
		}
		if (StringUtils.hasText(keyword)) {
			wrapper.and(w -> w.like(BlogAuthorApply::getUsername, keyword)
				.or()
				.like(BlogAuthorApply::getNickname, keyword)
				.or()
				.like(BlogAuthorApply::getReason, keyword));
		}
		wrapper.orderByDesc(BlogAuthorApply::getCreateTime);

		IPage<BlogAuthorApply> pr = applyMapper.selectPage(page, wrapper);
		List<AuthorApplyDTO> list = pr.getRecords().stream().map(this::toDto).collect(Collectors.toList());
		return new PageResult<>(list, pr.getTotal(), pr.getCurrent(), pr.getSize());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean handle(Long id, Integer applyStatus, String remark) {
		if (applyStatus == null || (applyStatus != STATUS_APPROVED && applyStatus != STATUS_REJECTED)) {
			throw new BusinessException("非法处理结果");
		}
		BlogAuthorApply existing = applyMapper.selectById(id);
		if (existing == null) {
			throw new BusinessException("申请不存在");
		}
		if (existing.getApplyStatus() != null && existing.getApplyStatus() != STATUS_PENDING) {
			throw new BusinessException("仅待处理申请可操作");
		}
		existing.setApplyStatus(applyStatus);
		existing.setHandleUser(SecurityContextUtil.getCurrentUsername());
		existing.setHandleTime(LocalDateTime.now());
		existing.setHandleRemark(remark);
		applyMapper.updateById(existing);
		return Boolean.TRUE;
	}

	private AuthorApplyDTO toDto(BlogAuthorApply entity) {
		AuthorApplyDTO dto = new AuthorApplyDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

}
