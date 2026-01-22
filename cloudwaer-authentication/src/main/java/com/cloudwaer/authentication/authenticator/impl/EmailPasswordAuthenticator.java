package com.cloudwaer.authentication.authenticator.impl;

import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.authentication.authenticator.LoginAuthenticator;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.enums.LoginType;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class EmailPasswordAuthenticator implements LoginAuthenticator {

	private AdminFeignClient adminFeignClient;

	private PasswordEncoder passwordEncoder;

	private TokenService tokenService;

	@Override
	public boolean supports(LoginType type) {
		return type == LoginType.EMAIL_PASSWORD;
	}

	@Override
	public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
		String email = loginRequestDTO.getEmail();
		String password = loginRequestDTO.getPassword();

		try {
			log.info("用户尝试登录: email={}", email);

			UserDTO user = getUserByEmail(email);
			String username = user.getUsername();
			validatePassword(user, password, ResultCode.LOGIN_ERROR_EMAIL);

			List<String> permissionCodes = tryGetPermissionCodes(user.getId());
			String tokenUuid = tokenService.generateAndStoreToken(user.getId(), username, user.getRoleIds(),
					permissionCodes);
			if (tokenUuid == null) {
				log.error("Token生成失败: email={}", email);
				throw new BusinessException(ResultCode.FAIL);
			}

			log.info("用户登录成功: username={}", username);
			return LoginResponseDTO.builder().token(tokenUuid).username(user.getUsername()).build();
		}
		catch (BusinessException exception) {
			log.error("登录异常: {}", exception.getMessage(), exception);
			throw new BusinessException(ResultCode.LOGIN_ERROR_EMAIL);
		}
		catch (Exception e) {
			log.error("登录异常: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.FAIL);
		}
	}

	private UserDTO getUserByEmail(String email) {
		Result<UserDTO> userResult;
		try {
			userResult = adminFeignClient.getUserByEmail(email);
		}
		catch (Exception e) {
			log.error("调用admin服务失败: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.NOT_SERVE_FOUND);
		}

		if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
			log.error("用户不存在或查询失败: email={}, result={}", email, userResult);
			throw new BusinessException(ResultCode.LOGIN_ERROR_EMAIL);
		}
		return userResult.getData();
	}

	private void validatePassword(UserDTO user, String plainPassword, ResultCode failCode) {
		if (user.getPassword() == null || !passwordEncoder.matches(plainPassword, user.getPassword())) {
			log.error("密码验证失败: userId={}", user.getId());
			throw new BusinessException(failCode);
		}
	}

	private List<String> tryGetPermissionCodes(Long userId) {
		try {
			Result<List<String>> permissionsResult = adminFeignClient.getPermissionsByUserId(userId);
			if (permissionsResult != null && permissionsResult.getCode() == 200
					&& permissionsResult.getData() != null) {
				return permissionsResult.getData();
			}
		}
		catch (Exception e) {
			log.warn("获取用户路由或权限失败: {}", e.getMessage());
			// 不影响登录，继续返回结果
		}
		return new ArrayList<>();
	}

}
