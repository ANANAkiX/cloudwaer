package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.OauthBindDTO;
import com.cloudwaer.admin.api.dto.OauthBindingDTO;
import com.cloudwaer.admin.api.dto.OauthProviderDTO;
import com.cloudwaer.admin.api.dto.OauthRegisterDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.serve.entity.SysOauthProvider;
import com.cloudwaer.admin.serve.service.SysOauthProviderService;
import com.cloudwaer.admin.serve.service.SysUserOauthService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 第三方登录内部接口（供 authentication 服务 Feign 调用）
 * <p>
 * 全部为 @PermissionCode(enable=false)：仅允许携带 X-Internal-Call: true 的内部调用。
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin第三方登录", description = "第三方登录供应商配置与账号绑定内部接口")
public class AdminOauthController {

	@Autowired
	private SysOauthProviderService sysOauthProviderService;

	@Autowired
	private SysUserOauthService sysUserOauthService;

	/**
	 * 获取启用中的供应商配置（含 Secret，内部接口）
	 */
	@GetMapping("/oauth/provider")
	@PermissionCode(value = "admin:oauth:provider", enable = false)
	@Operation(summary = "获取供应商配置", description = "内部接口：按类型取启用中的第三方登录供应商配置（含 Secret）")
	public Result<OauthProviderDTO> getOauthProvider(@RequestParam("providerType") String providerType) {
		SysOauthProvider provider = sysOauthProviderService.getEnabledByType(providerType);
		if (provider == null) {
			return Result.success(null);
		}
		OauthProviderDTO dto = new OauthProviderDTO();
		BeanUtils.copyProperties(provider, dto);
		return Result.success(dto);
	}

	/**
	 * 按第三方身份查询已绑定的系统用户
	 */
	@GetMapping("/user/oauth")
	@PermissionCode(value = "admin:user:oauth", enable = false)
	@Operation(summary = "查询第三方绑定用户", description = "内部接口：按供应商类型与第三方用户ID查询已绑定系统用户")
	public Result<UserDTO> getUserByOauth(@RequestParam("providerType") String providerType,
			@RequestParam("providerUserId") String providerUserId) {
		return Result.success(sysUserOauthService.getUserByOauth(providerType, providerUserId));
	}

	/**
	 * 第三方登录自动注册并绑定
	 */
	@PostMapping("/user/oauth/register")
	@PermissionCode(value = "admin:user:oauth:register", enable = false)
	@Operation(summary = "第三方登录自动注册", description = "内部接口：创建系统用户（访客角色）并绑定第三方账号")
	public Result<UserDTO> registerOauthUser(@RequestBody OauthRegisterDTO dto) {
		return Result.success(sysUserOauthService.registerOauthUser(dto));
	}

	/**
	 * 绑定第三方账号到已有用户
	 */
	@PostMapping("/user/oauth/bind")
	@PermissionCode(value = "admin:user:oauth:bind", enable = false)
	@Operation(summary = "绑定第三方账号", description = "内部接口：将第三方账号绑定到指定系统用户")
	public Result<Boolean> bindOauthUser(@RequestBody OauthBindDTO dto) {
		return Result.success(sysUserOauthService.bindOauthUser(dto));
	}

	/**
	 * 解绑
	 */
	@PostMapping("/user/oauth/unbind")
	@PermissionCode(value = "admin:user:oauth:unbind", enable = false)
	@Operation(summary = "解绑第三方账号", description = "内部接口：解除系统用户与指定供应商的绑定")
	public Result<Boolean> unbindOauthUser(@RequestParam("userId") Long userId,
			@RequestParam("providerType") String providerType) {
		return Result.success(sysUserOauthService.unbindOauthUser(userId, providerType));
	}

	/**
	 * 查询用户的第三方绑定列表
	 */
	@GetMapping("/user/oauth/list")
	@PermissionCode(value = "admin:user:oauth:list", enable = false)
	@Operation(summary = "查询第三方绑定列表", description = "内部接口：查询指定用户的第三方绑定列表")
	public Result<List<OauthBindingDTO>> listOauthBindings(@RequestParam("userId") Long userId) {
		return Result.success(sysUserOauthService.listByUser(userId));
	}

}
