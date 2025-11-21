package ${packageName}.${moduleName}.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * ${entityComment}DTO
 *
 * @author ${author}
 */
@Data
public class ${entityName}DTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ${entityComment}ID（序列化为字符串，避免前端精度丢失）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

<#list fields as field>
<#if !field.primaryKey || !field.autoIncrement>
    /**
     * ${field.columnComment}
     */
    private ${field.javaType} ${field.fieldName};
</#if>
</#list>
}

