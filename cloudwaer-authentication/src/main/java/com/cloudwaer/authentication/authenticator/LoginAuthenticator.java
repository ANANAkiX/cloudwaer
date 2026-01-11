package com.cloudwaer.authentication.authenticator;

import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.enums.LoginType;

/**
 * 登录认证扩展点。
 * 说明：
 * - 通过实现该接口即可扩展新的登录方式（如：账号密码、邮箱验证码、手机号验证码、第三方登录等）。
 * - 每个实现类声明自己支持的 LoginType，并在 authenticate 中完成校验。
 */
public interface LoginAuthenticator {

    /**
     * 当前认证器是否支持该登录类型。
     */
    boolean supports(LoginType type);

    /**
     * 执行认证，返回主体标识（如用户名/邮箱/手机号等）。
     * 发生认证失败时，应抛出业务异常（例如 BusinessException）。
     */
    LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO);
}
