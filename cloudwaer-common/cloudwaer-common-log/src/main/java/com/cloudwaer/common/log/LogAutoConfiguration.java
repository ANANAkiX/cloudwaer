package com.cloudwaer.common.log;

import com.cloudwaer.common.log.config.LogProperties;
import com.cloudwaer.common.log.service.LogSaver;
import com.cloudwaer.common.log.service.impl.Slf4jLogSaver;
import com.cloudwaer.common.log.aspect.RequestLogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 自动配置：请求日志模块
 *
 * - 绑定 {@link LogProperties} - 在 cloudwaer.log.enabled=true（默认）时生效 - 暴露
 * {@link com.cloudwaer.common.log.service.LogSaver} 的默认实现 - 注册请求日志切面
 * {@link com.cloudwaer.common.log.aspect.RequestLogAspect} - 异步保存日志（独立线程池，不阻塞主流程）
 */
@AutoConfiguration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = "cloudwaer.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration {

	/**
	 * 日志落地默认实现：输出到应用日志（Slf4j）。 业务侧可自定义同类型 Bean 覆盖（如保存到 DB/ES/MQ）。
	 */
	@Bean
	@ConditionalOnMissingBean
	public LogSaver logSaver() {
		return new Slf4jLogSaver();
	}

	/**
	 * 请求日志切面：拦截控制器层 Mapping 方法，统一记录请求信息。
	 */
	@Bean
	public RequestLogAspect requestLogAspect(LogProperties properties, LogSaver saver, Environment environment,
			Executor commonLogExecutor) {
		return new RequestLogAspect(properties, saver, environment, commonLogExecutor);
	}

	/**
	 * 日志异步线程池
	 */
	@Bean(name = "commonLogExecutor")
	@ConditionalOnMissingBean(name = "commonLogExecutor")
	public Executor commonLogExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(1024);
		executor.setThreadNamePrefix("common-log-");
		executor.setDaemon(true);
		executor.initialize();
		return executor;
	}

}
