package com.cloudwaer.common.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

/**
 * 包路径兜底路由：覆盖 Feign 直调、服务内部调用等非 Servlet 入口。
 *
 * <p>
 * 约定：{@code com.cloudwaer.<key>.serve..*} → 路由键 {@code <key>}。可用
 * {@link CloudwaerDataSource} 覆盖。 解析不出约定键时<strong>不推入</strong>默认库，保留外层（如路径过滤器）已选中的数据源，避免
 * Mapper 代理类把键打成 defaultKey。
 * </p>
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PackageDataSourceAspect {

	private final DataSourceRouteProperties props;

	public PackageDataSourceAspect(DataSourceRouteProperties props) {
		this.props = props;
	}

	/**
	 * 约定包结构；annotation 切点兜底非约定命名。
	 */
	@Pointcut("execution(* com.cloudwaer..serve..*.*(..)) || @annotation(com.cloudwaer.common.datasource.CloudwaerDataSource) || @within(com.cloudwaer.common.datasource.CloudwaerDataSource)")
	public void routed() {
	}

	@Around("routed()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		String ds = resolve(pjp);
		if (ds == null) {
			return pjp.proceed();
		}
		DynamicDataSourceContextHolder.push(ds);
		try {
			return pjp.proceed();
		}
		finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

	/**
	 * @return 路由键；无法判定时返回 {@code null}（不切换数据源）
	 */
	private String resolve(ProceedingJoinPoint pjp) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		CloudwaerDataSource onMethod = AnnotationUtils.findAnnotation(method, CloudwaerDataSource.class);
		if (onMethod != null) {
			return onMethod.value();
		}
		for (Class<?> type : candidateTypes(pjp, signature)) {
			CloudwaerDataSource onClass = AnnotationUtils.findAnnotation(type, CloudwaerDataSource.class);
			if (onClass != null) {
				return onClass.value();
			}
		}
		for (Class<?> type : candidateTypes(pjp, signature)) {
			String key = DataSourceRouteResolver.resolveFromPackage(type.getPackageName(), props);
			if (key != null) {
				return key;
			}
		}
		return null;
	}

	private Class<?>[] candidateTypes(ProceedingJoinPoint pjp, MethodSignature signature) {
		Class<?> declaring = signature.getDeclaringType();
		Class<?> targetClass = null;
		if (pjp.getTarget() != null) {
			targetClass = AopUtils.getTargetClass(pjp.getTarget());
			if (targetClass == null || isProxyTypeName(targetClass.getName())) {
				targetClass = pjp.getTarget().getClass();
			}
		}
		if (targetClass != null && !isProxyTypeName(targetClass.getName()) && targetClass != declaring) {
			return new Class<?>[] { targetClass, declaring };
		}
		return new Class<?>[] { declaring };
	}

	private static boolean isProxyTypeName(String name) {
		return name.contains("$$") || name.contains("$Proxy") || name.startsWith("com.sun.proxy.")
				|| name.startsWith("jdk.proxy");
	}

}
