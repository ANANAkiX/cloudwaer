package ${packageName}.${moduleName}.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
<#if enablePagination>
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
</#if>
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${packageName}.${moduleName}.api.dto.${entityName}DTO;
import ${packageName}.${moduleName}.serve.entity.${entityName};
import ${packageName}.${moduleName}.serve.mapper.${entityName}Mapper;
import ${packageName}.${moduleName}.serve.service.${entityName}Service;
<#if enablePagination>
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
</#if>
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

<#if enablePagination>
import java.util.List;
import java.util.stream.Collectors;
</#if>

/**
 * ${entityComment}服务实现类
 *
 * @author ${author}
 */
@Service
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements ${entityName}Service {

<#if enablePagination>
    @Override
    public List<${entityName}DTO> getAll${entityName}s() {
        List<${entityName}> list = this.list();
        return list.stream().map(item -> {
            ${entityName}DTO dto = new ${entityName}DTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<${entityName}DTO> get${entityName}Page(PageDTO pageDTO) {
        // 创建分页对象
        Page<${entityName}> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<${entityName}> wrapper = new LambdaQueryWrapper<>();
        <#if searchFields?size gt 0>
        if (StringUtils.hasText(pageDTO.getKeyword())) {
            String keyword = pageDTO.getKeyword().trim();
            wrapper.and(w -> {
                <#list searchFields as searchField>
                <#if searchField.javaType == "String">
                w.like(${entityName}::get${searchField.fieldName?cap_first}, keyword)<#if searchField_has_next>.or()</#if>
                <#else>
                w.eq(${entityName}::get${searchField.fieldName?cap_first}, keyword)<#if searchField_has_next>.or()</#if>
                </#if>
                </#list>
            });
        }
        </#if>
        
        // 分页查询
        IPage<${entityName}> pageResult = this.page(page, wrapper);
        
        // 转换为DTO
        List<${entityName}DTO> dtoList = pageResult.getRecords().stream().map(item -> {
            ${entityName}DTO dto = new ${entityName}DTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return new PageResult<>(dtoList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }
</#if>

    @Override
    public ${entityName}DTO get${entityName}ById(Long id) {
        ${entityName} entity = this.getById(id);
        if (entity == null) {
            <#if enablePagination>throw new BusinessException("${entityComment}不存在");<#else>return null;</#if>
        }
        ${entityName}DTO dto = new ${entityName}DTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save${entityName}(${entityName}DTO ${entityNameCamel}DTO) {
        ${entityName} entity = new ${entityName}();
        BeanUtils.copyProperties(${entityNameCamel}DTO, entity);
        return this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update${entityName}(${entityName}DTO ${entityNameCamel}DTO) {
        ${entityName} entity = this.getById(${entityNameCamel}DTO.getId());
        if (entity == null) {
            <#if enablePagination>throw new BusinessException("${entityComment}不存在");<#else>return false;</#if>
        }
        BeanUtils.copyProperties(${entityNameCamel}DTO, entity);
        return this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete${entityName}(Long id) {
        return this.removeById(id);
    }
}

