package com.cloudwaer.codegen.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.codegen.serve.entity.DatabaseConnection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库连接Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface DatabaseConnectionMapper extends BaseMapper<DatabaseConnection> {

}
