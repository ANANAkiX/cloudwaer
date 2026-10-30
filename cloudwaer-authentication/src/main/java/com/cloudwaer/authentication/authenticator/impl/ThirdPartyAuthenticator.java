package com.cloudwaer.authentication.authenticator.impl;

import com.cloudwaer.authentication.authenticator.LoginAuthenticator;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.enums.LoginType;
import com.cloudwaer.authentication.service.OauthService;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 第三方 OAuth 登录认证器（GitHub 等）。
 * <p>
 * 说明：
 * <ul>
 * <li>仅当 LoginType 为 THIRD_PARTY 时生效</li>
 * <li>用授权码（code）+ state 换票取第三方身份，已绑定直接登录，未绑定自动注册并绑定</li>
 * </ul>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
@AllArgsConstructor
public class ThirdPartyAuthenticator implements LoginAuthenticator {

	private OauthService oauthService;

	@Override
	public boolean supports(LoginType type) {
		return type == LoginType.THIRD_PARTY;
	}

	@Override
	public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
		String provider = loginRequestDTO.getProvider();
		String code = loginRequestDTO.getCode();
		String state = loginRequestDTO.getState();
		if (provider == null || provider.isBlank() || code == null || code.isBlank() || state == null
				|| state.isBlank()) {
			throw new BusinessException("第三方登录参数不完整");
		}
		try {
			return oauthService.loginWithCode(provider, code, state, loginRequestDTO.getClientId());
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("第三方登录异常: {}", e.getMessage(), e);
			throw new BusinessException("第三方登录失败");
		}
	}

}
