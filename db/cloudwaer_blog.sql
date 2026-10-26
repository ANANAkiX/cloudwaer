/*
 Navicat Premium Dump SQL

 Source Server         : 本地my_sql
 Source Server Type    : MySQL
 Source Server Version : 80018 (8.0.18)
 Source Host           : localhost:3306
 Source Schema         : cloudwaer_blog

 Target Server Type    : MySQL
 Target Server Version : 80018 (8.0.18)
 File Encoding         : 65001

 Date: 25/10/2026 00:27:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for blog_article
-- ----------------------------
DROP TABLE IF EXISTS `blog_article`;
CREATE TABLE `blog_article`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文（Markdown）',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID（blog_category.id）',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID（sys_user.id）',
  `author_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者昵称（冗余）',
  `publish_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发布状态：0-草稿，1-已发布，2-已下线，3-待审核',
  `is_top` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_recommend` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `view_count` int(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int(11) NOT NULL DEFAULT 0 COMMENT '评论数',
  `heat_score` double NOT NULL DEFAULT 0 COMMENT '热度分（对数加权+时间衰减）',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核意见（拒绝原因）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publish_heat`(`publish_status` ASC, `heat_score` ASC) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_author`(`author_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_article
-- ----------------------------
INSERT INTO `blog_article` VALUES (2100000000000000021, '欢迎使用 CloudWaer 博客', '这是 cloudwaer-blog 的第一篇文章，演示 Markdown 渲染、标签与热度功能 。', '# 欢迎使用 CloudWaer 博客\n\n这是 **cloudwaer-blog** 的第一篇文章，用于演示 Markdown 渲染、标签、评论与热度功能。\n\n## 快速开始\n\n1. 在管理后台发布文章\n2. 在首页查看文章流与热度排行\n3. 在详情页体验评论互动\n\n```java\npublic class HelloBlog {\n    public static void main(String[] args) {\n        System.out.println(\"Hello CloudWaer Blog!\");\n    }\n}\n```\n\n祝写作愉快！', '/blog/uploads/0082f5534cf743a8a1a6419f8d00c8ab.png', 2100000000000000001, 1, '管理员', 1, 1, 1, 150, 6, 1, 10.62829443397303, '2026-10-24 00:00:00', NULL, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_article` VALUES (2113757534013804546, '冒烟测试文章', 'CRUD冒烟', '# Hello\n正文内容', NULL, 2100000000000000001, 2113757451016916994, 'blog_smoke_admin', 0, 0, 0, 0, 0, 0, 0, NULL, NULL, '2026-10-24 06:20:42', NULL, '2026-10-24 06:20:43', NULL, 0);
INSERT INTO `blog_article` VALUES (2113758498082979841, '冒烟测试文章-已发布', '更新摘要', '# Hello\n更新正文', NULL, 2100000000000000001, 2113757451016916994, 'blog_smoke_admin', 1, 1, 1, 0, 0, 0, 0, '2026-10-24 06:24:33', NULL, '2026-10-24 06:24:32', NULL, '2026-10-24 06:24:33', NULL, 0);

-- ----------------------------
-- Table structure for blog_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_article_tag`;
CREATE TABLE `blog_article_tag`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID（blog_article.id）',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID（blog_tag.id）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_article_tag`(`article_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_article_tag
-- ----------------------------
INSERT INTO `blog_article_tag` VALUES (2113757534013804547, 2113757534013804546, 2100000000000000011, '2026-10-24 06:20:42', NULL, '2026-10-24 06:20:42', NULL, 0);
INSERT INTO `blog_article_tag` VALUES (2113757534013804548, 2113757534013804546, 2100000000000000012, '2026-10-24 06:20:42', NULL, '2026-10-24 06:20:42', NULL, 0);
INSERT INTO `blog_article_tag` VALUES (2114018213245435906, 2100000000000000021, 2100000000000000011, '2026-10-24 23:36:33', NULL, '2026-10-24 23:36:33', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114018213245435907, 2100000000000000021, 2100000000000000012, '2026-10-24 23:36:33', NULL, '2026-10-24 23:36:33', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114018213245435908, 2100000000000000021, 2100000000000000013, '2026-10-24 23:36:33', NULL, '2026-10-24 23:36:33', NULL, 1);

-- ----------------------------
-- Table structure for blog_author_apply
-- ----------------------------
DROP TABLE IF EXISTS `blog_author_apply`;
CREATE TABLE `blog_author_apply`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '申请人（sys_user.id）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（冗余）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称（冗余）',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请理由',
  `apply_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态：0-待处理，1-已通过，2-已拒绝',
  `handle_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handle_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_apply_status`(`apply_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客主申请表（MVP 打标）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_author_apply
-- ----------------------------

-- ----------------------------
-- Table structure for blog_category
-- ----------------------------
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `slug` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '别名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_category
-- ----------------------------
INSERT INTO `blog_category` VALUES (2100000000000000001, '技术', 'tech', '技术实践与工程记录', 1, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_category` VALUES (2100000000000000002, '生活', 'life', '生活随笔', 2, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);

-- ----------------------------
-- Table structure for blog_comment
-- ----------------------------
DROP TABLE IF EXISTS `blog_comment`;
CREATE TABLE `blog_comment`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID（blog_article.id）',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父评论ID，0-顶级评论',
  `root_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '根评论ID（楼中楼归属），0-顶级评论',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '评论用户ID（sys_user.id），可空',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论者昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '附图URL列表JSON（/blog/uploads/**）',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `audit_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评论IP',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article`(`article_id` ASC) USING BTREE,
  INDEX `idx_audit`(`audit_status` ASC) USING BTREE,
  INDEX `idx_root`(`root_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_comment
-- ----------------------------
INSERT INTO `blog_comment` VALUES (2100000000000000041, 2100000000000000021, 0, 0, 1, '管理员', NULL, '欢迎使用 CloudWaer 博客，这是第一条评论。', NULL, 0, 1, '127.0.0.1', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `slug` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '别名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_tag
-- ----------------------------
INSERT INTO `blog_tag` VALUES (2100000000000000011, 'Java', 'java', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_tag` VALUES (2100000000000000012, 'Vue', 'vue', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_tag` VALUES (2100000000000000013, 'Spring', 'spring', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_tag` VALUES (2100000000000000014, 'Docker', 'docker', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);

-- ----------------------------
-- Table structure for blog_site_setting
-- ----------------------------
DROP TABLE IF EXISTS `blog_site_setting`;
CREATE TABLE `blog_site_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `setting_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '配置值（简单项存字面量，复杂项存 JSON 字符串）',
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

-- ----------------------------
-- Records of blog_site_setting
-- ----------------------------
INSERT INTO `blog_site_setting` VALUES (2100000000000000051, 'siteTitle', 'CloudWaer 博客', '站点标题', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000052, 'siteDescription', '', '站点副标题/描述', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000053, 'favicon', '', '站点图标 URL（favicon/icon）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000054, 'enableSearch', '1', '是否开启搜索：0-关闭，1-开启', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000055, 'layout', 'default', '网站布局：default / compact / magazine', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000056, 'footerText', '', '页脚文案', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000057, 'nav', '[{"key":"home","label":"首页","path":"/","visible":true,"order":1},{"key":"forum","label":"微论坛","path":"/forum","visible":true,"order":2},{"key":"archive","label":"归档","path":"/archive","visible":true,"order":3},{"key":"about","label":"关于","path":"/about","visible":true,"order":4},{"key":"write","label":"写文章","path":"/write","visible":true,"order":5}]', '导航菜单 JSON 数组（key/label/path/visible/order）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000058, 'pages', '{"about":{"title":"关于云记","content":"云记博客是基于 cloudwaer-blog 的多作者技术写作与轻论坛平台，记录工程实践、踩坑与思考。"}}', '独立页面文案 JSON（如 about.title/about.content）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000059, 'articleLayout', '{"tocPosition":"right"}', '文章详情布局 JSON（tocPosition: left/right/off）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000060, 'homeLayout', '{"showFeatured":true,"showTagCloud":true,"showHot":true}', '首页模块开关 JSON（showFeatured/showTagCloud/showHot）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);

SET FOREIGN_KEY_CHECKS = 1;
