package com.cloudwaer.admin.api.feign;

import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.admin.api.dto.OauthBindDTO;
import com.cloudwaer.admin.api.dto.OauthBindingDTO;
import com.cloudwaer.admin.api.dto.OauthProviderDTO;
import com.cloudwaer.admin.api.dto.OauthRegisterDTO;
import com.cloudwaer.admin.api.dto.RegisterDTO;
import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.SendMessageDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Admin服务Feign客户端
 *
 * @author cloudwaer
 */
@FeignClient(name = "cloudwaer-admin-serve", path = "/admin")
public interface AdminFeignClient {

	/**
	 * 根据用户名获取用户信息
	 * @param username 用户名
	 * @return 用户信息
	 */
	@GetMapping("/user/username")
	Result<UserDTO> getUserByUsername(@RequestParam("username") String username);

	/**
	 * 根据用户ID获取路由列表
	 * @param userId 用户ID
	 * @return 路由列表
	 */
	@GetMapping("/route/user")
	Result<List<RouteDTO>> getRoutesByUserId(@RequestParam("userId") Long userId);

	/**
	 * 根据用户ID获取权限列表
	 * @param userId 用户ID
	 * @return 权限编码列表
	 */
	@GetMapping("/permission/user")
	Result<List<String>> getPermissionsByUserId(@RequestParam("userId") Long userId);

	/**
	 * 获取所有网关路由
	 * @return 网关路由列表
	 */
	@GetMapping("/gateway-route/list")
	Result<List<GatewayRouteDTO>> getAllGatewayRoutes();

	@GetMapping("/user/email")
	Result<UserDTO> getUserByEmail(@RequestParam("email") String email);

	/**
	 * 根据角色ID查询用户名列表（流程候选组展开）
	 */
	@GetMapping("/user/by-role-ids")
	Result<List<String>> getUsernamesByRoleIds(@RequestParam("roleIds") List<Long> roleIds);

	/**
	 * 用户注册（内部接口，由 authentication 服务转发匿名注册请求）
	 * @param registerDTO 注册信息
	 * @return 是否成功
	 */
	@PostMapping("/user/register")
	Result<Boolean> registerUser(@RequestBody RegisterDTO registerDTO);

	/**
	 * 发送站内信（内部接口，业务模块系统提示统一入口）
	 * @param dto 发送内容（收件人/标题/业务关联）
	 * @return 是否成功
	 */
	@PostMapping("/message/send-internal")
	Result<Boolean> sendMessage(@RequestBody SendMessageDTO dto);

	/**
	 * 获取第三方登录供应商配置（内部接口，含 Secret，仅限内部调用）
	 * @param providerType 供应商类型
	 * @return 供应商配置
	 */
	@GetMapping("/oauth/provider")
	Result<OauthProviderDTO> getOauthProvider(@RequestParam("providerType") String providerType);

	/**
	 * 根据第三方身份查询已绑定的系统用户（内部接口）
	 * @param providerType 供应商类型
	 * @param providerUserId 第三方用户ID
	 * @return 系统用户，未绑定返回 null data
	 */
	@GetMapping("/user/oauth")
	Result<UserDTO> getUserByOauth(@RequestParam("providerType") String providerType,
			@RequestParam("providerUserId") String providerUserId);

	/**
	 * 第三方登录自动注册并绑定（内部接口，分配访客角色）
	 * @param dto 注册+绑定信息
	 * @return 创建后的系统用户
	 */
	@PostMapping("/user/oauth/register")
	Result<UserDTO> registerOauthUser(@RequestBody OauthRegisterDTO dto);

	/**
	 * 将第三方账号绑定到已有系统用户（内部接口）
	 * @param dto 绑定信息
	 * @return 是否成功
	 */
	@PostMapping("/user/oauth/bind")
	Result<Boolean> bindOauthUser(@RequestBody OauthBindDTO dto);

	/**
	 * 解除当前用户与第三方账号的绑定（内部接口）
	 * @param userId 系统用户ID
	 * @param providerType 供应商类型
	 * @return 是否成功
	 */
	@PostMapping("/user/oauth/unbind")
	Result<Boolean> unbindOauthUser(@RequestParam("userId") Long userId,
			@RequestParam("providerType") String providerType);

	/**
	 * 查询用户的第三方绑定列表（内部接口）
	 * @param userId 系统用户ID
	 * @return 绑定列表
	 */
	@GetMapping("/user/oauth/list")
	Result<List<OauthBindingDTO>> listOauthBindings(@RequestParam("userId") Long userId);

}
