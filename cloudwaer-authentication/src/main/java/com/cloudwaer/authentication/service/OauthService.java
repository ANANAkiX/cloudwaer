package com.cloudwaer.authentication.service;

import com.cloudwaer.admin.api.dto.*;
import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.authentication.dto.LoginResponseDTO;
import com.cloudwaer.authentication.dto.OauthRegisterRequestDTO;
import com.cloudwaer.authentication.dto.OauthRegisterTicketDTO;
import com.cloudwaer.authentication.dto.OauthStateDTO;
import com.cloudwaer.authentication.exception.OauthNeedRegisterException;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.cloudwaer.common.core.service.GlobalSettingService;
import com.cloudwaer.common.core.service.PasswordTransportService;
import com.cloudwaer.common.core.service.TokenService;
import com.cloudwaer.common.core.util.RedisUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 第三方登录（OAuth）流程服务：授权地址生成、授权码换票、登录/绑定。
 * <p>
 * 流程：前端取授权地址 → 跳供应商授权 → 回调后端 /auth/oauth/callback 中转回前端 （回跳地址白名单校验）→ 前端带 code+state 调
 * /auth/oauth/login 完成登录。
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class OauthService {

	private static final String STATE_KEY_PREFIX = "cloudwaer:oauth:state:";

	private static final long STATE_TTL_SECONDS = 600;

	/**
	 * 「待注册」票据 key 前缀（一次性，短时有效）
	 */
	private static final String REGISTER_TICKET_KEY_PREFIX = "cloudwaer:oauth:register:";

	private static final long REGISTER_TICKET_TTL_SECONDS = 900;

	private static final String GITHUB_PROVIDER = "GITHUB";

	@Autowired
	private AdminFeignClient adminFeignClient;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private GlobalSettingService globalSettingService;

	@Autowired
	private PasswordTransportService passwordTransportService;

	@Autowired
	private ObjectMapper objectMapper;

	private RestTemplate restTemplate;

	/**
	 * 额外放行的回跳来源（逗号分隔 origin，如 https://blog.example.com）。 localhost / 127.0.0.1
	 * 始终放行（仅本机可达，不构成开放重定向风险）。
	 */
	@Value("${cloudwaer.oauth.redirect-origins:}")
	private String redirectOrigins;

	// ------------------------------------------------------------------
	// 授权入口
	// ------------------------------------------------------------------

	/**
	 * 生成第三方授权地址（登录意图）
	 */
	public Map<String, Object> buildLoginAuthorizeUrl(String providerType, String clientId, String returnTo) {
		return buildAuthorizeUrl(providerType, clientId, returnTo, OauthStateDTO.INTENT_LOGIN, null);
	}

	/**
	 * 生成第三方授权地址（绑定意图，需已登录）
	 */
	public Map<String, Object> buildBindAuthorizeUrl(String providerType, String clientId, String returnTo,
			Long userId) {
		if (userId == null) {
			throw new BusinessException("未登录或登录已失效");
		}
		return buildAuthorizeUrl(providerType, clientId, returnTo, OauthStateDTO.INTENT_BIND, userId);
	}

	private Map<String, Object> buildAuthorizeUrl(String providerType, String clientId, String returnTo, String intent,
			Long userId) {
		if (!StringUtils.hasText(providerType)) {
			throw new BusinessException("供应商类型不能为空");
		}
		if (!StringUtils.hasText(returnTo)) {
			throw new BusinessException("回跳地址不能为空");
		}
		if (!isAllowedReturnTo(returnTo)) {
			throw new BusinessException("回跳地址不在白名单内");
		}

		OauthProviderDTO provider = getProvider(providerType);
		if (!StringUtils.hasText(provider.getAuthUrl()) || !StringUtils.hasText(provider.getClientId())) {
			throw new BusinessException("第三方登录供应商配置不完整");
		}

		OauthStateDTO stateDTO = new OauthStateDTO();
		stateDTO.setIntent(intent);
		stateDTO.setProviderType(providerType);
		stateDTO.setClientId(StringUtils.hasText(clientId) ? clientId : "admin");
		stateDTO.setReturnTo(returnTo);
		stateDTO.setUserId(userId);
		String state = UUID.randomUUID().toString().replace("-", "");
		redisUtil.setObject(STATE_KEY_PREFIX + state, stateDTO, STATE_TTL_SECONDS);

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(provider.getAuthUrl())
			.queryParam("client_id", provider.getClientId())
			.queryParam("state", state);
		if (StringUtils.hasText(provider.getRedirectUri())) {
			builder.queryParam("redirect_uri", provider.getRedirectUri());
		}
		if (StringUtils.hasText(provider.getScope())) {
			builder.queryParam("scope", normalizeScope(provider.getScope()));
		}

		Map<String, Object> data = new HashMap<>();
		// build().encode() 对查询参数做百分号编码（scope 含空格时 build(true) 会直接抛异常）
		data.put("url", builder.build().encode().toUriString());
		data.put("state", state);
		return data;
	}

	/**
	 * 规范化 scope：兼容空格/逗号分隔，压缩为单空格（供应商按空格分隔识别）
	 */
	private String normalizeScope(String scope) {
		return scope.trim().replaceAll("[,\\s]+", " ");
	}

	// ------------------------------------------------------------------
	// 供应商回调中转（浏览器 302，不换票）
	// ------------------------------------------------------------------

	/**
	 * 供应商回调中转：校验 state 后 302 回前端地址（携 code+state，由前端完成登录/绑定）。
	 */
	public String resolveRelayLocation(String code, String state, String error) {
		OauthStateDTO stateDTO = peekState(state);
		if (stateDTO == null) {
			throw new BusinessException("state 无效或已过期，请重新发起授权");
		}
		String returnTo = stateDTO.getReturnTo();
		StringBuilder location = new StringBuilder(returnTo).append(returnTo.contains("?") ? "&" : "?");
		if (StringUtils.hasText(error)) {
			location.append("error=").append(urlEncode(error));
		}
		else {
			location.append("code=").append(urlEncode(code == null ? "" : code));
		}
		location.append("&state=").append(urlEncode(state));
		return location.toString();
	}

	// ------------------------------------------------------------------
	// 登录 / 绑定 / 解绑
	// ------------------------------------------------------------------

	/**
	 * 授权码登录：已绑定直接登录；未绑定自动注册并绑定（受注册开关约束）。
	 */
	public LoginResponseDTO loginWithCode(String providerType, String code, String state, String requestClientId) {
		OauthStateDTO stateDTO = consumeState(state, OauthStateDTO.INTENT_LOGIN);
		if (!stateDTO.getProviderType().equalsIgnoreCase(providerType)) {
			throw new BusinessException("state 与供应商类型不匹配");
		}
		if (StringUtils.hasText(requestClientId) && StringUtils.hasText(stateDTO.getClientId())
				&& !requestClientId.equals(stateDTO.getClientId())) {
			log.warn("clientId 与 state 记录不一致: request={}, state={}", requestClientId, stateDTO.getClientId());
		}

		OauthProviderDTO provider = getProvider(providerType);
		OauthIdentityDTO identity = exchangeAndFetch(provider, code);

		UserDTO user = getUserByOauth(providerType, identity.getProviderUserId());
		if (user == null) {
			if (!globalSettingService.getBool(GlobalSettingService.KEY_REGISTER_ENABLED, true)) {
				throw new BusinessException("该第三方账号尚未绑定系统账号，且注册功能已关闭");
			}
			// 不静默建号：签发注册票据，由前端引导用户到注册页手填账号/密码，提交后再注册并绑定
			String registerTicket = createRegisterTicket(identity, stateDTO.getClientId());
			throw new OauthNeedRegisterException("该第三方账号尚未绑定系统账号，请先注册", registerTicket, identity.getProviderUsername(),
					identity.getEmail(), identity.getAvatarUrl());
		}

		try {
			List<String> permissionCodes = tryGetPermissionCodes(user.getId());
			String tokenUuid = tokenService.generateAndStoreToken(user.getId(), user.getUsername(), user.getRoleIds(),
					permissionCodes, stateDTO.getClientId(), user.getNickname(), user.getAvatar());
			if (tokenUuid == null) {
				log.error("第三方登录 Token 生成失败: username={}", user.getUsername());
				throw new BusinessException(ResultCode.FAIL);
			}
			log.info("第三方登录成功: provider={}, providerUserId={}, username={}, userId={}", providerType,
					identity.getProviderUserId(), user.getUsername(), user.getId());
			return LoginResponseDTO.builder().token(tokenUuid).username(user.getUsername()).build();
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("第三方登录异常: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.FAIL);
		}
	}

	/**
	 * 授权码绑定到已登录账号（state 里带发起绑定时的 userId，且必须与当前登录用户一致）
	 */
	public Boolean bindWithCode(String providerType, String code, String state, Long currentUserId) {
		OauthStateDTO stateDTO = consumeState(state, OauthStateDTO.INTENT_BIND);
		if (!stateDTO.getProviderType().equalsIgnoreCase(providerType)) {
			throw new BusinessException("state 与供应商类型不匹配");
		}
		if (currentUserId == null || !currentUserId.equals(stateDTO.getUserId())) {
			throw new BusinessException("绑定请求与当前登录用户不一致，请重新发起绑定");
		}

		OauthProviderDTO provider = getProvider(providerType);
		OauthIdentityDTO identity = exchangeAndFetch(provider, code);

		OauthBindDTO bindDTO = new OauthBindDTO();
		bindDTO.setUserId(stateDTO.getUserId());
		bindDTO.setProviderType(providerType);
		bindDTO.setProviderUserId(identity.getProviderUserId());
		bindDTO.setProviderUsername(identity.getProviderUsername());
		bindDTO.setEmail(identity.getEmail());
		bindDTO.setAvatarUrl(identity.getAvatarUrl());

		Result<Boolean> result = callAdmin(() -> adminFeignClient.bindOauthUser(bindDTO));
		if (result == null || result.getCode() == null || result.getCode() != 200) {
			String message = result != null && result.getMessage() != null ? result.getMessage() : "绑定失败";
			throw new BusinessException(message);
		}
		log.info("第三方账号绑定成功: userId={}, provider={}, providerUserId={}", stateDTO.getUserId(), providerType,
				identity.getProviderUserId());
		return result.getData();
	}

	/**
	 * 解绑当前用户的第三方账号
	 */
	public Boolean unbind(String providerType, Long userId) {
		if (userId == null) {
			throw new BusinessException("未登录或登录已失效");
		}
		if (!StringUtils.hasText(providerType)) {
			throw new BusinessException("供应商类型不能为空");
		}
		Result<Boolean> result = callAdmin(() -> adminFeignClient.unbindOauthUser(userId, providerType));
		if (result == null || result.getCode() == null || result.getCode() != 200) {
			String message = result != null && result.getMessage() != null ? result.getMessage() : "解绑失败";
			throw new BusinessException(message);
		}
		return result.getData();
	}

	/**
	 * 查询用户的第三方绑定列表
	 */
	public List<OauthBindingDTO> listBindings(Long userId) {
		if (userId == null) {
			throw new BusinessException("未登录或登录已失效");
		}
		Result<List<OauthBindingDTO>> result = callAdmin(() -> adminFeignClient.listOauthBindings(userId));
		if (result == null || result.getCode() == null || result.getCode() != 200) {
			return new ArrayList<>();
		}
		return result.getData() == null ? new ArrayList<>() : result.getData();
	}

	// ------------------------------------------------------------------
	// 换票与用户信息
	// ------------------------------------------------------------------

	private OauthIdentityDTO exchangeAndFetch(OauthProviderDTO provider, String code) {
		String accessToken = exchangeCode(provider, code);
		return fetchUserInfo(provider, accessToken);
	}

	private String exchangeCode(OauthProviderDTO provider, String code) {
		if (!StringUtils.hasText(provider.getTokenUrl()) || !StringUtils.hasText(provider.getClientSecret())) {
			throw new BusinessException("第三方登录供应商配置不完整");
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.add("client_id", provider.getClientId());
			form.add("client_secret", provider.getClientSecret());
			form.add("code", code);
			if (StringUtils.hasText(provider.getRedirectUri())) {
				form.add("redirect_uri", provider.getRedirectUri());
			}

			String body = getRestTemplate().postForObject(provider.getTokenUrl(), new HttpEntity<>(form, headers),
					String.class);
			String accessToken = parseAccessToken(body);
			if (!StringUtils.hasText(accessToken)) {
				log.error("第三方换票未返回 access_token: provider={}, body={}", provider.getProviderType(), body);
				throw new BusinessException("第三方授权码无效或已过期");
			}
			return accessToken;
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("第三方换票失败: provider={}, {}", provider.getProviderType(), e.getMessage(), e);
			throw new BusinessException("第三方换票失败，请重试");
		}
	}

	private String parseAccessToken(String body) {
		if (!StringUtils.hasText(body)) {
			return null;
		}
		String trimmed = body.trim();
		if (trimmed.startsWith("{")) {
			try {
				JsonNode node = objectMapper.readTree(trimmed);
				JsonNode token = node.has("access_token") ? node.get("access_token") : null;
				return token == null || token.isNull() ? null : token.asText();
			}
			catch (Exception e) {
				log.warn("解析换票响应失败: {}", e.getMessage());
				return null;
			}
		}
		// 兼容 form 编码响应：access_token=xxx&token_type=bearer
		for (String pair : trimmed.split("&")) {
			int idx = pair.indexOf('=');
			if (idx > 0 && "access_token".equals(pair.substring(0, idx))) {
				return urlDecode(pair.substring(idx + 1));
			}
		}
		return null;
	}

	private OauthIdentityDTO fetchUserInfo(OauthProviderDTO provider, String accessToken) {
		if (!StringUtils.hasText(provider.getUserInfoUrl())) {
			throw new BusinessException("第三方登录供应商配置不完整");
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			// GitHub API 要求 User-Agent
			headers.set(HttpHeaders.USER_AGENT, "cloudwaer");

			String body = getRestTemplate()
				.exchange(provider.getUserInfoUrl(), HttpMethod.GET, new HttpEntity<>(headers), String.class)
				.getBody();
			JsonNode node = objectMapper.readTree(body == null ? "{}" : body);
			if (node.has("error") || (!node.has("id") && !node.has("sub"))) {
				log.error("第三方用户信息获取失败: provider={}, body={}", provider.getProviderType(), body);
				throw new BusinessException("获取第三方用户信息失败");
			}

			OauthIdentityDTO identity = new OauthIdentityDTO();
			identity.setProviderType(provider.getProviderType());
			identity.setProviderUserId(firstNonBlank(node, "id", "sub"));
			identity.setProviderUsername(firstNonBlank(node, "login", "username", "name"));
			identity.setEmail(firstNonBlank(node, "email"));
			identity.setAvatarUrl(firstNonBlank(node, "avatar_url", "avatar"));
			identity.setAccessToken(accessToken);

			// GitHub 邮箱可能因隐私设置返回 null，补查 /user/emails
			if (!StringUtils.hasText(identity.getEmail())
					&& GITHUB_PROVIDER.equalsIgnoreCase(provider.getProviderType())) {
				identity.setEmail(fetchGithubPrimaryEmail(provider.getUserInfoUrl(), headers));
			}
			return identity;
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("获取第三方用户信息异常: provider={}, {}", provider.getProviderType(), e.getMessage(), e);
			throw new BusinessException("获取第三方用户信息失败");
		}
	}

	private String fetchGithubPrimaryEmail(String userInfoUrl, HttpHeaders headers) {
		try {
			String emailsUrl = userInfoUrl.replaceAll("/$", "") + "/emails";
			String body = getRestTemplate().exchange(emailsUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class)
				.getBody();
			JsonNode arr = objectMapper.readTree(body == null ? "[]" : body);
			if (!arr.isArray()) {
				return null;
			}
			String fallback = null;
			for (JsonNode item : arr) {
				String email = item.has("email") ? item.get("email").asText() : null;
				if (!StringUtils.hasText(email)) {
					continue;
				}
				boolean primary = item.has("primary") && item.get("primary").asBoolean(false);
				boolean verified = item.has("verified") && item.get("verified").asBoolean(false);
				if (primary && verified) {
					return email;
				}
				if (fallback == null && verified) {
					fallback = email;
				}
			}
			return fallback;
		}
		catch (Exception e) {
			log.warn("补查 GitHub 邮箱失败: {}", e.getMessage());
			return null;
		}
	}

	// ------------------------------------------------------------------
	// admin 内部调用
	// ------------------------------------------------------------------

	private OauthProviderDTO getProvider(String providerType) {
		Result<OauthProviderDTO> result = callAdmin(() -> adminFeignClient.getOauthProvider(providerType));
		if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
			log.error("第三方登录供应商不存在或未启用: providerType={}, result={}", providerType, result);
			throw new BusinessException("第三方登录未开通或配置有误");
		}
		return result.getData();
	}

	private UserDTO getUserByOauth(String providerType, String providerUserId) {
		Result<UserDTO> result = callAdmin(() -> adminFeignClient.getUserByOauth(providerType, providerUserId));
		if (result == null || result.getCode() == null || result.getCode() != 200) {
			return null;
		}
		return result.getData();
	}

	/**
	 * 用注册票据完成注册并绑定第三方账号，成功后直接登录
	 */
	public LoginResponseDTO registerWithTicket(OauthRegisterRequestDTO request) {
		OauthRegisterTicketDTO ticket = consumeRegisterTicket(request.getRegisterTicket());
		if (!globalSettingService.getBool(GlobalSettingService.KEY_REGISTER_ENABLED, true)) {
			throw new BusinessException("注册功能已关闭");
		}
		String plainPassword = passwordTransportService.resolvePlainPassword(request.getPassword());
		if (!StringUtils.hasText(plainPassword) || plainPassword.length() < 6 || plainPassword.length() > 32) {
			throw new BusinessException("密码长度须在6-32位");
		}

		String username = request.getUsername().trim();
		String nickname = StringUtils.hasText(request.getNickname()) ? request.getNickname().trim()
				: (StringUtils.hasText(ticket.getProviderUsername()) ? ticket.getProviderUsername() : username);
		String email = StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : ticket.getEmail();

		OauthRegisterDTO dto = new OauthRegisterDTO();
		dto.setUsername(username);
		dto.setNickname(nickname);
		dto.setEmail(email);
		dto.setAvatar(ticket.getAvatarUrl());
		dto.setProviderType(ticket.getProviderType());
		dto.setProviderUserId(ticket.getProviderUserId());
		dto.setProviderUsername(ticket.getProviderUsername());
		dto.setAvatarUrl(ticket.getAvatarUrl());

		Result<UserDTO> result = callAdmin(() -> adminFeignClient.registerOauthUser(dto));
		if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
			String message = result != null && result.getMessage() != null ? result.getMessage() : "注册失败";
			log.error("第三方注册失败: provider={}, result={}", ticket.getProviderType(), result);
			throw new BusinessException(message);
		}

		UserDTO user = result.getData();
		try {
			List<String> permissionCodes = tryGetPermissionCodes(user.getId());
			String tokenUuid = tokenService.generateAndStoreToken(user.getId(), user.getUsername(), user.getRoleIds(),
					permissionCodes, ticket.getClientId(), user.getNickname(), user.getAvatar());
			if (tokenUuid == null) {
				log.error("第三方注册后签发 Token 失败: username={}", user.getUsername());
				throw new BusinessException(ResultCode.FAIL);
			}
			log.info("第三方注册并绑定成功: provider={}, providerUserId={}, username={}, userId={}", ticket.getProviderType(),
					ticket.getProviderUserId(), user.getUsername(), user.getId());
			return LoginResponseDTO.builder().token(tokenUuid).username(user.getUsername()).build();
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("第三方注册异常: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.FAIL);
		}
	}

	/**
	 * 签发「待注册」票据：记住第三方身份与发起方客户端（一次性，短时有效）
	 */
	private String createRegisterTicket(OauthIdentityDTO identity, String clientId) {
		OauthRegisterTicketDTO ticket = new OauthRegisterTicketDTO();
		ticket.setProviderType(identity.getProviderType());
		ticket.setProviderUserId(identity.getProviderUserId());
		ticket.setProviderUsername(identity.getProviderUsername());
		ticket.setEmail(identity.getEmail());
		ticket.setAvatarUrl(identity.getAvatarUrl());
		ticket.setClientId(StringUtils.hasText(clientId) ? clientId : "admin");
		String ticketId = UUID.randomUUID().toString().replace("-", "");
		redisUtil.setObject(REGISTER_TICKET_KEY_PREFIX + ticketId, ticket, REGISTER_TICKET_TTL_SECONDS);
		return ticketId;
	}

	private OauthRegisterTicketDTO consumeRegisterTicket(String ticketId) {
		if (!StringUtils.hasText(ticketId)) {
			throw new BusinessException("注册凭证不能为空");
		}
		OauthRegisterTicketDTO payload = redisUtil.getObject(REGISTER_TICKET_KEY_PREFIX + ticketId,
				OauthRegisterTicketDTO.class);
		redisUtil.delete(REGISTER_TICKET_KEY_PREFIX + ticketId);
		if (payload == null) {
			throw new BusinessException("注册凭证已过期，请重新发起第三方登录");
		}
		return payload;
	}

	private List<String> tryGetPermissionCodes(Long userId) {
		try {
			Result<List<String>> permissionsResult = adminFeignClient.getPermissionsByUserId(userId);
			if (permissionsResult != null && permissionsResult.getCode() == 200
					&& permissionsResult.getData() != null) {
				return permissionsResult.getData();
			}
		}
		catch (Exception e) {
			log.warn("获取用户权限失败: {}", e.getMessage());
		}
		return new ArrayList<>();
	}

	private <T> Result<T> callAdmin(AdminCall<T> call) {
		try {
			return call.invoke();
		}
		catch (Exception e) {
			log.error("调用 admin 服务失败: {}", e.getMessage(), e);
			throw new BusinessException(ResultCode.NOT_SERVE_FOUND);
		}
	}

	@FunctionalInterface
	private interface AdminCall<T> {

		Result<T> invoke();

	}

	// ------------------------------------------------------------------
	// state 与工具
	// ------------------------------------------------------------------

	private OauthStateDTO consumeState(String state, String expectedIntent) {
		OauthStateDTO stateDTO = peekState(state);
		// 一次性使用，防重放
		redisUtil.delete(STATE_KEY_PREFIX + state);
		if (stateDTO == null) {
			throw new BusinessException("state 无效或已过期，请重新发起授权");
		}
		if (!expectedIntent.equals(stateDTO.getIntent())) {
			throw new BusinessException("授权意图不匹配，请重新发起授权");
		}
		return stateDTO;
	}

	private OauthStateDTO peekState(String state) {
		if (!StringUtils.hasText(state)) {
			return null;
		}
		return redisUtil.getObject(STATE_KEY_PREFIX + state, OauthStateDTO.class);
	}

	private boolean isAllowedReturnTo(String returnTo) {
		try {
			URI uri = URI.create(returnTo.trim());
			String scheme = uri.getScheme();
			String host = uri.getHost();
			if (host == null || scheme == null) {
				return false;
			}
			if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
				return false;
			}
			// 回跳最终落在访问者本机，不构成对外开放重定向
			if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "[::1]".equals(host)
					|| "::1".equals(host)) {
				return true;
			}
			if (!StringUtils.hasText(redirectOrigins)) {
				return false;
			}
			String origin = scheme.toLowerCase() + "://" + host.toLowerCase()
					+ (uri.getPort() == -1 ? "" : ":" + uri.getPort());
			for (String allowed : redirectOrigins.split(",")) {
				if (origin.equalsIgnoreCase(allowed.trim())) {
					return true;
				}
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			// 走 GitHub 等外网接口：连接10s、读取60s，网络差时快速失败并给出业务错误，避免无限挂起
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			factory.setConnectTimeout(10000);
			factory.setReadTimeout(60000);
			restTemplate = new RestTemplate(factory);
		}
		return restTemplate;
	}

	private String firstNonBlank(JsonNode node, String... fields) {
		for (String field : fields) {
			JsonNode value = node.get(field);
			if (value != null && !value.isNull() && StringUtils.hasText(value.asText())) {
				return value.asText();
			}
		}
		return null;
	}

	private String urlEncode(String value) {
		return java.net.URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}

	private String urlDecode(String value) {
		return java.net.URLDecoder.decode(value == null ? "" : value, StandardCharsets.UTF_8);
	}

}
