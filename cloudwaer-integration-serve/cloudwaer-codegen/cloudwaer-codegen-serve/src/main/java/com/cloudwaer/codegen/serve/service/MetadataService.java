package com.cloudwaer.codegen.serve.service;

import com.cloudwaer.codegen.api.dto.TableMetadataDTO;

import java.util.List;

/**
 * 数据库元数据服务接口
 *
 * @author cloudwaer
 */
public interface MetadataService {

	/**
	 * 获取数据库中的所有表列表
	 * @param connectionId 连接ID
	 * @return 表名列表
	 */
	List<String> getTableList(Long connectionId);

	/**
	 * 获取表结构元数据
	 * @param connectionId 连接ID
	 * @param tableName 表名
	 * @return 表结构元数据
	 */
	TableMetadataDTO getTableMetadata(Long connectionId, String tableName);

	/**
	 * 获取多个表的结构元数据
	 * @param connectionId 连接ID
	 * @param tableNames 表名列表
	 * @return 表结构元数据列表
	 */
	List<TableMetadataDTO> getTableMetadataList(Long connectionId, List<String> tableNames);

}
