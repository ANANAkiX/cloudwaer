import request from './request'

/**
 * ${entityComment}信息
 */
export interface ${entityName}Info {
  id?: string | number
<#list fields as field>
<#if !field.primaryKey || !field.autoIncrement>
  ${field.fieldName}<#if field.nullable>?</#if>: <#if field.javaType == "Long">string | number<#elseif field.javaType == "Integer">number<#elseif field.javaType == "Boolean">boolean<#elseif field.javaType == "java.time.LocalDateTime">string<#elseif field.javaType == "java.time.LocalDate">string<#elseif field.javaType == "java.time.LocalTime">string<#elseif field.javaType == "java.math.BigDecimal">number<#else>string</#if>
</#if>
</#list>
}

<#if enablePagination>
/**
 * 获取所有${entityComment}列表
 */
export function get${entityName}List(): Promise<${entityName}Info[]> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/list',
    method: 'get'
  })
}

/**
 * 分页查询${entityComment}列表
 */
export function get${entityName}Page(params: {
  current: number
  size: number
  keyword?: string
}): Promise<{
  records: ${entityName}Info[]
  total: number
  current: number
  size: number
  pages: number
}> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/page',
    method: 'get',
    params
  })
}
</#if>

/**
 * 根据ID获取${entityComment}详情
 */
export function get${entityName}ById(id: string | number): Promise<${entityName}Info> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/detail',
    method: 'get',
    params: { id }
  })
}

/**
 * 新增${entityComment}
 */
export function save${entityName}(data: ${entityName}Info): Promise<void> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/save',
    method: 'post',
    data
  })
}

/**
 * 更新${entityComment}
 */
export function update${entityName}(data: ${entityName}Info): Promise<void> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/update',
    method: 'put',
    data
  })
}

/**
 * 删除${entityComment}
 */
export function delete${entityName}(id: string | number): Promise<void> {
  return request({
    url: '/${moduleName}/${entityNameCamel}/delete',
    method: 'delete',
    data: { id }
  })
}

