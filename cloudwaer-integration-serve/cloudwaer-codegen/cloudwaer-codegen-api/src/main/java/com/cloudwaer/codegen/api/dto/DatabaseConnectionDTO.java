package com.cloudwaer.codegen.api.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 数据库连接配置DTO
 *
 * @author cloudwaer
 */
@Data
public class DatabaseConnectionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 连接ID
     */
    private Long id;

    /**
     * 连接名称
     */
    private String name;

    /**
     * 数据库类型（mysql, postgresql, oracle等）
     */
    private String dbType;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接URL（可选，如果不提供则根据其他参数自动生成）
     */
    private String url;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}

