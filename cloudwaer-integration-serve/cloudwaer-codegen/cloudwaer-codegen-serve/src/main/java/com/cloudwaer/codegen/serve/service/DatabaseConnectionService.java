package com.cloudwaer.codegen.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.codegen.api.dto.DatabaseConnectionDTO;
import com.cloudwaer.codegen.serve.entity.DatabaseConnection;

import java.sql.Connection;

/**
 * 数据库连接服务接口
 *
 * @author cloudwaer
 */
public interface DatabaseConnectionService extends IService<DatabaseConnection> {

    /**
     * 测试数据库连接
     *
     * @param connectionDTO 连接配置
     * @return 是否连接成功
     */
    Boolean testConnection(DatabaseConnectionDTO connectionDTO);

    /**
     * 获取数据库连接
     *
     * @param connectionId 连接ID
     * @return 数据库连接
     */
    Connection getConnection(Long connectionId);

    /**
     * 获取数据库连接（通过配置）
     *
     * @param connectionDTO 连接配置
     * @return 数据库连接
     */
    Connection getConnection(DatabaseConnectionDTO connectionDTO);

    /**
     * 关闭数据库连接
     *
     * @param connection 数据库连接
     */
    void closeConnection(Connection connection);
}

