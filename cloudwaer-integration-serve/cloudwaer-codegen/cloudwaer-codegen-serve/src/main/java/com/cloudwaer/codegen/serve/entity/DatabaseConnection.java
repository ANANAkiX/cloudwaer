package com.cloudwaer.codegen.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库连接配置实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_database_connection")
public class DatabaseConnection extends BaseEntity {
    /**
     * 连接名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 数据库类型（mysql, postgresql, oracle等）
     */
    private String dbType;

    /**
     * 主机地址
     */
    @TableField("`host`")
    private String host;

    /**
     * 端口号
     */
    @TableField("`port`")
    private Integer port;

    /**
     * 数据库名称
     */
    @TableField("`database`")
    private String database;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField("`password`")
    private String password;

    /**
     * 连接URL（可选）
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

