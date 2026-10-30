package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.admin.api.dto.OauthBindDTO;
import com.cloudwaer.admin.api.dto.OauthBindingDTO;
import com.cloudwaer.admin.api.dto.OauthRegisterDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.entity.Role;
import com.cloudwaer.admin.serve.entity.SysUserOauth;
import com.cloudwaer.admin.serve.entity.User;
import com.cloudwaer.admin.serve.mapper.RoleMapper;
import com.cloudwaer.admin.serve.mapper.SysUserOauthMapper;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 第三方账号绑定服务：查询绑定、绑定/解绑、第三方登录自动注册
 *
 * @author cloudwaer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserOauthService {

	private final SysUserOauthMapper userOauthMapper;

	private final UserService userService;

	private final RoleMapper roleMapper;

	private final PasswordEncoder passwordEncoder;

	/**
	 * 按第三方身份查询已绑定的系统用户（含角色），未绑定返回 null
	 */
	public UserDTO getUserByOauth(String providerType, String providerUserId) {
		SysUserOauth binding = userOauthMapper
			.selectOne(new LambdaQueryWrapper<SysUserOauth>().eq(SysUserOauth::getProviderType, providerType)
				.eq(SysUserOauth::getProviderUserId, providerUserId)
				.last("LIMIT 1"));
		if (binding == null) {
			return null;
		}
		return userService.getUserById(binding.getUserId());
	}

	/**
	 * 第三方登录自动注册并绑定（分配访客角色）
	 */
	@Transactional(rollbackFor = Exception.class)
	public UserDTO registerOauthUser(OauthRegisterDTO dto) {
		String username = dto.getUsername() == null ? "" : dto.getUsername().trim();
		if (username.isEmpty()) {
			throw new BusinessException("用户名不能为空");
		}
		// 用户名由用户在注册页自填：冲突直接报错让用户更换，不自动加后缀
		if (userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0) {
			throw new BusinessException("用户名已存在，请更换");
		}
		Long userId;
		try {
			Role guestRole = roleMapper
				.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "GUEST").last("LIMIT 1"));
			if (guestRole == null) {
				throw new BusinessException("访客角色未初始化，请先导入 db/cloudwaer_blog.sql");
			}

			User user = new User();
			user.setUsername(username);
			// 随机密码：第三方登录用户不走密码登录，需要密码登录时走重置流程
			user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString().replace("-", "")));
			user.setNickname(dto.getNickname() != null && !dto.getNickname().isBlank() ? dto.getNickname()
					: (dto.getProviderUsername() != null && !dto.getProviderUsername().isBlank()
							? dto.getProviderUsername() : username));
			user.setEmail(dto.getEmail());
			user.setAvatar(
					dto.getAvatar() != null && !dto.getAvatar().isBlank() ? dto.getAvatar() : dto.getAvatarUrl());
			if (!userService.save(user)) {
				throw new BusinessException("创建用户失败");
			}
			userId = user.getId();
			userService.assignRoles(userId, List.of(guestRole.getId()));
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("第三方登录自动注册失败: {}", e.getMessage(), e);
			throw new BusinessException("自动注册失败");
		}

		doBind(userId, dto.getProviderType(), dto.getProviderUserId(), dto.getProviderUsername(), dto.getEmail(),
				dto.getAvatarUrl());
		log.info("第三方登录自动注册成功: username={}, userId={}, provider={}, providerUserId={}", username, userId,
				dto.getProviderType(), dto.getProviderUserId());
		return userService.getUserById(userId);
	}

	/**
	 * 绑定第三方账号到已有用户
	 */
	@Transactional(rollbackFor = Exception.class)
	public Boolean bindOauthUser(OauthBindDTO dto) {
		UserDTO user = userService.getUserById(dto.getUserId());
		if (user == null) {
			throw new BusinessException("用户不存在");
		}
		Long boundUserId = findUserIdByProvider(dto.getProviderType(), dto.getProviderUserId());
		if (boundUserId != null && !boundUserId.equals(dto.getUserId())) {
			String boundUsername = resolveUsername(boundUserId);
			throw new BusinessException(
					"当前第三方账号已被账号「" + boundUsername + "」绑定，请登录「" + boundUsername + "」后解绑，或更换其它第三方账号");
		}
		if (boundUserId != null) {
			return true;
		}
		SysUserOauth existing = userOauthMapper
			.selectOne(new LambdaQueryWrapper<SysUserOauth>().eq(SysUserOauth::getUserId, dto.getUserId())
				.eq(SysUserOauth::getProviderType, dto.getProviderType())
				.last("LIMIT 1"));
		if (existing != null) {
			throw new BusinessException("该用户已绑定此供应商的账号");
		}
		doBind(dto.getUserId(), dto.getProviderType(), dto.getProviderUserId(), dto.getProviderUsername(),
				dto.getEmail(), dto.getAvatarUrl());
		log.info("第三方账号绑定成功: userId={}, provider={}, providerUserId={}", dto.getUserId(), dto.getProviderType(),
				dto.getProviderUserId());
		return true;
	}

	/**
	 * 解绑
	 */
	public Boolean unbindOauthUser(Long userId, String providerType) {
		// 物理删除：逻辑删除的行会占住唯一索引，导致换绑/他人绑定同一第三方账号撞 Duplicate key
		int rows = userOauthMapper.deleteByUserAndProvider(userId, providerType);
		log.info("第三方账号解绑: userId={}, provider={}, rows={}", userId, providerType, rows);
		return rows > 0;
	}

	public Long findUserIdByProvider(String providerType, String providerUserId) {
		SysUserOauth binding = userOauthMapper
			.selectOne(new LambdaQueryWrapper<SysUserOauth>().eq(SysUserOauth::getProviderType, providerType)
				.eq(SysUserOauth::getProviderUserId, providerUserId)
				.last("LIMIT 1"));
		return binding == null ? null : binding.getUserId();
	}

	private String resolveUsername(Long userId) {
		try {
			UserDTO user = userService.getUserById(userId);
			if (user != null && user.getUsername() != null && !user.getUsername().isBlank()) {
				return user.getUsername();
			}
		}
		catch (Exception e) {
			log.warn("查询被占用账号用户名失败: userId={}, {}", userId, e.getMessage());
		}
		return "其他账号";
	}

	/**
	 * 查询用户的第三方绑定列表
	 */
	public List<OauthBindingDTO> listByUser(Long userId) {
		List<SysUserOauth> bindings = userOauthMapper
			.selectList(new LambdaQueryWrapper<SysUserOauth>().eq(SysUserOauth::getUserId, userId));
		return bindings.stream().map(binding -> {
			OauthBindingDTO dto = new OauthBindingDTO();
			dto.setProviderType(binding.getProviderType());
			dto.setProviderUsername(binding.getProviderUsername());
			dto.setAvatarUrl(binding.getAvatarUrl());
			return dto;
		}).collect(java.util.stream.Collectors.toList());
	}

	private void doBind(Long userId, String providerType, String providerUserId, String providerUsername, String email,
			String avatarUrl) {
		// 清理历史逻辑删除遗留（status!=1 仍占唯一索引），避免插入撞 Duplicate key
		userOauthMapper.purgeInactive(userId, providerType, providerUserId);
		SysUserOauth binding = new SysUserOauth();
		binding.setUserId(userId);
		binding.setProviderType(providerType);
		binding.setProviderUserId(providerUserId);
		binding.setProviderUsername(providerUsername);
		binding.setEmail(email);
		binding.setAvatarUrl(avatarUrl);
		userOauthMapper.insert(binding);
	}

}
