package com.cloudwaer.codegen.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.codegen.api.dto.*;
import com.cloudwaer.codegen.serve.entity.FormConfig;
import com.cloudwaer.codegen.serve.mapper.FormConfigMapper;
import com.cloudwaer.codegen.serve.service.FormGeneratorService;
import com.cloudwaer.codegen.serve.service.MetadataService;
import com.cloudwaer.common.core.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表单生成服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class FormGeneratorServiceImpl extends ServiceImpl<FormConfigMapper, FormConfig> implements FormGeneratorService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<FormFieldConfigDTO> generateFormFields(TableMetadataDTO tableMetadata) {
        List<FormFieldConfigDTO> formFields = new ArrayList<>();
        List<ColumnMetadataDTO> columns = tableMetadata.getColumns();
        Set<String> primaryKeys = new HashSet<>(tableMetadata.getPrimaryKeys());

        int formOrder = 1;
        int listOrder = 1;
        int rowIndex = 0;
        int currentRowSpan = 0;
        int maxSpan = 24; // Element Plus 栅格布局最大为24

        for (ColumnMetadataDTO column : columns) {
            FormFieldConfigDTO fieldConfig = new FormFieldConfigDTO();
            fieldConfig.setFieldName(column.getColumnName());
            
            // 字段标签：优先使用列注释，如果没有则使用列名
            String label = StringUtils.hasText(column.getColumnComment()) 
                    ? column.getColumnComment() 
                    : toCamelCase(column.getColumnName());
            fieldConfig.setLabel(label);

            // 根据数据类型确定表单字段类型
            fieldConfig.setFieldType(determineFieldType(column));

            // 是否必填：主键和非空字段必填（但自增主键在表单中不显示）
            boolean isPrimaryKey = Boolean.TRUE.equals(column.getPrimaryKey());
            boolean isAutoIncrement = Boolean.TRUE.equals(column.getAutoIncrement());
            boolean isNullable = Boolean.TRUE.equals(column.getNullable());
            
            // 自增主键在表单中不显示，在列表中显示
            if (isPrimaryKey && isAutoIncrement) {
                fieldConfig.setShowInForm(false);
                fieldConfig.setShowInList(true);
                fieldConfig.setRequired(false);
                fieldConfig.setSearchable(true);
            } else if (isPrimaryKey) {
                // 非自增主键在表单中显示但不允许修改（通常是编辑时）
                fieldConfig.setShowInForm(true);
                fieldConfig.setShowInList(true);
                fieldConfig.setRequired(true);
                fieldConfig.setSearchable(true);
            } else {
                // 普通字段
                fieldConfig.setShowInForm(true);
                fieldConfig.setShowInList(true);
                fieldConfig.setRequired(!isNullable);
                // 字符串类型和数字类型可搜索
                fieldConfig.setSearchable(isSearchableType(column.getJavaType()));
            }

            // 设置占位符
            fieldConfig.setPlaceholder("请输入" + label);

            // 列表宽度：根据字段类型设置默认宽度
            fieldConfig.setListWidth(determineListWidth(column));

            // 设置顺序
            fieldConfig.setFormOrder(formOrder++);
            fieldConfig.setListOrder(listOrder++);

            // 栅格布局：每行两个字段（每个字段占12个栅格）
            int span = 12;
            if (isTextareaType(fieldConfig.getFieldType())) {
                // 文本域占满一整行
                span = 24;
            }
            
            fieldConfig.setGridSpan(span);
            
            // 计算行索引
            if (currentRowSpan + span > maxSpan) {
                rowIndex++;
                currentRowSpan = 0;
            }
            fieldConfig.setRowIndex(rowIndex);
            currentRowSpan += span;

            // 设置验证规则
            fieldConfig.setValidationRules(generateValidationRules(column));

            formFields.add(fieldConfig);
        }

        return formFields;
    }

    @Override
    public CodeGenConfigDTO generateFormConfig(Long connectionId, String tableName) {
        // 获取表结构元数据
        TableMetadataDTO tableMetadata = metadataService.getTableMetadata(connectionId, tableName);
        if (tableMetadata == null) {
            throw new BusinessException("表不存在: " + tableName);
        }

        // 生成表单字段配置
        List<FormFieldConfigDTO> formFields = generateFormFields(tableMetadata);

        // 构建代码生成配置
        CodeGenConfigDTO configDTO = new CodeGenConfigDTO();
        configDTO.setTableName(tableName);
        configDTO.setConnectionId(connectionId);

        // 设置默认值
        String entityName = toPascalCase(tableName);
        configDTO.setEntityName(entityName);
        configDTO.setEntityComment(tableMetadata.getTableComment() != null ? tableMetadata.getTableComment() : entityName);
        configDTO.setAuthor("cloudwaer");
        configDTO.setModuleName("admin");
        configDTO.setPackageName("com.cloudwaer.admin");

        // 设置主键字段
        if (!tableMetadata.getPrimaryKeys().isEmpty()) {
            configDTO.setPrimaryKeyField(tableMetadata.getPrimaryKeys().get(0));
        }

        // 设置查询字段：默认所有可搜索的字段
        List<String> queryFields = formFields.stream()
                .filter(FormFieldConfigDTO::getSearchable)
                .map(FormFieldConfigDTO::getFieldName)
                .collect(Collectors.toList());
        configDTO.setQueryFields(queryFields);

        configDTO.setFormFields(formFields);
        configDTO.setEnablePagination(true);
        configDTO.setEnableLogicDelete(false);
        configDTO.setGenerateBackend(true);
        configDTO.setGenerateFrontend(true);
        configDTO.setGeneratePermission(true);

        return configDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateFormConfig(CodeGenConfigDTO configDTO) {
        try {
            FormConfig formConfig = new FormConfig();
            
            // 检查是否存在
            LambdaQueryWrapper<FormConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FormConfig::getTableName, configDTO.getTableName())
                    .eq(FormConfig::getConnectionId, configDTO.getConnectionId());
            FormConfig existingConfig = this.getOne(wrapper);

            if (existingConfig != null) {
                formConfig.setId(existingConfig.getId());
            }

            // 设置基本信息
            BeanUtils.copyProperties(configDTO, formConfig);
            formConfig.setConnectionId(configDTO.getConnectionId());

            // 转换表单字段为JSON
            if (configDTO.getFormFields() != null && !configDTO.getFormFields().isEmpty()) {
                String formFieldsJson = objectMapper.writeValueAsString(configDTO.getFormFields());
                formConfig.setFormFields(formFieldsJson);
            }

            // 转换查询字段为JSON
            if (configDTO.getQueryFields() != null && !configDTO.getQueryFields().isEmpty()) {
                String queryFieldsJson = objectMapper.writeValueAsString(configDTO.getQueryFields());
                formConfig.setQueryFields(queryFieldsJson);
            }

            return this.saveOrUpdate(formConfig);
        } catch (Exception e) {
            log.error("保存表单配置失败", e);
            throw new BusinessException("保存表单配置失败: " + e.getMessage());
        }
    }

    @Override
    public CodeGenConfigDTO getFormConfig(Long connectionId, String tableName) {
        LambdaQueryWrapper<FormConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormConfig::getTableName, tableName)
                .eq(FormConfig::getConnectionId, connectionId)
                .eq(FormConfig::getStatus, 1);
        FormConfig formConfig = this.getOne(wrapper);

        if (formConfig == null) {
            // 如果不存在，自动生成
            return generateFormConfig(connectionId, tableName);
        }

        return convertToDTO(formConfig);
    }

    @Override
    public CodeGenConfigDTO getFormConfigById(Long id) {
        FormConfig formConfig = this.getById(id);
        if (formConfig == null) {
            throw new BusinessException("表单配置不存在");
        }
        return convertToDTO(formConfig);
    }

    /**
     * 根据列元数据确定表单字段类型
     */
    private String determineFieldType(ColumnMetadataDTO column) {
        String javaType = column.getJavaType();
        String dataType = column.getDataType() != null ? column.getDataType().toUpperCase() : "";

        // 日期时间类型
        if (javaType.contains("LocalDateTime") || javaType.contains("LocalDate") || javaType.contains("LocalTime")) {
            return "date";
        }

        // 数字类型
        if (javaType.equals("Integer") || javaType.equals("Long") || javaType.equals("Double") || 
            javaType.equals("Float") || javaType.equals("BigDecimal")) {
            return "number";
        }

        // 布尔类型
        if (javaType.equals("Boolean")) {
            return "switch";
        }

        // 文本类型
        Long columnSize = column.getColumnSize();
        if (columnSize != null && columnSize > 255) {
            // 长文本使用文本域
            return "textarea";
        }

        // 默认使用输入框
        return "input";
    }

    /**
     * 判断是否为文本域类型
     */
    private boolean isTextareaType(String fieldType) {
        return "textarea".equals(fieldType);
    }

    /**
     * 判断字段类型是否可搜索
     */
    private boolean isSearchableType(String javaType) {
        return javaType.equals("String") || 
               javaType.equals("Integer") || 
               javaType.equals("Long") ||
               javaType.contains("LocalDateTime") ||
               javaType.contains("LocalDate");
    }

    /**
     * 根据字段类型确定列表显示宽度
     */
    private Integer determineListWidth(ColumnMetadataDTO column) {
        String javaType = column.getJavaType();
        Long columnSize = column.getColumnSize();

        if (javaType.contains("LocalDateTime") || javaType.contains("LocalDate")) {
            return 180;
        }
        if (javaType.equals("Integer") || javaType.equals("Long") || javaType.equals("Boolean")) {
            return 100;
        }
        if (columnSize != null && columnSize > 100) {
            return 200;
        }
        return 150;
    }

    /**
     * 生成验证规则
     */
    private String generateValidationRules(ColumnMetadataDTO column) {
        List<Map<String, Object>> rules = new ArrayList<>();

        // 必填验证
        if (!Boolean.TRUE.equals(column.getNullable())) {
            Map<String, Object> requiredRule = new HashMap<>();
            requiredRule.put("required", true);
            requiredRule.put("message", "请输入" + (column.getColumnComment() != null ? column.getColumnComment() : column.getColumnName()));
            requiredRule.put("trigger", "blur");
            rules.add(requiredRule);
        }

        // 长度验证（字符串类型）
        if (column.getJavaType().equals("String") && column.getColumnSize() != null) {
            Map<String, Object> lengthRule = new HashMap<>();
            lengthRule.put("max", column.getColumnSize());
            lengthRule.put("message", "长度不能超过" + column.getColumnSize() + "个字符");
            lengthRule.put("trigger", "blur");
            rules.add(lengthRule);
        }

        try {
            return objectMapper.writeValueAsString(rules);
        } catch (Exception e) {
            log.warn("生成验证规则失败", e);
            return "[]";
        }
    }

    /**
     * 将下划线命名转换为驼峰命名
     */
    private String toCamelCase(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1));
                }
            }
        }
        return result.toString();
    }

    /**
     * 将下划线命名转换为帕斯卡命名（首字母大写）
     */
    private String toPascalCase(String name) {
        String camelCase = toCamelCase(name);
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    /**
     * 将实体转换为DTO
     */
    private CodeGenConfigDTO convertToDTO(FormConfig formConfig) {
        CodeGenConfigDTO configDTO = new CodeGenConfigDTO();
        BeanUtils.copyProperties(formConfig, configDTO);
        configDTO.setConnectionId(formConfig.getConnectionId());
        configDTO.setId(formConfig.getId());

        try {
            // 解析表单字段JSON
            if (formConfig.getFormFields() != null && !formConfig.getFormFields().isEmpty()) {
                List<FormFieldConfigDTO> formFields = objectMapper.readValue(
                        formConfig.getFormFields(),
                        new TypeReference<List<FormFieldConfigDTO>>() {}
                );
                configDTO.setFormFields(formFields);
            }

            // 解析查询字段JSON
            if (formConfig.getQueryFields() != null && !formConfig.getQueryFields().isEmpty()) {
                List<String> queryFields = objectMapper.readValue(
                        formConfig.getQueryFields(),
                        new TypeReference<List<String>>() {}
                );
                configDTO.setQueryFields(queryFields);
            }
        } catch (Exception e) {
            log.error("解析表单配置JSON失败", e);
        }

        return configDTO;
    }
}

