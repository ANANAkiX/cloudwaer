package com.cloudwaer.authentication.service;

import com.cloudwaer.authentication.authenticator.LoginAuthenticationManager;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Slf4j
@Service
public class AuthService {

    @Autowired
    private LoginAuthenticationManager loginAuthenticationManager;

    @Autowired
    private TokenService tokenService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // 交给可插拔认证器完成认证，返回主体标识（用户名/邮箱/手机号等）
        return loginAuthenticationManager.authenticate(loginRequestDTO);
    }

    public Boolean tokenValid() {
        try {
            return tokenService.validateToken(SecurityContextUtil.getCurrentToken());
        } catch (Exception e) {
            log.error("校验Token异常", e);
            return Boolean.FALSE;
        }
    }

    public Boolean logout() {
        try {
            // 从请求头获取Token UUID
            String tokenUuid = SecurityContextUtil.getCurrentToken();
            if (tokenUuid != null) {
                // 删除Redis中的Token
                tokenService.deleteToken(tokenUuid);
                log.info("用户登出成功: tokenUuid={}", tokenUuid);
                return Boolean.TRUE;
            } else {
                log.info("tokenUuid可能为空: tokenUuid={}", tokenUuid);
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            log.error("登出异常: {}", e.getMessage(), e);
            return Boolean.FALSE;
        }
    }
}
