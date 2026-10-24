-- =============================================================================
-- 目标库：cloudwaer（sys_permission + 角色授权）
-- 用途：后台「文章导入」按钮权限——blog:article:import
--       接口：POST /blog/article/import（MD/TXT/PDF/DOCX → Markdown）
-- 依赖全量版本：2026-10-25/26 导出（db/cloudwaer.sql）
-- 权限三方：@PermissionCode("blog:article:import") ↔ 本脚本 ↔ cloudwaer-ui/src/config/permission.ts
-- =============================================================================

USE `cloudwaer`;

-- 按钮权限：导入文章（挂在 blog:article 菜单下）
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `route_path`, `parent_id`, `sort`, `icon`, `description`, `create_time`, `create_user`, `update_time`, `update_user`, `status`) VALUES
  (2100000000000000107, 'blog:article:import', '导入文章', 3, '', 2100000000000000102, 5, '', '导入 MD/TXT/PDF/DOCX 为文章草稿（POST /blog/article/import）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);

-- ADMIN（role_id=1）授权
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `create_user`, `update_time`, `update_user`, `status`) VALUES
  (2100000000000000214, 1, 2100000000000000107, '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);
