package com.cloudwaer.common.log.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 请求日志组件配置
 * 配置前缀：cloudwaer.log
 *
 * 示例：
 * cloudwaer:
 *   log:
 *     enabled: true
 *     exclude-paths:
 *       - /actuator/**
 *       - /auth/captcha
 *     exclude-http-methods:
 *       - OPTIONS
 *     exclude-class-names:
 *       - com.cloudwaer.demo.IgnoreController
 *     exclude-method-names:
 *       - ping
 */
@ConfigurationProperties(prefix = "cloudwaer.log")
public class LogProperties {
    /**
     * 是否启用请求日志
     */
    private boolean enabled = true;
    /**
     * 排除的请求路径（Ant风格通配符）
     */
    private Set<String> excludePaths = new HashSet<>();
    /**
     * 排除的HTTP方法（如：GET/POST/OPTIONS）
     */
    private Set<String> excludeHttpMethods = new HashSet<>();
    /**
     * 排除的控制器类名（全限定名）
     */
    private Set<String> excludeClassNames = new HashSet<>();
    /**
     * 排除的控制器方法名
     */
    private Set<String> excludeMethodNames = new HashSet<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Set<String> getExcludePaths() { return excludePaths; }
    public void setExcludePaths(Set<String> excludePaths) { this.excludePaths = excludePaths; }
    public Set<String> getExcludeHttpMethods() { return excludeHttpMethods; }
    public void setExcludeHttpMethods(Set<String> excludeHttpMethods) { this.excludeHttpMethods = excludeHttpMethods; }
    public Set<String> getExcludeClassNames() { return excludeClassNames; }
    public void setExcludeClassNames(Set<String> excludeClassNames) { this.excludeClassNames = excludeClassNames; }
    public Set<String> getExcludeMethodNames() { return excludeMethodNames; }
    public void setExcludeMethodNames(Set<String> excludeMethodNames) { this.excludeMethodNames = excludeMethodNames; }
}
