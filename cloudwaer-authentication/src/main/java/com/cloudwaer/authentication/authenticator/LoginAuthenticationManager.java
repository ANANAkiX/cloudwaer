package com.cloudwaer.authentication.authenticator;

import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.enums.LoginType;
import com.cloudwaer.common.core.result.ResultCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登录认证调度器。 说明： - 收集所有 LoginAuthenticator 实现。 - 按 LoginType 选择匹配的认证器并执行认证。 -
 * 如未找到匹配实现，抛出非法参数异常（可按需替换为业务异常）。
 */
@Component
@AllArgsConstructor
public class LoginAuthenticationManager {

	private final List<LoginAuthenticator> authenticators;

	/**
	 * 执行认证，返回主体标识（如用户名/邮箱/手机号等）。
	 */
	public LoginResponseDTO authenticate(LoginRequestDTO request) {
		LoginType loginType;
		try {
			loginType = LoginType.valueOf(request.getLoginType());
		}
		catch (Exception e) {
			throw new com.cloudwaer.common.core.exception.BusinessException(ResultCode.PARAM_ERROR);
		}
		for (LoginAuthenticator loginAuthenticator : authenticators) {
			if (loginAuthenticator.supports(loginType)) {
				return loginAuthenticator.authenticate(request);
			}
		}
		throw new com.cloudwaer.common.core.exception.BusinessException(ResultCode.PARAM_ERROR);

	}

}
