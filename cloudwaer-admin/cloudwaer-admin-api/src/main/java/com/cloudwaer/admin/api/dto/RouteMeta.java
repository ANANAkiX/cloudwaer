package com.cloudwaer.admin.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 路由元信息
 *
 * @author cloudwaer
 */
@Data
public class RouteMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 是否需要权限
     */
    private Boolean requiresAuth;
}




