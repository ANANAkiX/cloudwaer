package com.cloudwaer.admin.api.feign;

import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/user/username")
    Result<UserDTO> getUserByUsername(@RequestParam("username") String username);

    /**
     * 根据用户ID获取路由列表
     *
     * @param userId 用户ID
     * @return 路由列表
     */
    @GetMapping("/route/user")
    Result<List<RouteDTO>> getRoutesByUserId(@RequestParam("userId") Long userId);

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @GetMapping("/permission/user")
    Result<List<String>> getPermissionsByUserId(@RequestParam("userId") Long userId);

    /**
     * 获取所有权限的API映射（用于权限缓存）
     * 返回格式：Map<"GET /api/user/list", "admin:user:list">
     *
     * @return 权限映射Map
     */
    @GetMapping("/permission/api-mapping")
    Result<Map<String, String>> getPermissionApiMapping();

    /**
     * 获取所有网关路由
     *
     * @return 网关路由列表
     */
    @GetMapping("/gateway-route/list")
    Result<List<GatewayRouteDTO>> getAllGatewayRoutes();
}


