/*
 Navicat Premium Dump SQL

 Source Server         : 本地my_sql
 Source Server Type    : MySQL
 Source Server Version : 80018 (8.0.18)
 Source Host           : localhost:3306
 Source Schema         : cloudwaer_codegen_config

 Target Server Type    : MySQL
 Target Server Version : 80018 (8.0.18)
 File Encoding         : 65001

 Date: 24/01/2026 01:43:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_database_connection
-- ----------------------------
DROP TABLE IF EXISTS `sys_database_connection`;
CREATE TABLE `sys_database_connection`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '连接名称',
  `db_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据库类型（mysql, postgresql, oracle等）',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主机地址',
  `port` int(11) NOT NULL COMMENT '端口号',
  `database` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据库名称',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '连接URL（可选）',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用（0-否，1-是）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_db_type`(`db_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_enabled`(`enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据库连接配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_database_connection
-- ----------------------------
INSERT INTO `sys_database_connection` VALUES (1991565755409891330, '本地Mysql', 'MySQL', '127.0.0.1', 3306, 'cloudwaer', 'root', 'root', NULL, 1, NULL, '2025-11-21 01:54:12', NULL, '2025-11-21 01:54:12', NULL, 1);
INSERT INTO `sys_database_connection` VALUES (1991830218407124994, '本地mysql_mc', 'MySQL', '127.0.0.1', 3306, 'minecraft_forum', 'root', 'root', NULL, 1, NULL, '2025-11-21 19:25:05', NULL, '2025-11-21 19:25:05', NULL, 1);

-- ----------------------------
-- Table structure for sys_form_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_form_config`;
CREATE TABLE `sys_form_config`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表名',
  `connection_id` bigint(20) NOT NULL COMMENT '数据库连接ID',
  `module_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模块名称',
  `package_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '包名',
  `entity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实体类名称',
  `entity_comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实体类注释',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `form_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '表单字段配置（JSON格式）',
  `query_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '查询字段列表（JSON格式）',
  `primary_key_field` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主键字段名',
  `enable_pagination` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用分页查询（0-否，1-是）',
  `enable_logic_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否启用逻辑删除（0-否，1-是）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_table_connection`(`table_name` ASC, `connection_id` ASC) USING BTREE,
  INDEX `idx_connection_id`(`connection_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '表单配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_form_config
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
