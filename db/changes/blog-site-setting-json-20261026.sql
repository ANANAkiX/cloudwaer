-- 目标库：cloudwaer_blog
-- 用途：网站设置 JSON 高度定制化——blog_site_setting 增加 nav / pages / articleLayout / homeLayout
--       四个 JSON 键；setting_value 扩为 TEXT（pages.about.content 可能超 varchar(1024)）
-- 依赖全量版本：db/cloudwaer_blog.sql（2026-10-25 网站设置版）
-- 对应前端/后端：SiteSettingDTO 扩展 + SiteSetting.vue 分组 Tab + BlogLayout 可配置导航 + Article TOC 位置

USE `cloudwaer_blog`;

-- 1) value 列扩为 TEXT，容纳页面正文等 JSON
ALTER TABLE `blog_site_setting`
  MODIFY COLUMN `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值（简单项存字面量，复杂项存 JSON 字符串）';

-- 2) 默认 JSON 配置种子（已存在同 key 时跳过，便于重复执行）
INSERT INTO `blog_site_setting` (`id`, `setting_key`, `setting_value`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`)
SELECT * FROM (SELECT 2100000000000000057, 'nav',
  '[{"key":"home","label":"首页","path":"/","visible":true,"order":1},{"key":"forum","label":"微论坛","path":"/forum","visible":true,"order":2},{"key":"archive","label":"归档","path":"/archive","visible":true,"order":3},{"key":"about","label":"关于","path":"/about","visible":true,"order":4},{"key":"write","label":"写文章","path":"/write","visible":true,"order":5}]',
  '导航菜单 JSON 数组（key/label/path/visible/order）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `blog_site_setting` WHERE `setting_key` = 'nav');

INSERT INTO `blog_site_setting` (`id`, `setting_key`, `setting_value`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`)
SELECT * FROM (SELECT 2100000000000000058, 'pages',
  '{"about":{"title":"关于云记","content":"云记博客是基于 cloudwaer-blog 的多作者技术写作与轻论坛平台，记录工程实践、踩坑与思考。"}}',
  '独立页面文案 JSON（如 about.title/about.content）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `blog_site_setting` WHERE `setting_key` = 'pages');

INSERT INTO `blog_site_setting` (`id`, `setting_key`, `setting_value`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`)
SELECT * FROM (SELECT 2100000000000000059, 'articleLayout',
  '{"tocPosition":"right"}',
  '文章详情布局 JSON（tocPosition: left/right/off）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `blog_site_setting` WHERE `setting_key` = 'articleLayout');

INSERT INTO `blog_site_setting` (`id`, `setting_key`, `setting_value`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`)
SELECT * FROM (SELECT 2100000000000000060, 'homeLayout',
  '{"showFeatured":true,"showTagCloud":true,"showHot":true}',
  '首页模块开关 JSON（showFeatured/showTagCloud/showHot）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `blog_site_setting` WHERE `setting_key` = 'homeLayout');
