package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.entity.User;
import com.cloudwaer.admin.serve.entity.UserRole;
import com.cloudwaer.admin.serve.mapper.UserMapper;
import com.cloudwaer.admin.serve.mapper.UserRoleMapper;
import com.cloudwaer.admin.serve.service.UserService;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author cloudwaer
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDTO getUserByUsername(String username) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getUsername, username);
		User user = this.getOne(wrapper);
		if (user == null) {
			return null;
		}
		return toUserDTO(user, false);
	}

	/**
	 * 根据邮箱获取用户
	 * @param email 邮箱
	 * @return 用户DTO
	 */
	@Override
	public UserDTO getUserByEmail(String email) {
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(User::getEmail, email);
		User user = this.getOne(wrapper);
		if (user == null) {
			return null;
		}
		return toUserDTO(user, false);
	}

	@Override
	public List<UserDTO> getAllUsers() {
		List<User> users = this.list();
		return users.stream().map(user -> toUserDTO(user, true)).collect(Collectors.toList());
	}

	@Override
	public UserDTO getUserById(Long id) {
		User user = this.getById(id);
		if (user == null) {
			return null;
		}
		return toUserDTO(user, true);
	}

	@Override
	public PageResult<UserDTO> getUserPage(PageDTO pageDTO) {
		// 创建分页对象
		Page<User> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());

		// 构建查询条件（复合搜索：用户名、昵称、邮箱、手机号）
		LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
		String keyword = pageDTO.getKeywordTrimmed();
		if (keyword != null) {
			wrapper.and(w -> w.like(User::getUsername, keyword)
				.or()
				.like(User::getNickname, keyword)
				.or()
				.like(User::getEmail, keyword)
				.or()
				.like(User::getPhone, keyword));
		}

		// 分页查询
		IPage<User> pageResult = this.page(page, wrapper);

		// 转换为DTO并填充角色信息
		List<UserDTO> userDTOList = pageResult.getRecords()
			.stream()
			.map(user -> toUserDTO(user, true))
			.collect(Collectors.toList());

		return new PageResult<>(userDTOList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
	}

	@Override
	public Boolean saveUser(UserDTO userDTO) {
		User user = new User();
		BeanUtils.copyProperties(userDTO, user);

		// 如果提供了密码，进行加密
		if (userDTO.getPassword() != null && passwordEncoder != null) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		boolean result = this.save(user);

		// 保存角色关联
		if (result && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
			assignRoles(user.getId(), userDTO.getRoleIds());
		}

		return result;
	}

	@Override
	public Boolean updateUser(UserDTO userDTO) {
		User user = new User();
		BeanUtils.copyProperties(userDTO, user);

		// 如果提供了新密码，进行加密
		if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty() && passwordEncoder != null) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}
		else {
			// 不更新密码
			user.setPassword(null);
		}

		boolean result = this.updateById(user);

		// 更新角色关联
		if (result && userDTO.getRoleIds() != null) {
			assignRoles(userDTO.getId(), userDTO.getRoleIds());
		}

		return result;
	}

	@Override
	public Boolean deleteUser(Long id) {
		// 物理删除用户角色关联（中间表不使用逻辑删除）
		userRoleMapper.deleteByUserId(id);

		// 删除用户（使用逻辑删除）
		return this.removeById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean assignRoles(Long userId, List<Long> roleIds) {
		// 物理删除原有角色关联（中间表不使用逻辑删除，避免唯一索引冲突）
		userRoleMapper.deleteByUserId(userId);

		// 添加新的角色关联
		if (roleIds != null && !roleIds.isEmpty()) {
			for (Long roleId : roleIds) {
				UserRole userRole = new UserRole();
				userRole.setUserId(userId);
				userRole.setRoleId(roleId);
				userRoleMapper.insert(userRole);
			}
		}

		return true;
	}

	private UserDTO toUserDTO(User user, boolean maskPassword) {
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(user, userDTO);
		if (maskPassword) {
			userDTO.setPassword(null);
		}
		userDTO.setRoleIds(RelationQuerySupport.queryRoleIdsByUserId(userRoleMapper, user.getId()));
		return userDTO;
	}

}
