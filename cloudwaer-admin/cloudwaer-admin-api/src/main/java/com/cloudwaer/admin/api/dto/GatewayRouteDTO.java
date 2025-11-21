package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 网关路由DTO
 *
 * @author cloudwaer
 */
@Data
public class GatewayRouteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路由ID（序列化为字符串，避免前端精度丢失）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 路由ID（唯一标识）
     */
    private String routeId;

    /**
     * 路由URI（如：lb://service-name）
     */
    private String uri;

    /**
     * 路由断言列表
     */
    private List<PredicateConfig> predicates;

    /**
     * 路由过滤器列表
     */
    private List<FilterConfig> filters;

    /**
     * 路由顺序（数字越小优先级越高）
     */
    private Integer order;

    /**
     * 路由描述
     */
    private String description;

    /**
     * 断言配置
     */
    @Data
    public static class PredicateConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<String, String> args;
    }

    /**
     * 过滤器配置
     */
    @Data
    public static class FilterConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<String, String> args;
    }
}

