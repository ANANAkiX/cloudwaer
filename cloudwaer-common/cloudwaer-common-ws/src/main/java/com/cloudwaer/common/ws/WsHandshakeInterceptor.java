package com.cloudwaer.common.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * WS 握手拦截器：从 query 解析 token/clientId 完成鉴权，身份写入 attributes 供会话注册使用。
 *
 * @author cloudwaer
 */
@Component
@RequiredArgsConstructor
public class WsHandshakeInterceptor implements HandshakeInterceptor {

	public static final String ATTR_USER_ID = "ws.userId";

	public static final String ATTR_USERNAME = "ws.username";

	public static final String ATTR_CLIENT_ID = "ws.clientId";

	private final WsAuthenticator wsAuthenticator;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
		String token = params.getFirst("token");
		String clientId = params.getFirst("clientId");
		WsAuthenticator.Principal principal = wsAuthenticator.authenticate(token, clientId);
		if (principal == null) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		attributes.put(ATTR_USER_ID, principal.userId());
		attributes.put(ATTR_USERNAME, principal.username());
		if (StringUtils.hasText(principal.clientId())) {
			attributes.put(ATTR_CLIENT_ID, principal.clientId());
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// no-op
	}

}
