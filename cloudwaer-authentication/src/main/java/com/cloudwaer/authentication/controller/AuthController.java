package com.cloudwaer.authentication.controller;

import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.dto.RegisterRequestDTO;
import com.cloudwaer.authentication.service.AuthService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证授权", description = "用户登录、登出接口")
// 认证接口不需要权限验证
public class AuthController {

	@Autowired
	private AuthService authService;

	/**
	 * 匿名公开设置（登录页/前台用）：验证码开关、注册开关、已启用第三方登录等
	 */
	@GetMapping("/public-settings")
	@PermissionCode(value = "auth:public:settings", enable = false)
	@Operation(summary = "获取公开设置", description = "匿名可读：验证码开关/有效期、注册开关、第三方登录入口（脱敏）")
	public Result<Map<String, Object>> publicSettings() {
		return Result.success(authService.publicSettings());
	}

	/**
	 * 校验当前请求中的Token是否有效
	 */
	@GetMapping("/token/valid")
	@PermissionCode(value = "auth:token:valid", enable = false)
	@Operation(summary = "校验Token是否有效", description = "根据请求头中的Token UUID在Redis中校验是否存在")
	public Result<Boolean> tokenValid() {
		return Result.success(authService.tokenValid());
	}

	/**
	 * 登录
	 */
	@PostMapping("/login")
	@PermissionCode(value = "auth:login", enable = false)
	@Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回JWT Token")
	public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequestDTO loginRequest) {
		LoginResponseDTO login = authService.login(loginRequest);
		if (login == null) {
			log.error("登录失败");
			return Result.fail(ResultCode.LOGIN_ERROR_ACCOUNT.getCode(), ResultCode.LOGIN_ERROR_ACCOUNT.getMessage());
		}
		Map<String, Object> result = new HashMap<>();
		result.put("token", login.getToken()); // 只返回UUID包装的Token
		log.info("用户登录成功: username={}", login.getUsername());
		return Result.success(result);
	}

	/**
	 * 注册
	 */
	@PostMapping("/register")
	@PermissionCode(value = "auth:register", enable = false)
	@Operation(summary = "用户注册", description = "匿名注册新用户，默认分配访客角色")
	public Result<Boolean> register(@Validated @RequestBody RegisterRequestDTO registerRequest) {
		return Result.success(authService.register(registerRequest));
	}

	/**
	 * 登出
	 */
	@PostMapping("/logout")
	@PermissionCode(value = "auth:logout", enable = false)
	@Operation(summary = "用户登出", description = "用户登出，清除Redis中的Token")
	public Result<?> logout() {
		return Result.success(authService.logout());
	}

}
