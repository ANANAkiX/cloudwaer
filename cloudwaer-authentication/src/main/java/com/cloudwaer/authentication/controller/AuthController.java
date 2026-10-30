package com.cloudwaer.authentication.controller;

import com.cloudwaer.admin.api.dto.OauthBindingDTO;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.dto.OauthLoginRequestDTO;
import com.cloudwaer.authentication.dto.OauthRegisterRequestDTO;
import com.cloudwaer.authentication.dto.RegisterRequestDTO;
import com.cloudwaer.authentication.exception.OauthNeedRegisterException;
import com.cloudwaer.authentication.service.AuthService;
import com.cloudwaer.authentication.service.OauthService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

	@Autowired
	private OauthService oauthService;

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

	/**
	 * 第三方登录授权地址（登录意图，匿名）
	 */
	@GetMapping("/oauth/authorize-url")
	@PermissionCode(value = "auth:oauth:authorize", enable = false)
	@Operation(summary = "获取第三方授权地址", description = "匿名可调：返回供应商授权跳转地址（含 state）")
	public Result<Map<String, Object>> oauthAuthorizeUrl(@RequestParam("providerType") String providerType,
			@RequestParam(value = "clientId", required = false) String clientId,
			@RequestParam("returnTo") String returnTo) {
		return Result.success(oauthService.buildLoginAuthorizeUrl(providerType, clientId, returnTo));
	}

	/**
	 * 第三方账号绑定授权地址（需已登录）
	 */
	@GetMapping("/oauth/bind-url")
	@PermissionCode(value = "auth:oauth:bind-url", enable = false)
	@Operation(summary = "获取第三方绑定授权地址", description = "需已登录：返回绑定流程的供应商授权跳转地址")
	public Result<Map<String, Object>> oauthBindUrl(@RequestParam("providerType") String providerType,
			@RequestParam(value = "clientId", required = false) String clientId,
			@RequestParam("returnTo") String returnTo) {
		// /auth/** 在权限过滤器排除名单内，@RequireLogin 不生效，这里显式校验登录态
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
		}
		return Result.success(oauthService.buildBindAuthorizeUrl(providerType, clientId, returnTo, userId));
	}

	/**
	 * 供应商回调中转：302 回前端回跳地址（携 code+state），此处不换票
	 */
	@GetMapping("/oauth/callback")
	@PermissionCode(value = "auth:oauth:callback", enable = false)
	@Operation(summary = "第三方授权回调中转", description = "浏览器可达的 redirect_uri，校验 state 后 302 回前端")
	public ResponseEntity<Void> oauthCallback(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "error", required = false) String error) {
		String location = oauthService.resolveRelayLocation(code, state, error);
		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.LOCATION, location)
			.header(HttpHeaders.CACHE_CONTROL, "no-store")
			.build();
	}

	/**
	 * 第三方授权码登录：已绑定直接登录；未绑定返回 NEED_REGISTER + 注册票据（引导注册后绑定）
	 */
	@PostMapping("/oauth/login")
	@PermissionCode(value = "auth:oauth:login", enable = false)
	@Operation(summary = "第三方授权码登录", description = "已绑定直接登录；未绑定返回 NEED_REGISTER 与注册票据，由注册页提交后注册并绑定")
	public Result<Map<String, Object>> oauthLogin(@Validated @RequestBody OauthLoginRequestDTO oauthRequest) {
		try {
			LoginResponseDTO login = authService.oauthLogin(oauthRequest);
			if (login == null) {
				log.error("第三方登录失败");
				return Result.fail(ResultCode.LOGIN_ERROR_ACCOUNT.getCode(),
						ResultCode.LOGIN_ERROR_ACCOUNT.getMessage());
			}
			Map<String, Object> result = new HashMap<>();
			result.put("status", "OK");
			result.put("token", login.getToken());
			result.put("username", login.getUsername());
			log.info("第三方登录成功: username={}", login.getUsername());
			return Result.success(result);
		}
		catch (OauthNeedRegisterException e) {
			Map<String, Object> result = new HashMap<>();
			result.put("status", "NEED_REGISTER");
			result.put("registerTicket", e.getRegisterTicket());
			Map<String, Object> profile = new HashMap<>();
			profile.put("nickname", e.getNickname());
			profile.put("email", e.getEmail());
			profile.put("avatar", e.getAvatar());
			result.put("profile", profile);
			log.info("第三方账号未绑定，进入注册流程");
			return Result.success(result);
		}
	}

	/**
	 * 第三方登录后注册并绑定（用注册票据，成功即登录）
	 */
	@PostMapping("/oauth/register")
	@PermissionCode(value = "auth:oauth:register", enable = false)
	@Operation(summary = "第三方登录后注册并绑定", description = "用注册票据建号（访客角色）并绑定授权的第三方账号，成功后直接登录")
	public Result<Map<String, Object>> oauthRegister(@Validated @RequestBody OauthRegisterRequestDTO registerRequest) {
		LoginResponseDTO login = oauthService.registerWithTicket(registerRequest);
		Map<String, Object> result = new HashMap<>();
		result.put("status", "OK");
		result.put("token", login.getToken());
		result.put("username", login.getUsername());
		log.info("第三方注册并绑定成功: username={}", login.getUsername());
		return Result.success(result);
	}

	/**
	 * 第三方账号绑定（需已登录）
	 */
	@PostMapping("/oauth/bind")
	@PermissionCode(value = "auth:oauth:bind", enable = false)
	@Operation(summary = "绑定第三方账号", description = "需已登录：用授权码将第三方账号绑定到当前用户")
	public Result<Boolean> oauthBind(@Validated @RequestBody OauthLoginRequestDTO oauthRequest) {
		// /auth/** 在权限过滤器排除名单内，显式校验登录态
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
		}
		return Result.success(oauthService.bindWithCode(oauthRequest.getProviderType(), oauthRequest.getCode(),
				oauthRequest.getState(), userId));
	}

	/**
	 * 解绑第三方账号（需已登录）
	 */
	@PostMapping("/oauth/unbind")
	@PermissionCode(value = "auth:oauth:unbind", enable = false)
	@Operation(summary = "解绑第三方账号", description = "需已登录：解除当前用户与指定供应商的绑定")
	public Result<Boolean> oauthUnbind(@RequestParam("providerType") String providerType) {
		// /auth/** 在权限过滤器排除名单内，显式校验登录态
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
		}
		return Result.success(oauthService.unbind(providerType, userId));
	}

	/**
	 * 当前用户的第三方绑定列表（需已登录）
	 */
	@GetMapping("/oauth/bindings")
	@PermissionCode(value = "auth:oauth:bindings", enable = false)
	@Operation(summary = "查询第三方绑定列表", description = "需已登录：返回当前用户的第三方账号绑定列表")
	public Result<List<OauthBindingDTO>> oauthBindings() {
		// /auth/** 在权限过滤器排除名单内，显式校验登录态
		Long userId = SecurityContextUtil.getCurrentUserId();
		if (userId == null) {
			return Result.fail(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
		}
		return Result.success(oauthService.listBindings(userId));
	}

}
