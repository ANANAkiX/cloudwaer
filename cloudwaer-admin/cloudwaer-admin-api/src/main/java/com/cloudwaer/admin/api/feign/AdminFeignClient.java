package com.cloudwaer.admin.api.feign;

import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
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

}
