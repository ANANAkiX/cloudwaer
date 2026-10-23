package com.cloudwaer.common.redis.aspect;

import com.cloudwaer.common.core.util.RedisUtil;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.common.redis.annotation.RedisCache;
import com.cloudwaer.common.redis.model.AuthCacheContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.StringJoiner;

/**
 * {@link RedisCache} 切面：优先读缓存，未命中执行方法并回写。
 *
 * <p>
 * SpEL 上下文：
 * </p>
 * <ul>
 * <li>方法参数：{@code #{#userId}} / {@code #{#p0}}</li>
 * <li>登录用户：{@code #{#auth.userId}}、{@code #{#auth.username}}、{@code #{#auth.roleId}}、
 * {@code #{#auth.roleIds}}、{@code #{#auth.permissions}}</li>
 * <li>无同名方法参数时，{@code #{#userId}} / {@code #{#username}} 等会回落到当前登录用户</li>
 * </ul>
 *
 * @author cloudwaer
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RedisCacheAspect {

	private final RedisUtil redisUtil;

	private final ObjectProvider<ObjectMapper> objectMapperProvider;

	private final ExpressionParser parser = new SpelExpressionParser();

	private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

	@Around("@annotation(cacheAnnotation)")
	public Object around(ProceedingJoinPoint pjp, RedisCache cacheAnnotation) throws Throwable {
		if (redisUtil == null || !redisUtil.isAvailable()) {
			return pjp.proceed();
		}

		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		String cacheKey = buildKey(method, pjp.getArgs(), cacheAnnotation);
		if (cacheKey == null) {
			return pjp.proceed();
		}

		String cached = redisUtil.get(cacheKey);
		if (cached != null) {
			Object hit = deserialize(cached, method.getGenericReturnType());
			if (hit != null) {
				log.info("@RedisCache 命中缓存: key={}, method={}.{}", cacheKey, method.getDeclaringClass().getSimpleName(),
						method.getName());
				return hit;
			}
			log.debug("@RedisCache 缓存反序列化失败，将重算: key={}", cacheKey);
		}

		Object result = pjp.proceed();
		if (result != null) {
			try {
				String json = mapper().writeValueAsString(result);
				redisUtil.set(cacheKey, json, cacheAnnotation.time());
			}
			catch (Exception e) {
				log.warn("写入 Redis 缓存失败, key={}", cacheKey, e);
			}
		}
		return result;
	}

	private String buildKey(Method method, Object[] args, RedisCache annotation) {
		String expression = annotation.key();
		String body;
		if (expression == null || expression.isBlank()) {
			body = defaultKeyBody(method, args);
		}
		else if (expression.contains("#{")) {
			body = evaluate(expression, method, args);
		}
		else {
			body = expression;
		}
		if (body == null) {
			return null;
		}
		String prefix = annotation.prefix();
		if (prefix == null || prefix.isBlank()) {
			prefix = "cloudwaer:cache:";
		}
		return prefix + body;
	}

	private String evaluate(String expression, Method method, Object[] args) {
		try {
			StandardEvaluationContext context = new StandardEvaluationContext();
			String[] names = nameDiscoverer.getParameterNames(method);
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					context.setVariable(names[i], args[i]);
				}
			}
			for (int i = 0; i < args.length; i++) {
				context.setVariable("p" + i, args[i]);
				context.setVariable("a" + i, args[i]);
			}

			AuthCacheContext auth = resolveAuthContext();
			context.setVariable("auth", auth);
			// 无同名方法参数时，常用别名回落到登录用户
			context.setVariable("userId", auth.getUserId());
			context.setVariable("username", auth.getUsername());
			context.setVariable("roleId", auth.getRoleId());
			context.setVariable("roleIds", auth.getRoleIds());
			context.setVariable("permissions", auth.getPermissions());

			Object value = parser.parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION).getValue(context);
			return value == null ? null : String.valueOf(value);
		}
		catch (Exception e) {
			log.warn("解析 @RedisCache SpEL 失败: {}", expression, e);
			return null;
		}
	}

	/**
	 * 登录上下文；未登录时字段为 null。
	 */
	private AuthCacheContext resolveAuthContext() {
		AuthCacheContext auth = new AuthCacheContext();
		try {
			auth.setUserId(SecurityContextUtil.getCurrentUserId());
			auth.setUsername(SecurityContextUtil.getCurrentUsername());
			auth.setRoleId(SecurityContextUtil.getCurrentRoleId());
			auth.setRoleIds(SecurityContextUtil.getCurrentRoleIds());
			auth.setPermissions(SecurityContextUtil.getCurrentPermissions());
			auth.setToken(SecurityContextUtil.getCurrentToken());
		}
		catch (Exception e) {
			log.debug("解析登录上下文失败: {}", e.getMessage());
		}
		return auth;
	}

	private String defaultKeyBody(Method method, Object[] args) {
		StringJoiner joiner = new StringJoiner("_");
		if (args != null) {
			for (Object arg : args) {
				joiner.add(arg == null ? "null" : String.valueOf(arg));
			}
		}
		String params = joiner.length() == 0 ? "" : joiner.toString();
		return method.getDeclaringClass().getSimpleName() + ":" + method.getName()
				+ (params.isEmpty() ? "" : ":" + params);
	}

	private Object deserialize(String json, Type returnType) {
		try {
			if (returnType == String.class) {
				return json;
			}
			return mapper().readValue(json, mapper().getTypeFactory().constructType(returnType));
		}
		catch (Exception e) {
			log.debug("缓存反序列化失败, 将重算: {}", e.getMessage());
			return null;
		}
	}

	private ObjectMapper mapper() {
		ObjectMapper mapper = objectMapperProvider.getIfAvailable();
		return mapper != null ? mapper : new ObjectMapper();
	}

}
