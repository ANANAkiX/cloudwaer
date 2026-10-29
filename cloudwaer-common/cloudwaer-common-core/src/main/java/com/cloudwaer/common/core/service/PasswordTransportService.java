package com.cloudwaer.common.core.service;

import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 登录密码传输加密（RSA / PKCS#1 v1.5）。
 *
 * <p>
 * 开关 {@code security.password-transport.enabled} 未覆盖时默认关闭（走明文，无痛兼容）；开启后前端用公钥
 * 加密密码，服务端用私钥解密后再做 BCrypt 比对。密钥对由后台「生成密钥对」产出，存于全局设置 （public-key 下发登录页，private-key
 * 仅服务端使用），保存后需「发布设置」生效。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class PasswordTransportService {

	public static final String KEY_PT_ENABLED = "security.password-transport.enabled";

	public static final String KEY_PT_PUBLIC_KEY = "security.password-transport.public-key";

	public static final String KEY_PT_PRIVATE_KEY = "security.password-transport.private-key";

	private static final String RSA_TRANSFORM = "RSA/ECB/PKCS1Padding";

	@Autowired
	private GlobalSettingService globalSettingService;

	/**
	 * 传输加密是否启用（未覆盖默认 false）
	 */
	public boolean isEnabled() {
		return globalSettingService.getBool(KEY_PT_ENABLED, false);
	}

	/**
	 * 公钥 PEM（登录页加密用）；未配置返回 null
	 */
	public String getPublicKeyPem() {
		return globalSettingService.getRaw(KEY_PT_PUBLIC_KEY);
	}

	/**
	 * 还原明文密码：未启用直接原样返回；启用后按 RSA 密文解密（失败抛业务异常，不做明文回退）
	 */
	public String resolvePlainPassword(String rawPassword) {
		if (!isEnabled()) {
			return rawPassword;
		}
		if (!StringUtils.hasText(rawPassword)) {
			return rawPassword;
		}
		String privatePem = globalSettingService.getRaw(KEY_PT_PRIVATE_KEY);
		if (!StringUtils.hasText(privatePem)) {
			log.error("密码传输加密已启用但未配置私钥");
			throw new BusinessException("密码加密密钥未配置：请在「服务器设置 · 安全」生成 RSA 密钥对并点击「发布设置」");
		}
		try {
			PrivateKey privateKey = loadPrivateKey(privatePem);
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] plain = cipher.doFinal(Base64.getDecoder().decode(rawPassword.trim()));
			return new String(plain, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			log.warn("登录密码解密失败: {}", e.getMessage());
			throw new BusinessException("密码解密失败，请刷新页面后重试");
		}
	}

	/**
	 * 生成 RSA-2048 密钥对（PEM），供后台「生成密钥对」保存到全局设置
	 */
	public Map<String, String> generateKeyPairPem() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();
			Map<String, String> result = new LinkedHashMap<>();
			result.put("publicKey", toPem("PUBLIC KEY", keyPair.getPublic().getEncoded()));
			result.put("privateKey", toPem("PRIVATE KEY", keyPair.getPrivate().getEncoded()));
			return result;
		}
		catch (Exception e) {
			log.error("生成 RSA 密钥对失败", e);
			throw new BusinessException("生成密钥对失败");
		}
	}

	private PrivateKey loadPrivateKey(String pem) throws Exception {
		byte[] der = decodePem(pem);
		// PKCS#8（Java getEncoded 默认格式）
		return KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(der));
	}

	/**
	 * 公钥 PEM → PublicKey（X.509 SPKI）
	 */
	public PublicKey loadPublicKey(String pem) throws Exception {
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodePem(pem)));
	}

	private byte[] decodePem(String pem) {
		String normalized = pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
		return Base64.getDecoder().decode(normalized);
	}

	private String toPem(String type, byte[] der) {
		String base64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(der);
		return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----";
	}

}
