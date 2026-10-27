package com.cloudwaer.common.datasource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

/**
 * 多数据源路由自动配置。
 *
 * <p>
 * 引入 {@code cloudwaer-common-datasource} 且 classpath 存在 dynamic-datasource
 * 时自动生效；业务模块只需按约定 命名包路径（{@code com.cloudwaer.<key>.serve}）并在 yml 配置对应数据源，无需改 boot。
 * </p>
 */
@AutoConfiguration
@ConditionalOnClass(DynamicDataSourceContextHolder.class)
@EnableConfigurationProperties(DataSourceRouteProperties.class)
@ConditionalOnProperty(prefix = "cloudwaer.datasource.route", name = "enabled", havingValue = "true",
		matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DataSourceRouteAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PackageDataSourceAspect packageDataSourceAspect(DataSourceRouteProperties props) {
		return new PackageDataSourceAspect(props);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public FilterRegistrationBean<PathDataSourceRouteFilter> pathDataSourceRouteFilter(
			DataSourceRouteProperties props) {
		FilterRegistrationBean<PathDataSourceRouteFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new PathDataSourceRouteFilter(props));
		bean.setOrder(0);
		bean.setName("pathDataSourceRouteFilter");
		return bean;
	}

}
