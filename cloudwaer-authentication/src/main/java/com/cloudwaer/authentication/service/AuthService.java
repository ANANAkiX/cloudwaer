package com.cloudwaer.authentication.service;

import com.cloudwaer.admin.api.dto.RegisterDTO;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.authentication.authenticator.LoginAuthenticationManager;
import com.cloudwaer.authentication.dto.LoginRequestDTO;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.dto.RegisterRequestDTO;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.service.GlobalSettingService;
import com.cloudwaer.common.core.service.PasswordTransportService;
import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

	@Autowired
	private LoginAuthenticationManager loginAuthenticationManager;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private AdminFeignClient adminFeignClient;

	@Autowired
	private GlobalSettingService globalSettingService;

	@Autowired
	private PasswordTransportService passwordTransportService;

	public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
		// 密码传输加密启用时此处解密（未启用原样返回，无痛兼容）
		loginRequestDTO.setPassword(passwordTransportService.resolvePlainPassword(loginRequestDTO.getPassword()));
		// 交给可插拔认证器完成认证，返回主体标识（用户名/邮箱/手机号等）
		return loginAuthenticationManager.authenticate(loginRequestDTO);
	}

	/**
	 * 用户注册：转发到 admin 内部接口创建用户并分配访客角色
	 */
	public Boolean register(RegisterRequestDTO registerRequest) {
		if (!globalSettingService.getBool(GlobalSettingService.KEY_REGISTER_ENABLED, true)) {
			throw new BusinessException("注册功能已关闭");
		}
		registerRequest.setPassword(passwordTransportService.resolvePlainPassword(registerRequest.getPassword()));
		RegisterDTO registerDTO = new RegisterDTO();
		BeanUtils.copyProperties(registerRequest, registerDTO);

		Result<Boolean> result;
		try {
			result = adminFeignClient.registerUser(registerDTO);
		}
		catch (Exception e) {
			log.error("注册失败，admin服务调用异常: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.NOT_SERVE_FOUND);
		}

		if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
			String message = result != null && result.getMessage() != null ? result.getMessage() : "注册失败";
			throw new BusinessException(message);
		}
		log.info("用户注册成功: username={}", registerRequest.getUsername());
		return result.getData();
	}

	/**
	 * 匿名公开设置（登录页用）：验证码开关/有效期、注册开关、已启用第三方登录（脱敏）
	 */
	public Map<String, Object> publicSettings() {
		Map<String, Object> data = new HashMap<>();
		data.put("captchaEnabled", globalSettingService.getBool(GlobalSettingService.KEY_CAPTCHA_ENABLED, false));
		data.put("captchaExpireSeconds", globalSettingService.getInt(GlobalSettingService.KEY_CAPTCHA_EXPIRE, 120));
		data.put("registerEnabled", globalSettingService.getBool(GlobalSettingService.KEY_REGISTER_ENABLED, true));
		List<Map<String, String>> oauthProviders = globalSettingService.getJson(GlobalSettingService.OAUTH_PUBLIC_KEY,
				new TypeReference<List<Map<String, String>>>() {
				}, new ArrayList<>());
		data.put("oauthProviders", oauthProviders);
		Map<String, Object> passwordTransport = new HashMap<>();
		passwordTransport.put("enabled", passwordTransportService.isEnabled());
		if (passwordTransportService.isEnabled()) {
			passwordTransport.put("publicKey", passwordTransportService.getPublicKeyPem());
		}
		data.put("passwordTransport", passwordTransport);
		return data;
	}

	public Boolean tokenValid() {
		try {
			return tokenService.validateToken(SecurityContextUtil.getCurrentToken());
		}
		catch (Exception e) {
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
			}
			else {
				log.info("tokenUuid可能为空: tokenUuid={}", tokenUuid);
				return Boolean.FALSE;
			}
		}
		catch (Exception e) {
			log.error("登出异常: {}", e.getMessage(), e);
			return Boolean.FALSE;
		}
	}

}
