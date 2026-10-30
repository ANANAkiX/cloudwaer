package com.cloudwaer.authentication.exception;

import com.cloudwaer.common.core.exception.BusinessException;
import lombok.Getter;

/**
 * 第三方登录需要先注册：授权成功但第三方账号未绑定系统账号。
 * <p>
 * 携带注册票据与资料预填信息，由 AuthController 转为 NEED_REGISTER 响应。
 *
 * @author cloudwaer
 */
@Getter
public class OauthNeedRegisterException extends BusinessException {

	private final String registerTicket;

	private final String nickname;

	private final String email;

	private final String avatar;

	public OauthNeedRegisterException(String message, String registerTicket, String nickname, String email,
			String avatar) {
		super(message);
		this.registerTicket = registerTicket;
		this.nickname = nickname;
		this.email = email;
		this.avatar = avatar;
	}

}
