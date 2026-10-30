package com.cloudwaer.common.ws;

import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.RedisUtil;
import com.cloudwaer.common.ws.model.WsSessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * WS 握手鉴权：复用现有 Token 体系（UUID Token + Redis 校验 + clientId 绑定）。
 *
 * <p>
 * 连接地址：{@code /ws?token=xxx&clientId=admin}；token 无效直接拒绝握手。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class WsAuthenticator {

	@Autowired
	private TokenService tokenService;

	@Autowired(required = false)
	private RedisUtil redisUtil;

	/**
	 * 校验 token 并解析用户身份；失败返回 null
	 */
	public WsAuthenticator.Principal authenticate(String token, String clientId) {
		if (!StringUtils.hasText(token)) {
			return null;
		}
		try {
			if (!tokenService.validateTokenClient(token, clientId)) {
				return null;
			}
			Map<String, Object> data = tokenService.getTokenData(token);
			if (data == null) {
				return null;
			}
			Long userId = extractLong(data.get("userId"));
			String username = String.valueOf(data.get("username"));
			Object boundClient = data.get("clientId");
			String client = boundClient == null ? null : String.valueOf(boundClient);
			if (userId == null) {
				return null;
			}
			return new Principal(userId, username, client);
		}
		catch (Exception e) {
			log.warn("WS 鉴权失败: {}", e.getMessage());
			return null;
		}
	}

	private Long extractLong(Object value) {
		if (value instanceof Number number) {
			return number.longValue();
		}
		if (value instanceof String s) {
			try {
				return Long.parseLong(s);
			}
			catch (NumberFormatException ignored) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 鉴权结果
	 */
	public record Principal(Long userId, String username, String clientId) {
	}

}
