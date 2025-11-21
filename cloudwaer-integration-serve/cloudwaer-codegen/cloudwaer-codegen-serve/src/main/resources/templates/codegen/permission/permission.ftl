-- ${entityComment}权限SQL（默认CRUD权限）
-- 表名: ${tableName}
-- 实体类: ${entityName}
-- 模块: ${moduleName}
-- 生成时间: ${.now?string("yyyy-MM-dd HH:mm:ss")}

-- ============================================
-- ${entityComment}管理权限（操作权限）
-- ============================================

<#if enablePagination>
-- 查询${entityComment}列表权限（分页查询）
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:view', '查询${entityComment}列表', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/page', 'GET', NULL, 1, '查询${entityComment}列表权限（分页查询）', 1, NOW(), NOW());

-- 获取所有${entityComment}列表权限（非分页）
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:list', '获取所有${entityComment}列表', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/list', 'GET', NULL, 2, '获取所有${entityComment}列表权限', 1, NOW(), NOW());
<#else>
-- 获取所有${entityComment}列表权限
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:list', '获取所有${entityComment}列表', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/list', 'GET', NULL, 1, '获取所有${entityComment}列表权限', 1, NOW(), NOW());
</#if>

-- 查询${entityComment}详情权限
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:detail', '查询${entityComment}详情', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/detail', 'GET', NULL, ${enablePagination?then(3, 2)}, '查询${entityComment}详情权限', 1, NOW(), NOW());

-- 新增${entityComment}权限
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:add', '新增${entityComment}', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/save', 'POST', NULL, ${enablePagination?then(4, 3)}, '新增${entityComment}权限', 1, NOW(), NOW());

-- 更新${entityComment}权限
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:edit', '更新${entityComment}', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/update', 'PUT', NULL, ${enablePagination?then(5, 4)}, '更新${entityComment}权限', 1, NOW(), NOW());

-- 删除${entityComment}权限
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `parent_id`, `route_path`, `api_url`, `http_method`, `icon`, `sort`, `description`, `status`, `create_time`, `update_time`) 
VALUES (NULL, '${moduleName}:${entityNameCamel}:delete', '删除${entityComment}', 3, NULL, NULL, '/${moduleName}/${entityNameCamel}/delete', 'DELETE', NULL, ${enablePagination?then(6, 5)}, '删除${entityComment}权限', 1, NOW(), NOW());

-- ============================================
-- 使用说明：
-- 1. permission_type: 3 表示操作权限（1-菜单，2-页面，3-操作）
-- 2. 默认所有权限的 status 为 1（启用）
-- 3. 如果需要关联到菜单权限，请设置 parent_id 为对应菜单权限的ID
-- 4. 可以根据实际需求调整 sort 排序值
-- ============================================

