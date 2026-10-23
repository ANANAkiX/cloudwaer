package com.cloudwaer.boot.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 单体多数据源：Flowable 启动建表强制写入 flowable 库。
 *
 * <p>
 * 引擎必须使用与业务相同的 dynamic 路由 DataSource（不要另包一层代理，否则 Spring 事务按 DataSource 实例绑定连接时会与 MyBatis
 * 断开共享）。运行期由 {@code DynamicDataSourceRouteFilter} 在 {@code /flowable/**} 请求全程 push
 * flowable 键。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SpringProcessEngineConfiguration.class)
public class BootFlowableDataSourceConfig {

	@Bean
	@ConditionalOnMissingBean(type = "org.flowable.engine.ProcessEngine")
	public ProcessEngineFactoryBean bootFlowableProcessEngine(SpringProcessEngineConfiguration configuration) {
		return new FlowableDsAwareProcessEngineFactoryBean(configuration);
	}

	static class FlowableDsAwareProcessEngineFactoryBean extends ProcessEngineFactoryBean {

		FlowableDsAwareProcessEngineFactoryBean(SpringProcessEngineConfiguration configuration) {
			setProcessEngineConfiguration(configuration);
		}

		@Override
		public ProcessEngine getObject() throws Exception {
			DynamicDataSourceContextHolder.push("flowable");
			try {
				return super.getObject();
			}
			finally {
				DynamicDataSourceContextHolder.poll();
			}
		}

	}

}
