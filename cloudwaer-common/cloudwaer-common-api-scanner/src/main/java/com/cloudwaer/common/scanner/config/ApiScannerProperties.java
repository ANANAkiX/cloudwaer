package com.cloudwaer.common.scanner.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * API扫描器配置属性
 *
 * @author cloudwaer
 */
@Data
@Validated
@ConfigurationProperties(prefix = "cloudwaer.api-scanner")
public class ApiScannerProperties {

    /**
     * 服务ID（必填，唯一标识，通常是服务名称）
     * 例如：cloudwaer-admin-serve
     */
    @NotBlank(message = "服务ID不能为空")
    private String serviceId;

    /**
     * 是否启用API扫描
     */
    private boolean enabled = true;

    /**
     * 排除的请求方法名称（不扫描这些方法）
     * 例如：["error", "health", "actuator"]
     */
    private List<String> excludeMethods = new ArrayList<>();

    /**
     * 排除的路径模式（支持Ant路径匹配）
     * 例如：["/actuator/**", "/error"]
     */
    private List<String> excludePaths = new ArrayList<>();

    /**
     * 扫描的基础包路径（如果不配置，则扫描所有包）
     */
    private List<String> basePackages = new ArrayList<>();
}

