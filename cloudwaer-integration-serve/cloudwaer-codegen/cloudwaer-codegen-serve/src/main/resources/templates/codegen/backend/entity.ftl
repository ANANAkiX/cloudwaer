package ${packageName}.${moduleName}.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${entityComment}实体
 *
 * @author ${author}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("${tableName}")
public class ${entityName} extends BaseEntity {

<#list fields as field>
    /**
     * ${field.columnComment}
     */
    <#if field.primaryKey>
    @com.baomidou.mybatisplus.annotation.TableId(type = com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID)
    </#if>
    <#if !field.nullable && !field.primaryKey>
    @com.baomidou.mybatisplus.annotation.TableField("`${field.columnName}`")
    </#if>
    private ${field.javaType} ${field.fieldName};

</#list>
}

