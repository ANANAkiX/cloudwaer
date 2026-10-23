package com.cloudwaer.boot.filter;

import com.cloudwaer.boot.config.CaptchaProperties;
import com.cloudwaer.common.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CaptchaValidationFilter extends OncePerRequestFilter {

	private static final String LOGIN_PATH = "/auth/login";

	private static final String HEADER_CAPTCHA_ID = "X-Captcha-Id";

	private static final String HEADER_CAPTCHA_CODE = "X-Captcha-Code";

	private static final String CAPTCHA_KEY_PREFIX = "captcha:";

	private final CaptchaProperties props;

	private final StringRedisTemplate redis;

	private final ObjectMapper objectMapper;

	public CaptchaValidationFilter(CaptchaProperties props, StringRedisTemplate redis, ObjectMapper objectMapper) {
		this.props = props;
		this.redis = redis;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!props.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		String path = request.getRequestURI();
		if (!LOGIN_PATH.equals(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String captchaId = request.getHeader(HEADER_CAPTCHA_ID);
		String captchaCode = request.getHeader(HEADER_CAPTCHA_CODE);
		if (!org.springframework.util.StringUtils.hasText(captchaId)) {
			captchaId = request.getParameter("captchaId");
		}
		if (!org.springframework.util.StringUtils.hasText(captchaCode)) {
			captchaCode = request.getParameter("captchaCode");
		}

		if (!org.springframework.util.StringUtils.hasText(captchaId)
				|| !org.springframework.util.StringUtils.hasText(captchaCode)) {
			writeError(response, Result.fail(400, "缺少验证码参数"));
			return;
		}

		String key = CAPTCHA_KEY_PREFIX + captchaId;
		String expect = redis.opsForValue().get(key);
		if (!org.springframework.util.StringUtils.hasText(expect)) {
			writeError(response, Result.fail(400, "验证码已过期，请重新获取"));
			return;
		}

		if (!expect.equalsIgnoreCase(captchaCode)) {
			writeError(response, Result.fail(400, "验证码错误"));
			return;
		}

		try {
			redis.delete(key);
		}
		catch (Exception e) {
		}

		filterChain.doFilter(request, response);
	}

	private void writeError(HttpServletResponse response, Result<?> result) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));
		response.getWriter().flush();
	}

}
