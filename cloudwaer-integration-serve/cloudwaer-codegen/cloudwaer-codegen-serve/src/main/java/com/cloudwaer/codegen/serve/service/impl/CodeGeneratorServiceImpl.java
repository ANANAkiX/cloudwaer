package com.cloudwaer.codegen.serve.service.impl;

import com.cloudwaer.codegen.api.dto.*;
import com.cloudwaer.codegen.serve.service.CodeGeneratorService;
import com.cloudwaer.codegen.serve.service.MetadataService;
import com.cloudwaer.codegen.serve.util.CodeGenUtil;
import com.cloudwaer.common.core.exception.BusinessException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class CodeGeneratorServiceImpl implements CodeGeneratorService {

	@Autowired
	private MetadataService metadataService;

	@Autowired
	private CodeGenUtil codeGenUtil;

	@Override
	public Map<String, String> generateBackendCode(CodeGenConfigDTO configDTO) {
		try {
			// 获取表结构元数据
			TableMetadataDTO tableMetadata = metadataService.getTableMetadata(configDTO.getConnectionId(),
					configDTO.getTableName());
			if (tableMetadata == null) {
				throw new BusinessException("表不存在: " + configDTO.getTableName());
			}

			// 准备模板数据
			Map<String, Object> dataModel = buildDataModel(configDTO, tableMetadata);

			// 生成代码
			Map<String, String> generatedFiles = new LinkedHashMap<>();

			// 生成 Entity（在serve模块）
			generatedFiles.put(buildFilePath(configDTO, "entity", configDTO.getEntityName() + ".java", false),
					generateCode("backend/entity.ftl", dataModel));

			// 生成 Mapper（在serve模块）
			generatedFiles.put(buildFilePath(configDTO, "mapper", configDTO.getEntityName() + "Mapper.java", false),
					generateCode("backend/mapper.ftl", dataModel));

			// 生成 DTO（在api模块）
			generatedFiles.put(buildFilePath(configDTO, "dto", configDTO.getEntityName() + "DTO.java", true),
					generateCode("backend/dto.ftl", dataModel));

			// 生成 Service 接口（在serve模块）
			generatedFiles.put(buildFilePath(configDTO, "service", configDTO.getEntityName() + "Service.java", false),
					generateCode("backend/service.ftl", dataModel));

			// 生成 ServiceImpl（在serve模块）
			generatedFiles.put(
					buildFilePath(configDTO, "service/impl", configDTO.getEntityName() + "ServiceImpl.java", false),
					generateCode("backend/serviceImpl.ftl", dataModel));

			// 生成 Controller（在serve模块）
			generatedFiles.put(
					buildFilePath(configDTO, "controller", configDTO.getEntityName() + "Controller.java", false),
					generateCode("backend/controller.ftl", dataModel));

			return generatedFiles;
		}
		catch (Exception e) {
			log.error("生成后端代码失败", e);
			throw new BusinessException("生成后端代码失败: " + e.getMessage());
		}
	}

	@Override
	public Map<String, String> generateFrontendCode(CodeGenConfigDTO configDTO) {
		try {
			// 获取表结构元数据
			TableMetadataDTO tableMetadata = metadataService.getTableMetadata(configDTO.getConnectionId(),
					configDTO.getTableName());
			if (tableMetadata == null) {
				throw new BusinessException("表不存在: " + configDTO.getTableName());
			}

			// 准备模板数据
			Map<String, Object> dataModel = buildDataModel(configDTO, tableMetadata);

			// 生成代码
			Map<String, String> generatedFiles = new LinkedHashMap<>();

			// 生成 API 文件
			String apiFileName = codeGenUtil.toCamelCase(configDTO.getEntityName()) + ".ts";
			generatedFiles.put(buildFrontendFilePath(configDTO, "api", apiFileName),
					generateCode("frontend/api.ftl", dataModel));

			// 生成 Vue 页面
			String vueFileName = configDTO.getEntityName() + ".vue";
			generatedFiles.put(buildFrontendFilePath(configDTO, "views/" + configDTO.getModuleName(), vueFileName),
					generateCode("frontend/view.ftl", dataModel));

			return generatedFiles;
		}
		catch (Exception e) {
			log.error("生成前端代码失败", e);
			throw new BusinessException("生成前端代码失败: " + e.getMessage());
		}
	}

	@Override
	public String generatePermissionSql(CodeGenConfigDTO configDTO) {
		try {
			// 准备模板数据
			Map<String, Object> dataModel = new HashMap<>();
			dataModel.put("tableName", configDTO.getTableName());
			dataModel.put("entityName", configDTO.getEntityName());
			dataModel.put("entityComment", StringUtils.hasText(configDTO.getEntityComment())
					? configDTO.getEntityComment() : configDTO.getEntityName());
			dataModel.put("moduleName", configDTO.getModuleName());

			// 实体类名称（驼峰命名）
			String entityNameCamel = codeGenUtil.toCamelCase(configDTO.getEntityName());
			dataModel.put("entityNameCamel", entityNameCamel);

			// 是否启用分页
			dataModel.put("enablePagination", Boolean.TRUE.equals(configDTO.getEnablePagination()));

			// 生成权限SQL
			return generateCode("permission/permission.ftl", dataModel);
		}
		catch (Exception e) {
			log.error("生成权限SQL失败", e);
			throw new BusinessException("生成权限SQL失败: " + e.getMessage());
		}
	}

	/**
	 * 构建模板数据模型
	 */
	private Map<String, Object> buildDataModel(CodeGenConfigDTO configDTO, TableMetadataDTO tableMetadata) {
		Map<String, Object> dataModel = new HashMap<>();

		// 基本信息
		dataModel.put("packageName", configDTO.getPackageName());
		dataModel.put("moduleName", configDTO.getModuleName());
		dataModel.put("author", configDTO.getAuthor());
		dataModel.put("entityName", configDTO.getEntityName());
		dataModel.put("entityComment", configDTO.getEntityComment());
		dataModel.put("tableName", configDTO.getTableName());
		dataModel.put("primaryKeyField", configDTO.getPrimaryKeyField());
		dataModel.put("enablePagination", Boolean.TRUE.equals(configDTO.getEnablePagination()));
		dataModel.put("enableLogicDelete", Boolean.TRUE.equals(configDTO.getEnableLogicDelete()));

		// 实体类名称（驼峰命名）
		String entityNameCamel = codeGenUtil.toCamelCase(configDTO.getEntityName());
		dataModel.put("entityNameCamel", entityNameCamel);

		// 包路径
		String packagePath = configDTO.getPackageName().replace(".", "/");
		dataModel.put("packagePath", packagePath);

		// BaseEntity中已包含的字段，不需要在生成的实体类中重复定义
		Set<String> baseEntityFields = new HashSet<>();
		baseEntityFields.add("id");
		baseEntityFields.add("create_time");
		baseEntityFields.add("create_user");
		baseEntityFields.add("update_time");
		baseEntityFields.add("update_user");
		baseEntityFields.add("status");

		// 字段列表（只包含需要在实体类中显示的字段，排除BaseEntity已有的字段）
		List<Map<String, Object>> fields = new ArrayList<>();
		List<FormFieldConfigDTO> formFields = configDTO.getFormFields();
		List<ColumnMetadataDTO> columns = tableMetadata.getColumns();

		Map<String, ColumnMetadataDTO> columnMap = columns.stream()
			.collect(Collectors.toMap(ColumnMetadataDTO::getColumnName, col -> col));

		for (FormFieldConfigDTO formField : formFields) {
			if (Boolean.FALSE.equals(formField.getShowInForm()) && Boolean.FALSE.equals(formField.getShowInList())) {
				continue; // 既不显示在表单也不显示在列表中的字段跳过
			}

			ColumnMetadataDTO column = columnMap.get(formField.getFieldName());
			if (column == null) {
				continue;
			}

			// 排除BaseEntity中已有的字段
			String columnName = column.getColumnName().toLowerCase();
			if (baseEntityFields.contains(columnName)) {
				continue;
			}

			Map<String, Object> field = new HashMap<>();
			field.put("fieldName", codeGenUtil.toCamelCase(column.getColumnName()));
			field.put("columnName", column.getColumnName());
			field.put("javaType", column.getJavaType());
			field.put("columnComment", StringUtils.hasText(column.getColumnComment()) ? column.getColumnComment()
					: column.getColumnName());
			field.put("nullable", Boolean.TRUE.equals(column.getNullable()));
			field.put("primaryKey", Boolean.TRUE.equals(column.getPrimaryKey()));
			field.put("autoIncrement", Boolean.TRUE.equals(column.getAutoIncrement()));

			fields.add(field);
		}
		dataModel.put("fields", fields);

		// 查询字段列表
		List<String> queryFields = configDTO.getQueryFields();
		List<Map<String, Object>> searchFields = new ArrayList<>();
		if (queryFields != null && !queryFields.isEmpty()) {
			for (String queryField : queryFields) {
				ColumnMetadataDTO column = columnMap.get(queryField);
				if (column != null) {
					Map<String, Object> searchField = new HashMap<>();
					searchField.put("fieldName", codeGenUtil.toCamelCase(column.getColumnName()));
					searchField.put("columnName", column.getColumnName());
					searchField.put("javaType", column.getJavaType());
					searchField.put("columnComment", StringUtils.hasText(column.getColumnComment())
							? column.getColumnComment() : column.getColumnName());
					searchFields.add(searchField);
				}
			}
		}
		dataModel.put("searchFields", searchFields);

		// 表单字段配置
		dataModel.put("formFields", formFields);

		return dataModel;
	}

	/**
	 * 生成代码（使用FreeMarker模板）
	 */
	private String generateCode(String templateName, Map<String, Object> dataModel) {
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
			cfg.setClassForTemplateLoading(this.getClass(), "/templates/codegen");
			cfg.setDefaultEncoding("UTF-8");

			Template template = cfg.getTemplate(templateName);
			StringWriter writer = new StringWriter();
			template.process(dataModel, writer);
			return writer.toString();
		}
		catch (IOException | TemplateException e) {
			log.error("生成代码失败: templateName={}", templateName, e);
			throw new BusinessException("生成代码失败: " + e.getMessage());
		}
	}

	/**
	 * 构建后端文件路径
	 */
	private String buildFilePath(CodeGenConfigDTO configDTO, String subPath, String fileName) {
		return buildFilePath(configDTO, subPath, fileName, false);
	}

	/**
	 * 构建后端文件路径
	 */
	private String buildFilePath(CodeGenConfigDTO configDTO, String subPath, String fileName, boolean isApi) {
		String packagePath = configDTO.getPackageName().replace(".", "/");
		String modulePath = isApi ? configDTO.getModuleName() + "-api" : configDTO.getModuleName() + "-serve";

		// 构建完整路径：
		// API模块：cloudwaer-admin/cloudwaer-admin-api/src/main/java/com/cloudwaer/admin/api/dto/UserDTO.java
		// Serve模块：cloudwaer-admin/cloudwaer-admin-serve/src/main/java/com/cloudwaer/admin/serve/entity/User.java
		// Controller：cloudwaer-admin/cloudwaer-admin-serve/src/main/java/com/cloudwaer/admin/serve/controller/UserController.java
		// ServiceImpl：cloudwaer-admin/cloudwaer-admin-serve/src/main/java/com/cloudwaer/admin/serve/service/impl/UserServiceImpl.java
		String subPackage = isApi ? "api" : "serve";

		return String.format("%s/src/main/java/%s/%s/%s/%s", modulePath, packagePath, subPackage, subPath, fileName);
	}

	/**
	 * 构建前端文件路径
	 */
	private String buildFrontendFilePath(CodeGenConfigDTO configDTO, String subPath, String fileName) {
		return String.format("src/%s/%s", subPath, fileName);
	}

	@Override
	public InputStream generateAllCodeAsZip(CodeGenConfigDTO configDTO) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8);

			try {
				// 生成后端代码
				if (Boolean.TRUE.equals(configDTO.getGenerateBackend())) {
					try {
						Map<String, String> backendFiles = generateBackendCode(configDTO);
						for (Map.Entry<String, String> entry : backendFiles.entrySet()) {
							addFileToZip(zos, "backend/" + entry.getKey(), entry.getValue());
						}
					}
					catch (Exception e) {
						log.error("生成后端代码失败", e);
						// 添加错误信息文件
						addFileToZip(zos, "backend/ERROR.txt", "生成后端代码失败: " + e.getMessage());
					}
				}

				// 生成前端代码
				if (Boolean.TRUE.equals(configDTO.getGenerateFrontend())) {
					try {
						Map<String, String> frontendFiles = generateFrontendCode(configDTO);
						for (Map.Entry<String, String> entry : frontendFiles.entrySet()) {
							addFileToZip(zos, "frontend/" + entry.getKey(), entry.getValue());
						}
					}
					catch (Exception e) {
						log.error("生成前端代码失败", e);
						// 添加错误信息文件
						addFileToZip(zos, "frontend/ERROR.txt", "生成前端代码失败: " + e.getMessage());
					}
				}

				// 生成权限SQL
				if (Boolean.TRUE.equals(configDTO.getGeneratePermission())) {
					try {
						String permissionSql = generatePermissionSql(configDTO);
						String sqlFileName = "permission_" + configDTO.getTableName() + ".sql";
						addFileToZip(zos, "sql/" + sqlFileName, permissionSql);
					}
					catch (Exception e) {
						log.error("生成权限SQL失败", e);
						// 添加错误信息文件
						addFileToZip(zos, "sql/ERROR.txt", "生成权限SQL失败: " + e.getMessage());
					}
				}

				// 添加README文件
				StringBuilder readme = new StringBuilder();
				readme.append("# 代码生成结果\n\n");
				readme.append("## 生成信息\n\n");
				readme.append("- 表名: ").append(configDTO.getTableName()).append("\n");
				readme.append("- 实体名: ").append(configDTO.getEntityName()).append("\n");
				readme.append("- 模块名: ").append(configDTO.getModuleName()).append("\n");
				readme.append("- 包名: ").append(configDTO.getPackageName()).append("\n");
				readme.append("- 作者: ").append(configDTO.getAuthor()).append("\n\n");
				readme.append("## 生成时间\n\n");
				readme.append("- ").append(new java.util.Date()).append("\n\n");
				readme.append("## 目录说明\n\n");
				readme.append("- backend/: 后端代码文件\n");
				readme.append("- frontend/: 前端代码文件\n");
				readme.append("- sql/: SQL脚本文件\n");
				addFileToZip(zos, "README.md", readme.toString());

			}
			finally {
				zos.close();
			}

			return new ByteArrayInputStream(baos.toByteArray());
		}
		catch (Exception e) {
			log.error("生成ZIP压缩包失败", e);
			throw new BusinessException("生成ZIP压缩包失败: " + e.getMessage());
		}
	}

	@Override
	public Map<String, Object> previewGeneratedFiles(CodeGenConfigDTO configDTO) {
		Map<String, Object> result = new java.util.HashMap<>();
		Map<String, String> backendFiles = new java.util.LinkedHashMap<>();
		Map<String, String> frontendFiles = new java.util.LinkedHashMap<>();
		Map<String, String> sqlFiles = new java.util.LinkedHashMap<>();

		try {
			// 预览后端代码文件路径和内容（返回与ZIP中一致的相对路径）
			if (Boolean.TRUE.equals(configDTO.getGenerateBackend())) {
				try {
					Map<String, String> generatedFiles = generateBackendCode(configDTO);
					backendFiles.putAll(generatedFiles);
				}
				catch (Exception e) {
					log.error("生成后端代码预览失败", e);
					// 即使生成失败，也添加错误信息
					backendFiles.put("ERROR.txt", "生成后端代码失败: " + e.getMessage());
				}
			}

			// 预览前端代码文件路径和内容（返回与ZIP中一致的相对路径）
			if (Boolean.TRUE.equals(configDTO.getGenerateFrontend())) {
				try {
					Map<String, String> generatedFiles = generateFrontendCode(configDTO);
					frontendFiles.putAll(generatedFiles);
				}
				catch (Exception e) {
					log.error("生成前端代码预览失败", e);
					// 即使生成失败，也添加错误信息
					frontendFiles.put("ERROR.txt", "生成前端代码失败: " + e.getMessage());
				}
			}

			// 预览权限SQL文件路径和内容
			if (Boolean.TRUE.equals(configDTO.getGeneratePermission())) {
				try {
					String permissionSql = generatePermissionSql(configDTO);
					String sqlFileName = "permission_" + configDTO.getTableName() + ".sql";
					sqlFiles.put(sqlFileName, permissionSql);
				}
				catch (Exception e) {
					log.error("生成权限SQL预览失败", e);
					// 即使生成失败，也添加错误信息
					sqlFiles.put("ERROR.txt", "生成权限SQL失败: " + e.getMessage());
				}
			}

			result.put("backend", backendFiles);
			result.put("frontend", frontendFiles);
			result.put("sql", sqlFiles);
		}
		catch (Exception e) {
			log.error("预览生成文件列表失败", e);
			throw new BusinessException("预览生成文件列表失败: " + e.getMessage());
		}

		return result;
	}

	/**
	 * 添加文件到ZIP压缩包
	 */
	private void addFileToZip(ZipOutputStream zos, String fileName, String content) throws IOException {
		ZipEntry entry = new ZipEntry(fileName);
		zos.putNextEntry(entry);
		zos.write(content.getBytes(StandardCharsets.UTF_8));
		zos.closeEntry();
	}

}
