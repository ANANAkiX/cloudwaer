package com.cloudwaer.common.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 多数据源路由配置。
 *
 * <p>
 * 约定优先：无配置时按 {@code com.cloudwaer.<key>.serve} 与 {@code /<key>/} 自动解析。此处用于覆盖/补漏。
 * </p>
 */
@ConfigurationProperties(prefix = "cloudwaer.datasource.route")
@Data
public class DataSourceRouteProperties {

	/** 是否启用自动路由（过滤器 + 切面） */
	private boolean enabled = true;

	/** 无法解析时的默认路由键（与 dynamic primary 无关，admin/auth 等场景回落） */
	private String defaultKey = "admin";

	/** 不参与「路径首段即路由键」的保留段（auth、actuator 等应落 defaultKey） */
	private String[] reservedPathSegments = new String[] { "auth", "actuator", "captcha", "error", "favicon", "robots",
			"v3", "swagger-ui", "doc", "swagger", "druid", "h2", "ws", "sockjs-node" };

	/** 额外包前缀 → 路由键（最长前缀优先；约定已覆盖的可不配） */
	private Map<String, String> packagePrefixes = new LinkedHashMap<>();

	/** 额外路径前缀 → 路由键（须以 / 开头；最长前缀优先） */
	private Map<String, String> pathPrefixes = new LinkedHashMap<>();

}
