package ${packageName}.${moduleName}.serve.controller;

import ${packageName}.${moduleName}.api.dto.${entityName}DTO;
import ${packageName}.${moduleName}.serve.service.${entityName}Service;
<#if enablePagination>
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
</#if>
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

<#if enablePagination>import java.util.List;</#if>

/**
 * ${entityComment}管理控制器
 *
 * @author ${author}
 */
@Slf4j
@RestController
@RequestMapping("/${moduleName}/${entityNameCamel}")
@Tag(name = "${entityComment}管理", description = "${entityComment}管理接口")
public class ${entityName}Controller {

    @Autowired
    private ${entityName}Service ${entityNameCamel}Service;

<#if enablePagination>
    /**
     * 获取所有${entityComment}列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有${entityComment}列表", description = "获取所有${entityComment}信息")
    public Result<List<${entityName}DTO>> getAll${entityName}s() {
        List<${entityName}DTO> list = ${entityNameCamel}Service.getAll${entityName}s();
        return Result.success(list);
    }

    /**
     * 分页查询${entityComment}列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询${entityComment}列表", description = "分页获取${entityComment}信息<#if searchFields?size gt 0>，支持关键词搜索</#if>")
    public Result<PageResult<${entityName}DTO>> get${entityName}Page(@RequestParam(value = "current", defaultValue = "1") Long current,
                                                                      @RequestParam(value = "size", defaultValue = "10") Long size,
                                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setCurrent(current);
        pageDTO.setSize(size);
        pageDTO.setKeyword(keyword);
        PageResult<${entityName}DTO> pageResult = ${entityNameCamel}Service.get${entityName}Page(pageDTO);
        return Result.success(pageResult);
    }
</#if>

    /**
     * 根据ID获取${entityComment}
     */
    @GetMapping("/detail")
    @Operation(summary = "根据ID获取${entityComment}", description = "通过${entityComment}ID查询${entityComment}详细信息")
    public Result<${entityName}DTO> get${entityName}ById(@RequestParam("id") Long id) {
        ${entityName}DTO dto = ${entityNameCamel}Service.get${entityName}ById(id);
        return Result.success(dto);
    }

    /**
     * 新增${entityComment}
     */
    @PostMapping("/save")
    @Operation(summary = "新增${entityComment}", description = "创建新的${entityComment}")
    public Result<Boolean> save${entityName}(@RequestBody @Validated ${entityName}DTO ${entityNameCamel}DTO) {
        Boolean result = ${entityNameCamel}Service.save${entityName}(${entityNameCamel}DTO);
        return Result.success(result);
    }

    /**
     * 更新${entityComment}
     */
    @PutMapping("/update")
    @Operation(summary = "更新${entityComment}", description = "更新${entityComment}信息")
    public Result<Boolean> update${entityName}(@RequestBody @Validated ${entityName}DTO ${entityNameCamel}DTO) {
        Boolean result = ${entityNameCamel}Service.update${entityName}(${entityNameCamel}DTO);
        return Result.success(result);
    }

    /**
     * 删除${entityComment}
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除${entityComment}", description = "删除指定${entityComment}")
    public Result<Boolean> delete${entityName}(@RequestParam("id") Long id) {
        Boolean result = ${entityNameCamel}Service.delete${entityName}(id);
        return Result.success(result);
    }
}

