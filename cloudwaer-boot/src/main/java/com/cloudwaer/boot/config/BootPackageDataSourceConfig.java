package com.cloudwaer.boot.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 包路径兜底路由：覆盖 Feign 直调、服务内部调用等非 Servlet 入口。
 *
 * <p>
 * primary=flowable 时，admin/codegen/blog 必须显式切到对应库；flowable 包保持 flowable（与 primary 一致，
 * 主要是防止过滤器未覆盖的路径）。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class BootPackageDataSourceConfig {

	@Bean
	public BootPackageDataSourceAspect bootPackageDataSourceAspect() {
		return new BootPackageDataSourceAspect();
	}

	@Aspect
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public static class BootPackageDataSourceAspect {

		@Pointcut("execution(* com.cloudwaer.admin.serve..*.*(..))")
		public void adminServe() {
		}

		@Pointcut("execution(* com.cloudwaer.codegen.serve..*.*(..))")
		public void codegenServe() {
		}

		@Pointcut("execution(* com.cloudwaer.flowable.serve..*.*(..))")
		public void flowableServe() {
		}

		@Pointcut("execution(* com.cloudwaer.blog.serve..*.*(..))")
		public void blogServe() {
		}

		@Around("adminServe()")
		public Object aroundAdmin(ProceedingJoinPoint pjp) throws Throwable {
			return proceedWith("admin", pjp);
		}

		@Around("codegenServe()")
		public Object aroundCodegen(ProceedingJoinPoint pjp) throws Throwable {
			return proceedWith("codegen", pjp);
		}

		@Around("flowableServe()")
		public Object aroundFlowable(ProceedingJoinPoint pjp) throws Throwable {
			return proceedWith("flowable", pjp);
		}

		/**
		 * blog 使用独立库 cloudwaer_blog（集成模块各一库，不与 sys_* 共库）。
		 */
		@Around("blogServe()")
		public Object aroundBlog(ProceedingJoinPoint pjp) throws Throwable {
			return proceedWith("blog", pjp);
		}

		private Object proceedWith(String ds, ProceedingJoinPoint pjp) throws Throwable {
			DynamicDataSourceContextHolder.push(ds);
			try {
				return pjp.proceed();
			}
			finally {
				DynamicDataSourceContextHolder.poll();
			}
		}

	}

}
