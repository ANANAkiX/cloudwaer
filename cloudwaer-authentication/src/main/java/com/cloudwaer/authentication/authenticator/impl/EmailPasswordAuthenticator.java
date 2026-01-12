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
        try {
            String email = loginRequestDTO.getEmail();
            String password = loginRequestDTO.getPassword();

            log.info("用户尝试登录: email={}", email);

            // 获取用户信息
            Result<UserDTO> userResult;
            try {
                userResult = adminFeignClient.getUserByEmail(email);
            } catch (Exception e) {
                log.error("调用admin服务失败: {}", e.getMessage(), e);
                throw new BusinessException(ResultCode.NOT_SERVE_FOUND);
            }


            if (userResult == null || userResult.getCode() != 200 || userResult.getData() == null) {
                log.error("用户不存在或查询失败: email={}, result={}", email, userResult);
                throw new BusinessException(ResultCode.LOGIN_ERROR);
            }

            UserDTO user = userResult.getData();
            String username = user.getUsername();
            // 验证密码（实际应该使用加密后的密码进行比对）
            if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
                log.error("密码验证失败: email={}", email);
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
            String tokenUuid = tokenService.generateAndStoreToken(user.getId(), username, user.getRoleIds(), permissionCodes);

            if (tokenUuid == null) {
                log.error("Token生成失败: email={}", email);
                throw new BusinessException(ResultCode.FAIL);
            }
            log.info("用户登录成功: username={}", username);
            return LoginResponseDTO.builder().token(tokenUuid).username(user.getUsername()).build();
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FAIL);
        }
    }
}
