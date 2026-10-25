package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.serve.entity.User;
import com.cloudwaer.admin.serve.mapper.UserMapper;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
