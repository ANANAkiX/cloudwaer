package com.cloudwaer.common.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller 方法级 Redis 结果缓存。
 *
 * <p>
 * 查询优先读缓存；未命中则执行方法并将返回值写入 Redis。 默认缓存 24 小时，可通过 {@link #time()} 覆盖（单位秒）。
 * </p>
 *
 * <p>
 * key 规则：最终 key = {@code prefix} + {@code key}；若 {@code key} 为空，则使用
 * {@code 类名:方法名:参数摘要}。{@code key} 支持 SpEL，如 {@code #{#p0}}、#{@code {#type}}。
 * </p>
 *
 * <pre>
 * &#64;RedisCache(key = "dict:all", time = 3600)
 * &#64;GetMapping("/list")
 * public Result&lt;List&lt;Dict&gt;&gt; list() { ... }
 * </pre>
 *
 * @author cloudwaer
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCache {

	/**
	 * 缓存 key。为空时自动生成 类名:方法名:参数摘要。
	 *
	 * <p>
	 * 含 {@code #{...}} 时按 SpEL 模板解析；否则按字面量使用（如 {@code permission:enum}）。
	 * </p>
	 *
	 * <p>
	 * SpEL 示例：
	 * </p>
	 * <ul>
	 * <li>方法参数：{@code permission:user:#{#userId}}、{@code dict:#{#p0}}</li>
	 * <li>登录用户：{@code permission:user:#{#auth.userId}}（也支持 {@code #{#auth.username}} /
	 * {@code #{#auth.roleId}} / {@code #{#auth.roleIds}} /
	 * {@code #{#auth.permissions}}）</li>
	 * <li>方法无同名参数时，{@code #{#userId}} 自动回落当前登录用户</li>
	 * </ul>
	 */
	String key() default "";

	/**
	 * 过期时间（秒），默认 24 小时。
	 */
	long time() default 86400L;

	/**
	 * key 前缀，默认 cloudwaer:cache:
	 */
	String prefix() default "cloudwaer:cache:";

}
