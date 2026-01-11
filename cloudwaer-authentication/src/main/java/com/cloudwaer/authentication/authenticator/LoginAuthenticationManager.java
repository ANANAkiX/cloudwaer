package com.cloudwaer.authentication.authenticator;


import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.enums.LoginType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登录认证调度器。
 * 说明：
 * - 收集所有 LoginAuthenticator 实现。
 * - 按 LoginType 选择匹配的认证器并执行认证。
 * - 如未找到匹配实现，抛出非法参数异常（可按需替换为业务异常）。
 */
@Component
@AllArgsConstructor
public class LoginAuthenticationManager {

    private final List<LoginAuthenticator> authenticators;

    /**
     * 执行认证，返回主体标识（如用户名/邮箱/手机号等）。
     */
    public LoginResponseDTO authenticate(LoginRequestDTO request) {
        LoginType loginType = LoginType.valueOf(request.getLoginType());
        for (LoginAuthenticator a : authenticators) {
            if (a.supports(loginType)) {
                return a.authenticate(request);
            }
        }
        throw new IllegalArgumentException("unsupported loginType");
    }
}
