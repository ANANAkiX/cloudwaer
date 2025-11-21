package ${packageName}.${moduleName}.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ${packageName}.${moduleName}.api.dto.${entityName}DTO;
import ${packageName}.${moduleName}.serve.entity.${entityName};
<#if enablePagination>
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
</#if>

<#if enablePagination>import java.util.List;</#if>

/**
 * ${entityComment}服务接口
 *
 * @author ${author}
 */
public interface ${entityName}Service extends IService<${entityName}> {

<#if enablePagination>
    /**
     * 获取所有${entityComment}列表
     *
     * @return ${entityComment}列表
     */
    List<${entityName}DTO> getAll${entityName}s();

    /**
     * 分页查询${entityComment}列表
     *
     * @param pageDTO 分页参数
     * @return 分页结果
     */
    PageResult<${entityName}DTO> get${entityName}Page(PageDTO pageDTO);
</#if>

    /**
     * 根据ID获取${entityComment}
     *
     * @param id ${entityComment}ID
     * @return ${entityComment}DTO
     */
    ${entityName}DTO get${entityName}ById(Long id);

    /**
     * 保存${entityComment}
     *
     * @param ${entityNameCamel}DTO ${entityComment}DTO
     * @return 是否成功
     */
    Boolean save${entityName}(${entityName}DTO ${entityNameCamel}DTO);

    /**
     * 更新${entityComment}
     *
     * @param ${entityNameCamel}DTO ${entityComment}DTO
     * @return 是否成功
     */
    Boolean update${entityName}(${entityName}DTO ${entityNameCamel}DTO);

    /**
     * 删除${entityComment}
     *
     * @param id ${entityComment}ID
     * @return 是否成功
     */
    Boolean delete${entityName}(Long id);
}

