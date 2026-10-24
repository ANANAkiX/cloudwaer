-- =============================================================================
-- 目标库：cloudwaer_blog（建表+配置种子） / cloudwaer（sys_permission + 角色授权）
-- 用途：后台「网站设置」功能——blog_site_setting key-value 表 + 默认配置种子
--       + 菜单/按钮权限码（blog:setting / blog:setting:view / blog:setting:edit）
-- 依赖全量版本：2026-10-25 导出（db/cloudwaer_blog.sql、db/cloudwaer.sql）
-- 权限三方：@PermissionCode ↔ 本脚本 sys_permission ↔ cloudwaer-ui/src/config/permission.ts
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1) cloudwaer_blog：站点设置表（key-value）
-- ---------------------------------------------------------------------------
USE `cloudwaer_blog`;

DROP TABLE IF EXISTS `blog_site_setting`;
CREATE TABLE `blog_site_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `setting_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `setting_value` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置值',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_setting_key`(`setting_key` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客站点设置表（key-value）' ROW_FORMAT = DYNAMIC;

-- 默认配置种子
INSERT INTO `blog_site_setting` (`id`, `setting_key`, `setting_value`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`) VALUES
  (2100000000000000051, 'siteTitle', 'CloudWaer 博客', '站点标题', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000052, 'siteDescription', '', '站点副标题/描述', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000053, 'favicon', '', '站点图标 URL（favicon/icon）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000054, 'enableSearch', '1', '是否开启搜索：0-关闭，1-开启', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000055, 'layout', 'default', '网站布局：default / compact / magazine', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000056, 'footerText', '', '页脚文案', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);

-- ---------------------------------------------------------------------------
-- 2) cloudwaer：菜单 + 按钮权限 + ADMIN 授权
-- ---------------------------------------------------------------------------
USE `cloudwaer`;

-- 菜单：博客管理 → 网站设置
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `permission_type`, `route_path`, `parent_id`, `sort`, `icon`, `description`, `create_time`, `create_user`, `update_time`, `update_user`, `status`) VALUES
  (2100000000000000160, 'blog:setting', '网站设置', 2, '/blog/sitesetting', 2100000000000000101, 6, 'Setting', '博客站点设置页（GET /blog/setting）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000161, 'blog:setting:view', '查看设置', 3, '', 2100000000000000160, 1, '', '站点设置读取（GET /blog/setting；公开 GET /blog/open/setting）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000162, 'blog:setting:edit', '保存设置', 3, '', 2100000000000000160, 2, '', '保存站点设置（POST /blog/setting/save）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);

-- ADMIN（role_id=1）授权
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `create_user`, `update_time`, `update_user`, `status`) VALUES
  (2100000000000000211, 1, 2100000000000000160, '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000212, 1, 2100000000000000161, '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1),
  (2100000000000000213, 1, 2100000000000000162, '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
