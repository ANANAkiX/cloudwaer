package com.cloudwaer.codegen.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 代码生成配置DTO
 *
 * @author cloudwaer
 */
@Data
public class CodeGenConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID（更新时使用）
     */
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据库连接ID
     */
    private Long connectionId;

    /**
     * 模块名称（如：admin, system等）
     */
    private String moduleName;

    /**
     * 包名（如：com.cloudwaer.admin）
     */
    private String packageName;

    /**
     * 作者
     */
    private String author;

    /**
     * 实体类名称（如：User）
     */
    private String entityName;

    /**
     * 实体类注释
     */
    private String entityComment;

    /**
     * 是否生成后端代码
     */
    private Boolean generateBackend;

    /**
     * 是否生成前端代码
     */
    private Boolean generateFrontend;

    /**
     * 是否生成权限SQL
     */
    private Boolean generatePermission;

    /**
     * 后端代码生成路径
     */
    private String backendPath;

    /**
     * 前端代码生成路径
     */
    private String frontendPath;

    /**
     * 是否启用分页查询
     */
    private Boolean enablePagination;

    /**
     * 是否启用逻辑删除
     */
    private Boolean enableLogicDelete;

    /**
     * 表单字段配置列表
     */
    private List<FormFieldConfigDTO> formFields;

    /**
     * 查询字段列表（用于搜索）
     */
    private List<String> queryFields;

    /**
     * 主键字段名
     */
    private String primaryKeyField;
}

