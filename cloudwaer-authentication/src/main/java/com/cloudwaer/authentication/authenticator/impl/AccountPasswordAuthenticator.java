package com.cloudwaer.authentication.authenticator.impl;

import com.cloudwaer.admin.api.dto.RouteDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 效基于账号+密码的登录认证器。
 * * 说明：
 * * - 仅当 LoginType 为 ACCOUNT_PASSWORD 时生。
 * - 通过 admin 内部接口获取用户凭证（密码哈希）并做 BCrypt 比对。
 * - 认证成功返回主体标识（这里返回账号 account），失败抛出 BizException。
 */

@Component
@Slf4j
public class AccountPasswordAuthenticator implements LoginAuthenticator {
    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(LoginType type) {
        return type == LoginType.ACCESS_PASSWORD;
    }


    @Autowired
    private TokenService tokenService;

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        try {
            String username = loginRequestDTO.getUsername();
            String password = loginRequestDTO.getPassword();

            log.info("用户尝试登录: username={}", username);

            // 获取用户信息
            Result<UserDTO> userResult;
            try {
                userResult = adminFeignClient.getUserByUsername(username);
            } catch (Exception e) {
                log.error("调用admin服务失败: {}", e.getMessage(), e);
                throw new BusinessException(ResultCode.NOT_SERVE_FOUND);
            }


            if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
                log.error("用户不存在或查询失败: username={}, result={}", username, userResult);
                throw new BusinessException(ResultCode.LOGIN_ERROR);
            }

            UserDTO user = userResult.getData();

            // 验证密码（实际应该使用加密后的密码进行比对）
            if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
                log.error("密码验证失败: username={}", username);
                throw new BusinessException(ResultCode.LOGIN_ERROR);
            }

            // 获取用户路由和权限代码
            List<String> permissionCodes = new ArrayList<>();
            try {

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
            String tokenUuid = tokenService.generateAndStoreToken(user.getId(), user.getUsername(), user.getRoleIds(), permissionCodes);

            if (tokenUuid == null) {
                log.error("Token生成失败: username={}", username);
                throw new BusinessException(ResultCode.FAIL);
            }
            log.info("用户登录成功: username={}", username);
            return LoginResponseDTO.builder().token(tokenUuid).username(username).build();
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FAIL);
        }
    }
}
