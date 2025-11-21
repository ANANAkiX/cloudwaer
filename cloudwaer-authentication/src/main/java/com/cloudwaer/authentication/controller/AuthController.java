package com.cloudwaer.authentication.controller;

import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.common.core.annotation.PermitAll;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
@PermitAll  // 认证接口不需要权限验证
public class AuthController {

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;


    /**
     * 校验当前请求中的Token是否有效
     */
    @GetMapping("/token/valid")
    @Operation(summary = "校验Token是否有效", description = "根据请求头中的Token UUID在Redis中校验是否存在")
    public Result<Boolean> tokenValid() {
        try {
            String tokenUuid = SecurityContextUtil.getCurrentToken();
            boolean valid = tokenService.validateToken(tokenUuid);
            return Result.success(valid);
        } catch (Exception e) {
            log.error("校验Token异常", e);
            return Result.success(false);
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回JWT Token")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequestDTO loginRequest) {
        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            
            log.info("用户尝试登录: username={}", username);
            
            // 获取用户信息
            Result<UserDTO> userResult;
            try {
                userResult = adminFeignClient.getUserByUsername(username);
            } catch (Exception e) {
                log.error("调用admin服务失败: {}", e.getMessage(), e);
                return Result.fail(ResultCode.LOGIN_ERROR.getCode(), "服务暂时不可用，请稍后重试");
            }

            
            if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
                log.warn("用户不存在或查询失败: username={}, result={}", username, userResult);
                return Result.fail(ResultCode.LOGIN_ERROR);
            }
            
            UserDTO user = userResult.getData();
            
            // 验证密码（实际应该使用加密后的密码进行比对）
            if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
                log.warn("密码验证失败: username={}", username);
                return Result.fail(ResultCode.LOGIN_ERROR);
            }

            // 获取用户路由和权限代码
            List<RouteDTO> routes = new ArrayList<>();
            List<String> permissionCodes = new ArrayList<>();
            try {
                // 获取路由
                Result<List<RouteDTO>> routesResult = adminFeignClient.getRoutesByUserId(user.getId());
                if (routesResult != null && routesResult.getCode() == 200 && routesResult.getData() != null) {
                    routes = routesResult.getData();
                }

                // 获取权限代码
                Result<List<String>> permissionsResult = adminFeignClient.getPermissionsByUserId(user.getId());
                if (permissionsResult != null && permissionsResult.getCode() == 200 && permissionsResult.getData() != null) {
                    permissionCodes = permissionsResult.getData();
                }
            } catch (Exception e) {
                log.warn("获取用户路由或权限失败: {}", e.getMessage());
                // 不影响登录，继续返回结果
            }

            // 生成Token UUID（包装JWT，存储用户信息、角色、权限到Redis）
            String tokenUuid = tokenService.generateAndStoreToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRoleIds(),
                    permissionCodes
            );

            if (tokenUuid == null) {
                log.error("Token生成失败: username={}", username);
                return Result.fail(ResultCode.LOGIN_ERROR.getCode(), "Token生成失败，请稍后重试");
            }

            // 返回结果（只返回Token UUID）
            Map<String, Object> result = new HashMap<>();
            result.put("token", tokenUuid); // 只返回UUID包装的Token

            log.info("用户登录成功: username={}", username);
            return Result.success(result);
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            return Result.fail(ResultCode.LOGIN_ERROR.getCode(), "登录失败，请稍后重试");
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，清除Redis中的Token")
    public Result<?> logout() {
        try {
            // 从请求头获取Token UUID
            String tokenUuid = SecurityContextUtil.getCurrentToken();
            if (tokenUuid != null) {
                // 删除Redis中的Token
                tokenService.deleteToken(tokenUuid);
                log.info("用户登出成功: tokenUuid={}", tokenUuid);
            }
            return Result.success();
        } catch (Exception e) {
            log.error("登出异常: {}", e.getMessage(), e);
            return Result.success(); // 即使出错也返回成功，避免影响用户体验
        }
    }
}


