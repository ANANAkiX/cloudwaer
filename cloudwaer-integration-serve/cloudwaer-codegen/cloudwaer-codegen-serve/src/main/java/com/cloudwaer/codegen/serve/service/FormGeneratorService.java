package com.cloudwaer.codegen.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.codegen.api.dto.*;
import com.cloudwaer.codegen.serve.entity.FormConfig;

import java.util.List;

/**
 * 表单生成服务接口
 *
 * @author cloudwaer
 */
public interface FormGeneratorService extends IService<FormConfig> {

	/**
	 * 根据表结构元数据生成表单字段配置
	 * @param tableMetadata 表结构元数据
	 * @return 表单字段配置列表
	 */
	List<FormFieldConfigDTO> generateFormFields(TableMetadataDTO tableMetadata);

	/**
	 * 根据表名和连接ID生成表单配置
	 * @param connectionId 连接ID
	 * @param tableName 表名
	 * @return 代码生成配置DTO
	 */
	CodeGenConfigDTO generateFormConfig(Long connectionId, String tableName);

	/**
	 * 保存或更新表单配置
	 * @param configDTO 代码生成配置DTO
	 * @return 是否成功
	 */
	Boolean saveOrUpdateFormConfig(CodeGenConfigDTO configDTO);

	/**
	 * 根据表名和连接ID获取表单配置
	 * @param connectionId 连接ID
	 * @param tableName 表名
	 * @return 代码生成配置DTO
	 */
	CodeGenConfigDTO getFormConfig(Long connectionId, String tableName);

	/**
	 * 根据ID获取表单配置
	 * @param id 配置ID
	 * @return 代码生成配置DTO
	 */
	CodeGenConfigDTO getFormConfigById(Long id);

}
