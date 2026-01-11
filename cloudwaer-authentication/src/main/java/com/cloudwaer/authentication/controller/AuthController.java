package com.cloudwaer.authentication.controller;

import com.cloudwaer.admin.api.dto.RouteDTO;
import com.cloudwaer.admin.api.dto.UserDTO;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.service.AuthService;
import com.cloudwaer.common.core.annotation.PermitAll;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AuthService authService;

    /**
     * 校验当前请求中的Token是否有效
     */
    @GetMapping("/token/valid")
    @Operation(summary = "校验Token是否有效", description = "根据请求头中的Token UUID在Redis中校验是否存在")
    public Result<Boolean> tokenValid() {
        return Result.success(authService.tokenValid());
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回JWT Token")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO login = authService.login(loginRequest);
        if (login == null) {
            log.error("登录失败");
            return Result.fail(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("token", login.getToken()); // 只返回UUID包装的Token
        log.info("用户登录成功: username={}", login.getUsername());
        return Result.success(result);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，清除Redis中的Token")
    public Result<?> logout() {
        return Result.success(authService.logout());

    }
}


