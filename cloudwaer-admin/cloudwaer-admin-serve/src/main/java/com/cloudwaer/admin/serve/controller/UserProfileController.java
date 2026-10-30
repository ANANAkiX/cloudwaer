package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.serve.entity.User;
import com.cloudwaer.admin.serve.mapper.UserMapper;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.service.PasswordTransportService;
import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 当前用户自助资料（登录即可，仅本人）
 * <p>
 * 头像使用 sys_user.avatar 字段
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/user/profile")
@Tag(name = "当前用户资料", description = "头像/邮箱/手机号等仅本人修改")
public class UserProfileController {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordTransportService passwordTransportService;

	@Autowired
	private TokenService tokenService;

	@Data
	public static class ProfileDTO {

		private String nickname;

		private String email;

		private String phone;

		private String avatar;

	}

	@Data
	public static class ProfileVO {

		private Long id;

		private String username;

		private String nickname;

		private String email;

		private String phone;

		private String avatar;

	}

	@GetMapping
	@RequireLogin
	@Operation(summary = "获取本人资料")
	public Result<ProfileVO> get() {
		User user = requireUserEntity();
		ProfileVO vo = new ProfileVO();
		vo.setId(user.getId());
		vo.setUsername(user.getUsername());
		vo.setNickname(user.getNickname());
		vo.setEmail(user.getEmail());
		vo.setPhone(user.getPhone());
		vo.setAvatar(user.getAvatar());
		return Result.success(vo);
	}

	@PutMapping
	@RequireLogin
	@Operation(summary = "更新本人资料")
	public Result<Boolean> update(@RequestBody ProfileDTO dto) {
		User user = requireUserEntity();
		if (dto.getNickname() != null) {
			user.setNickname(dto.getNickname());
		}
		if (dto.getEmail() != null) {
			user.setEmail(dto.getEmail());
		}
		if (dto.getPhone() != null) {
			user.setPhone(dto.getPhone());
		}
		if (dto.getAvatar() != null) {
			user.setAvatar(dto.getAvatar());
		}
		userMapper.updateById(user);
		// 昵称/头像被发文等场景从 Token 读取，资料变更后即时刷新
		tokenService.updateUserTokenProfile(user.getId(), user.getNickname(), user.getAvatar());
		return Result.success(Boolean.TRUE);
	}

	@Data
	public static class PasswordDTO {

		private String oldPassword;

		private String newPassword;

	}

	@PutMapping("/password")
	@RequireLogin
	@Operation(summary = "修改本人密码", description = "需验证原密码；密码传输加密启用时字段为 RSA 密文")
	public Result<Boolean> changePassword(@RequestBody PasswordDTO dto) {
		User user = requireUserEntity();
		String oldPlain = passwordTransportService.resolvePlainPassword(dto.getOldPassword());
		String newPlain = passwordTransportService.resolvePlainPassword(dto.getNewPassword());
		if (!StringUtils.hasText(oldPlain)) {
			throw new BusinessException("原密码不能为空");
		}
		if (!StringUtils.hasText(newPlain) || newPlain.length() < 6 || newPlain.length() > 32) {
			throw new BusinessException("新密码长度须在6-32位");
		}
		if (!passwordEncoder.matches(oldPlain, user.getPassword())) {
			throw new BusinessException("原密码不正确");
		}
		if (passwordEncoder.matches(newPlain, user.getPassword())) {
			throw new BusinessException("新密码不能与原密码相同");
		}
		user.setPassword(passwordEncoder.encode(newPlain));
		userMapper.updateById(user);
		log.info("用户修改密码成功: userId={}", user.getId());
		return Result.success(Boolean.TRUE);
	}

	private User requireUserEntity() {
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			throw new BusinessException("请先登录");
		}
		User user = userMapper.selectById(userId);
		if (user == null) {
			throw new BusinessException("用户不存在");
		}
		return user;
	}

}
