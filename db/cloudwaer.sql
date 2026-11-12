/*
 Navicat Premium Dump SQL

 Source Server         : 本地my_sql
 Source Server Type    : MySQL
 Source Server Version : 80018 (8.0.18)
 Source Host           : localhost:3306
 Source Schema         : cloudwaer

 Target Server Type    : MySQL
 Target Server Version : 80018 (8.0.18)
 File Encoding         : 65001

 Date: 12/11/2026 21:32:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1992000000000001000, 'status', '状态', 0, '状态字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000002000, 'delete_flag', '删除标识', 0, '删除标识字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000003000, 'boolean', '布尔', 0, '布尔字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000004000, 'flowable_type', '流程类型', 0, '流程类型字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 0);
INSERT INTO `sys_dict` VALUES (2013029000000000001, 'permission_type', '权限类型', 0, '权限类型字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict` VALUES (2013029000000000003, 'flowable_priority', '任务优先级', 0, '流程任务优先级字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 0);
INSERT INTO `sys_dict` VALUES (2026021400000020001, 'workflow_form_category', '流程表单分类', 1, '流程表单分类字典', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict` VALUES (2026021400000020002, 'workflow_form_status', '流程表单状态', 2, '流程表单状态字典', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict` VALUES (2120489282651189250, 'workflow_def_category', '流程模型分类', 7, '流程模型分类字典', '2026-11-11 20:10:16', NULL, '2026-11-11 20:10:16', NULL, 1);

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `dict_id` bigint(20) NOT NULL COMMENT '字典ID',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典编码',
  `value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典值',
  `label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '显示名称',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dict_id`(`dict_id` ASC) USING BTREE,
  INDEX `idx_dict_id_code`(`dict_id` ASC, `code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1992000000000001001, 1992000000000001000, '0', '0', '删除', 0, '状态-删除', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000001002, 1992000000000001000, '1', '1', '有效', 1, '状态-有效', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000001003, 1992000000000001000, '2', '2', '无效', 2, '状态-无效', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000002001, 1992000000000002000, '0', '0', '未删除', 0, '删除标识-未删除', '2025-11-21 20:40:17', 'system', '2025-11-21 22:28:07', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000002002, 1992000000000002000, '1', '1', '已删除', 1, '删除标识-已删除', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000003001, 1992000000000003000, 'true', 'true', '是', 0, '布尔-是', '2025-11-21 20:40:17', 'system', '2025-11-21 22:23:50', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000003002, 1992000000000003000, 'false', 'false', '否', 1, '布尔-否', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000004001, 1992000000000004000, 'leave', 'leave', '请假类型', 0, '流程类型-请假', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (1992000000000004002, 1992000000000004000, 'ea', 'ea', '审批类型', 1, '流程类型-审批', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (1992000000000004003, 1992000000000004000, 'expense', 'expense', '报销类型', 2, '流程类型-报销', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013028106117353473, 2013027807789092865, 'running', '0', '运行中', 0, '', '2026-01-19 07:17:55', NULL, '2026-01-19 07:17:55', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013028154754502658, 2013027807789092865, 'completed', '1', '已完成', 1, '', '2026-01-19 07:18:07', NULL, '2026-01-19 07:19:34', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013028202070446082, 2013027807789092865, 'suspended', '2', '已挂起', 2, '', '2026-01-19 07:18:18', NULL, '2026-01-19 07:19:43', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013028251974275073, 2013027807789092865, 'rejected', '3', '被拒绝', 3, '', '2026-01-19 07:18:30', NULL, '2026-01-19 07:19:38', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013028311856353281, 2013027807789092865, 'terminated', '4', '已终止', 4, '', '2026-01-19 07:18:44', NULL, '2026-01-19 07:19:48', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013028468014485506, 2013027807789092865, 'canceled', '5', '已取消', 5, '', '2026-01-19 07:19:22', NULL, '2026-01-19 07:19:28', NULL, 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000101, 2013029000000000001, '1', '1', '菜单', 0, '权限类型-菜单', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000102, 2013029000000000001, '2', '2', '页面', 1, '权限类型-页面', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000103, 2013029000000000001, '3', '3', '操作', 2, '权限类型-操作', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000301, 2013029000000000003, 'low', 'low', '低级', 0, '任务优先级-低级', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000302, 2013029000000000003, 'normal', 'normal', '正常', 1, '任务优先级-正常', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000303, 2013029000000000003, 'urgent', 'urgent', '紧急', 2, '任务优先级-紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000304, 2013029000000000003, 'very_urgent', 'very_urgent', '非常紧急', 3, '任务优先级-非常紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000401, 2013029000000000004, '0', '0', '草稿', 0, '模型版本-草稿', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000402, 2013029000000000004, '1', '1', '已发布', 1, '模型版本-已发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000403, 2013029000000000004, '2', '2', '已归档', 2, '模型版本-已归档', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000404, 2013029000000000004, '3', '3', '已更新未发布', 3, '模型版本-已更新未发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000405, 2013029000000000004, '4', '4', '已回滚未发布', 4, '模型版本-已回滚未发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2013029000000000406, 2013029000000000004, '5', '5', '已关闭', 5, '模型版本-已关闭', '2026-10-23 23:49:00', 'system', '2026-10-23 23:49:00', 'system', 0);
INSERT INTO `sys_dict_item` VALUES (2026021400000021001, 2026021400000020001, 'leave', 'leave', '请假', 1, '请假类表单', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000021002, 2026021400000020001, 'expense', 'expense', '报销', 2, '报销类表单', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000021003, 2026021400000020001, 'purchase', 'purchase', '采购', 3, '采购类表单', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000021004, 2026021400000020001, 'attendance', 'attendance', '考勤', 4, '考勤类表单', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000021005, 2026021400000020001, 'other', 'other', '其他', 99, '其他类表单', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000022001, 2026021400000020002, 'draft', '0', '草稿', 1, '草稿状态', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000022002, 2026021400000020002, 'enabled', '1', '启用', 2, '启用状态', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2026021400000022003, 2026021400000020002, 'disabled', '2', '停用', 3, '停用状态', '2026-11-03 07:25:45', 'admin', '2026-11-03 07:25:45', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2120489282919624706, 2120489282651189250, 'leave', 'leave', '请假', 1, '', '2026-11-11 20:10:17', NULL, '2026-11-11 20:10:17', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2120489282974150658, 2120489282651189250, 'expense', 'expense', '报销', 2, '', '2026-11-11 20:10:17', NULL, '2026-11-11 20:10:17', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2120489283037065217, 2120489282651189250, 'purchase', 'purchase', '采购', 3, '', '2026-11-11 20:10:17', NULL, '2026-11-11 20:10:17', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2120489283099979777, 2120489282651189250, 'attendance', 'attendance', '考勤', 4, '', '2026-11-11 20:10:17', NULL, '2026-11-11 20:10:17', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2120489283297112065, 2120489282651189250, 'other', 'other', '其他', 9, '', '2026-11-11 20:10:17', NULL, '2026-11-11 20:10:17', NULL, 1);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `access_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问路径（相对或完整 URL）',
  `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储供应商 LOCAL/ALIYUN_OSS/TENCENT_COS',
  `oss_config_id` bigint(20) NULL DEFAULT NULL COMMENT '使用的 OSS 配置 ID',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'OSS 对象键；LOCAL 可为相对路径',
  `bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '桶名',
  `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_size` bigint(20) NOT NULL DEFAULT 0 COMMENT '字节数',
  `md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型：avatar/article/product… 预留',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务 ID 预留',
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_provider`(`provider` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台通用文件元数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (2114380103574286337, '创建用户结果_1792942374442.csv', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/25/fbaa59cf24b74b8ea785261f7d56cafe.csv', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/25/fbaa59cf24b74b8ea785261f7d56cafe.csv', 'cloudwaer-ui', 'text/csv', 182, NULL, NULL, NULL, '2026-10-25 23:34:35', NULL, '2026-10-25 23:37:02', NULL, 0);
INSERT INTO `sys_file` VALUES (2114381219636637698, 'files.zip', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/25/49894ee33e24418b9628fd06e85fa742.zip', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/25/49894ee33e24418b9628fd06e85fa742.zip', 'cloudwaer-ui', 'application/x-zip-compressed', 341, NULL, NULL, NULL, '2026-10-25 23:39:01', NULL, '2026-10-26 00:47:11', NULL, 0);
INSERT INTO `sys_file` VALUES (2114389995651493890, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/c1406bb6f04b4bc8bca24b72ec703de2.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/c1406bb6f04b4bc8bca24b72ec703de2.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 00:13:53', NULL, '2026-10-26 00:26:35', NULL, 0);
INSERT INTO `sys_file` VALUES (2114399564243050498, '微信图片_2026-10-25_225701_826.jpg', '/api/admin/file/display?id=2114399564243050498', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/311a6c8bb2634d7f8471a5eac3d68e0f.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-10-26 00:51:54', NULL, '2026-10-26 00:53:14', NULL, 0);
INSERT INTO `sys_file` VALUES (2114399587198476290, '微信图片_2026-10-25_225701_826.jpg', '/api/admin/file/display?id=2114399587198476290', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/e5348183df3845d3af951e4c548a12e8.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 00:52:00', NULL, '2026-10-26 00:56:10', NULL, 0);
INSERT INTO `sys_file` VALUES (2114401115154059266, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/cf0d3d8bd03b4792986b8cd5204dfe41.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/cf0d3d8bd03b4792986b8cd5204dfe41.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-10-26 00:58:04', NULL, '2026-10-26 01:00:54', NULL, 0);
INSERT INTO `sys_file` VALUES (2114401274214649858, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/69e3e4f987664e68851b1ed6fe2357b9.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/69e3e4f987664e68851b1ed6fe2357b9.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 00:58:42', NULL, '2026-10-26 01:00:51', NULL, 0);
INSERT INTO `sys_file` VALUES (2114401348512550913, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/f41f39cde94941e7bbacb8064f8572c6.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/f41f39cde94941e7bbacb8064f8572c6.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 00:59:00', NULL, '2026-10-26 01:00:49', NULL, 0);
INSERT INTO `sys_file` VALUES (2114402720310005761, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/7698a3b8d3a941d5a3d7c44a32e8bd98.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/7698a3b8d3a941d5a3d7c44a32e8bd98.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 01:04:27', NULL, '2026-10-26 01:04:27', NULL, 1);
INSERT INTO `sys_file` VALUES (2114402855899271170, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-10-26 01:04:59', NULL, '2026-10-26 01:04:59', NULL, 1);
INSERT INTO `sys_file` VALUES (2114500331440369665, '22f62aaf78f74aa3b3573140e856f1f4.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', 'cloudwaer-ui', 'image/jpeg', 4794490, NULL, 'avatar', '1992208936665325569', '2026-10-26 07:32:19', NULL, '2026-10-26 07:32:19', NULL, 1);
INSERT INTO `sys_file` VALUES (2117608347714908161, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/360e5b097c7545729c983f8547acf2b0.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/360e5b097c7545729c983f8547acf2b0.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 21:22:28', NULL, '2026-11-03 21:22:28', NULL, 1);
INSERT INTO `sys_file` VALUES (2117608426001592321, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/3334b968ecf04390aa65fdc19eddfe1b.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/3334b968ecf04390aa65fdc19eddfe1b.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 21:22:47', NULL, '2026-11-03 21:22:47', NULL, 1);
INSERT INTO `sys_file` VALUES (2117608476849139714, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/65cce7e74dd24f0ab5322c1f00e6cd45.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/65cce7e74dd24f0ab5322c1f00e6cd45.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 21:22:59', NULL, '2026-11-03 21:22:59', NULL, 1);
INSERT INTO `sys_file` VALUES (2117619847552720898, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/ecb636d97e124269bd5c0ac969c1e937.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/ecb636d97e124269bd5c0ac969c1e937.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:08:10', NULL, '2026-11-03 22:08:10', NULL, 1);
INSERT INTO `sys_file` VALUES (2117621299432022017, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/d47d287a36fa4083a04d577dba2c3e9d.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/d47d287a36fa4083a04d577dba2c3e9d.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:13:56', NULL, '2026-11-03 22:13:56', NULL, 1);
INSERT INTO `sys_file` VALUES (2117622013596160001, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/8837c7055969430e91107d7209903a9e.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/8837c7055969430e91107d7209903a9e.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:16:46', NULL, '2026-11-03 22:16:46', NULL, 1);
INSERT INTO `sys_file` VALUES (2117624242273251330, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/84c8f519866e47fc8d0f3f8c302417ed.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/84c8f519866e47fc8d0f3f8c302417ed.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:25:38', NULL, '2026-11-03 22:25:38', NULL, 1);
INSERT INTO `sys_file` VALUES (2117627468670226434, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/c7a0d25f942a4d16a8fea8d7a88e3257.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/c7a0d25f942a4d16a8fea8d7a88e3257.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:38:27', NULL, '2026-11-03 22:38:27', NULL, 1);
INSERT INTO `sys_file` VALUES (2117628424745046018, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/03/4558aafabc604048a19b43845c73d70b.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/03/4558aafabc604048a19b43845c73d70b.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-03 22:42:15', NULL, '2026-11-03 22:42:15', NULL, 1);
INSERT INTO `sys_file` VALUES (2117738307528093697, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/430ab94969b64993895b1694e1554a92.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/430ab94969b64993895b1694e1554a92.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 05:58:53', NULL, '2026-11-04 05:58:53', NULL, 1);
INSERT INTO `sys_file` VALUES (2117738602437996546, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/e0e7a4cbe90b4408a5967ea263c6f9f3.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/e0e7a4cbe90b4408a5967ea263c6f9f3.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 06:00:03', NULL, '2026-11-04 06:00:03', NULL, 1);
INSERT INTO `sys_file` VALUES (2117744406545260545, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/4bbb456a2b8a4adeb71193321c65c257.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/4bbb456a2b8a4adeb71193321c65c257.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 06:23:07', NULL, '2026-11-04 06:23:07', NULL, 1);
INSERT INTO `sys_file` VALUES (2117751594516918273, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/44d8defdb84f439aa4491b0328ab571c.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/44d8defdb84f439aa4491b0328ab571c.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 06:51:41', NULL, '2026-11-04 06:51:41', NULL, 1);
INSERT INTO `sys_file` VALUES (2117753460487266306, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/7e2b403c881b462fa59ada529b3d5bea.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/7e2b403c881b462fa59ada529b3d5bea.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 06:59:06', NULL, '2026-11-04 06:59:06', NULL, 1);
INSERT INTO `sys_file` VALUES (2117757568891965441, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/342f48463fa74cc4928c1db1708d2d92.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/342f48463fa74cc4928c1db1708d2d92.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 07:15:25', NULL, '2026-11-04 07:15:25', NULL, 1);
INSERT INTO `sys_file` VALUES (2117757700182069249, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/bc525e0e321c4097a318112a2ae3f30d.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/bc525e0e321c4097a318112a2ae3f30d.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 07:15:56', NULL, '2026-11-04 07:15:56', NULL, 1);
INSERT INTO `sys_file` VALUES (2117949723015081985, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/3a471924728e4ac68052070ff6fb4965.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/3a471924728e4ac68052070ff6fb4965.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 19:58:58', NULL, '2026-11-04 19:58:58', NULL, 1);
INSERT INTO `sys_file` VALUES (2117953776746983426, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/222e2eb5c7514fc7aa22e9289396f14e.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/222e2eb5c7514fc7aa22e9289396f14e.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:15:05', NULL, '2026-11-04 20:15:05', NULL, 1);
INSERT INTO `sys_file` VALUES (2117954974690537473, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/342aa21c9df445d892a5cf8b5b5cbc33.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/342aa21c9df445d892a5cf8b5b5cbc33.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:19:50', NULL, '2026-11-04 20:19:50', NULL, 1);
INSERT INTO `sys_file` VALUES (2117955050825543681, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/172a38f05ef44b80ab8b507aa46453bd.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/172a38f05ef44b80ab8b507aa46453bd.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:20:08', NULL, '2026-11-04 20:20:08', NULL, 1);
INSERT INTO `sys_file` VALUES (2117955867515252737, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/41b1e39036f54afda3ba418d375d30e4.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/41b1e39036f54afda3ba418d375d30e4.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:23:23', NULL, '2026-11-04 20:23:23', NULL, 1);
INSERT INTO `sys_file` VALUES (2117960882103427074, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/c869782982884f66b9b382bca6c340d8.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/c869782982884f66b9b382bca6c340d8.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:43:19', NULL, '2026-11-04 20:43:19', NULL, 1);
INSERT INTO `sys_file` VALUES (2117963431648899073, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/7848d525ebd743df913585263acf730d.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/7848d525ebd743df913585263acf730d.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:53:27', NULL, '2026-11-04 20:53:27', NULL, 1);
INSERT INTO `sys_file` VALUES (2117964640468283393, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/9eff326c081241be963d398ca24c935f.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/9eff326c081241be963d398ca24c935f.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 20:58:15', NULL, '2026-11-04 20:58:15', NULL, 1);
INSERT INTO `sys_file` VALUES (2117965511667167233, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/445d8bb057614292bd11040099e262ad.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/445d8bb057614292bd11040099e262ad.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 21:01:43', NULL, '2026-11-04 21:01:43', NULL, 1);
INSERT INTO `sys_file` VALUES (2117965580650885121, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/4b1925950e1e440fbd72830cad2b64b3.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/4b1925950e1e440fbd72830cad2b64b3.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 21:01:59', NULL, '2026-11-04 21:01:59', NULL, 1);
INSERT INTO `sys_file` VALUES (2117965623235653633, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/2e8da0e7562e423ebef88be1f2bb14de.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/2e8da0e7562e423ebef88be1f2bb14de.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 21:02:09', NULL, '2026-11-04 21:02:09', NULL, 1);
INSERT INTO `sys_file` VALUES (2117971072882257921, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/7fdb57050bee45fdbbfa1e2eebd7c8e2.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/7fdb57050bee45fdbbfa1e2eebd7c8e2.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 21:23:48', NULL, '2026-11-04 21:23:48', NULL, 1);
INSERT INTO `sys_file` VALUES (2117972531271106562, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/c277dac0c6f14168991607ecaab7cade.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/c277dac0c6f14168991607ecaab7cade.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 21:29:36', NULL, '2026-11-04 21:29:36', NULL, 1);
INSERT INTO `sys_file` VALUES (2117980767164481537, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/1cd9772c96aa45cead2e518ca4f47cd0.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/1cd9772c96aa45cead2e518ca4f47cd0.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 22:02:20', NULL, '2026-11-04 22:02:20', NULL, 1);
INSERT INTO `sys_file` VALUES (2117982332386459650, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/936dad2254214c95bc72d38b442f4247.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/936dad2254214c95bc72d38b442f4247.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 22:08:33', NULL, '2026-11-04 22:08:33', NULL, 1);
INSERT INTO `sys_file` VALUES (2117984397384921089, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/b83b4116dad442b49f0ad1037c93d936.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/b83b4116dad442b49f0ad1037c93d936.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 22:16:45', NULL, '2026-11-04 22:16:45', NULL, 1);
INSERT INTO `sys_file` VALUES (2117986238004899841, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/04/ae6042039d094218b26759e84c314ad3.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/04/ae6042039d094218b26759e84c314ad3.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, NULL, NULL, '2026-11-04 22:24:04', NULL, '2026-11-04 22:24:04', NULL, 1);
INSERT INTO `sys_file` VALUES (2119821793831354369, 'avatar-test.png', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/09/06aebd39675e42f393d5d127e38fa233.png', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/09/06aebd39675e42f393d5d127e38fa233.png', 'cloudwaer-ui', 'image/png', 70, NULL, 'avatar', '1', '2026-11-09 23:57:55', NULL, '2026-11-09 23:57:55', NULL, 1);
INSERT INTO `sys_file` VALUES (2119876617058848769, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/10/7f028a355e7c4ac5aab0055450fc2769.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/10/7f028a355e7c4ac5aab0055450fc2769.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-11-10 03:35:46', NULL, '2026-11-10 03:35:46', NULL, 1);
INSERT INTO `sys_file` VALUES (2119876920214753282, '1.png', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/10/149827bef47849e895fe4a88eb7cfacf.png', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/10/149827bef47849e895fe4a88eb7cfacf.png', 'cloudwaer-ui', 'image/png', 64836, NULL, 'avatar', '1', '2026-11-10 03:36:58', NULL, '2026-11-10 03:36:58', NULL, 1);
INSERT INTO `sys_file` VALUES (2119876959788011521, '微信图片_2026-10-25_225701_826.jpg', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/10/e924ef1d29f6427da38222ac2a1596c1.jpg', 'ALIYUN_OSS', 2114379361417359361, 'uploads/2026/11/10/e924ef1d29f6427da38222ac2a1596c1.jpg', 'cloudwaer-ui', 'image/jpeg', 98440, NULL, 'avatar', '1', '2026-11-10 03:37:07', NULL, '2026-11-10 03:37:07', NULL, 1);

-- ----------------------------
-- Table structure for sys_gateway_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_gateway_route`;
CREATE TABLE `sys_gateway_route`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `route_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由ID（唯一标识）',
  `uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路由URI（如：lb://service-name）',
  `predicates` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路由断言（JSON格式，如：[{\"name\":\"Path\",\"args\":{\"pattern\":\"/api/**\"}}]）',
  `filters` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路由过滤器（JSON格式）',
  `order` int(11) NULL DEFAULT 0 COMMENT '路由顺序（数字越小优先级越高）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_route_id`(`route_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_order`(`order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网关路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gateway_route
-- ----------------------------
INSERT INTO `sys_gateway_route` VALUES (1, 'cloudwaer-authentication', 'lb://cloudwaer-authentication', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/auth/**\"}}]', NULL, 0, '鉴权服务的网关转发配置', '2025-11-20 18:52:12', NULL, '2025-11-21 00:38:11', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (2, 'cloudwaer-admin-serve', 'lb://cloudwaer-admin-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/admin/**\"}}]', NULL, 0, '后台管理服务服务的网关转发配置', '2025-11-20 18:52:12', NULL, '2025-11-21 00:38:00', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (10, 'cloudwaer-blog-serve', 'lb://cloudwaer-blog-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/blog/**\"}}]', NULL, 0, '博客服务路由', '2026-10-24 00:00:00', NULL, '2026-10-24 00:00:00', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (11, 'ws-cloudwaer-admin-serve', 'lb://cloudwaer-admin-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/ws/**\"}}]', NULL, 0, '平台 WS 握手（admin-serve /ws 端点，仅 WebSocket 转发，勿 StripPrefix）', '2026-11-12 19:55:27', NULL, '2026-11-12 19:55:27', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (12, 'cloudwaer-admin-serve-message', 'lb://cloudwaer-admin-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/message/**\"}}]', NULL, 0, '站内信用户端接口（/message/**）转发 admin-serve，勿 StripPrefix', '2026-11-12 19:55:27', NULL, '2026-11-12 19:55:27', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (1991546479613448194, 'cloudwaer-codegen-serve', 'lb://cloudwaer-codegen-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/codegen/**\"}}]', NULL, 0, '代码生成服务的网关转发配置', '2025-11-21 00:37:37', NULL, '2025-11-21 00:37:37', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (1992205324727853058, 'ws-cloudwaer-im-service', 'lb:ws://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/ws/**\"}}]', NULL, 0, '仅让 WebSocket 握手走这条，避免影响 REST 调用\n', '2025-11-22 20:15:37', NULL, '2026-01-06 05:56:00', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (1992239401883295746, 'rest-cloudwaer-im-service', 'lb://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/**\"}}]', NULL, 1, '处理 /im/** 的 HTTP 接口', '2025-11-22 22:31:02', NULL, '2026-01-06 05:56:13', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (2011458231829864450, 'cloudwaer-flowable-serve', 'lb://cloudwaer-flowable-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/flowable/**\"}}]', NULL, 0, 'cloudwaer-flowable-serve 的基础网关转发（与 admin/codegen 一致，保留 /flowable 前缀，勿加 StripPrefix）', '2026-01-14 23:19:48', NULL, '2026-11-12 19:55:27', NULL, 2);
INSERT INTO `sys_gateway_route` VALUES (2115773090837475330, 'cloudwaer-wiki-serve', 'lb://cloudwaer-wiki-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/wiki/**\"}}]', NULL, 0, 'cloudwaer-wiki-serve的基础网关转发', '2026-10-29 19:49:49', NULL, '2026-10-29 19:49:49', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (2117316762494214145, 'cloudwaer-workflow-serve', 'lb://cloudwaer-workflow-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/workflow/**\"}}]', NULL, 0, 'cloudwaer-admin-serve 的基础网关转发', '2026-11-03 02:03:49', NULL, '2026-11-03 02:03:49', NULL, 1);

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `msg_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SYSTEM' COMMENT '类型：NOTICE-公告 SYSTEM-系统提示 PRIVATE-私信（预留）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容（纯文本/简单 HTML）',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型（blog_article / blog_comment ...）',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务 ID',
  `sender_id` bigint(20) NULL DEFAULT NULL COMMENT '发送人（系统消息为空）',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发送人展示名',
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除 1-有效 2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_time`(`msg_type` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内信消息主体' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (2115845113710288897, 'SYSTEM', '[冒烟] 评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '123', NULL, NULL, '2026-10-30 00:36:00', NULL, '2026-10-30 00:36:00', NULL, 1);
INSERT INTO `sys_message` VALUES (2115845777265963009, 'SYSTEM', '[冒烟] 评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '123', NULL, NULL, '2026-10-30 00:38:38', NULL, '2026-10-30 00:38:38', NULL, 1);
INSERT INTO `sys_message` VALUES (2115852468124581890, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, NULL, '2026-10-30 01:05:14', NULL, '2026-10-30 01:05:14', NULL, 1);
INSERT INTO `sys_message` VALUES (2115853155919134722, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, NULL, '2026-10-30 01:07:58', NULL, '2026-10-30 01:07:58', NULL, 1);
INSERT INTO `sys_message` VALUES (2115853263393980417, 'SYSTEM', '评论审核未通过', '你的评论未通过审核。', 'blog_article', '2100000000000000021', NULL, NULL, '2026-10-30 01:08:23', NULL, '2026-10-30 01:08:23', NULL, 1);
INSERT INTO `sys_message` VALUES (2115853616420159490, 'SYSTEM', '文章审核通过', '你的文章《README》已通过审核并发布。', 'blog_article', '2115853380234706945', NULL, NULL, '2026-10-30 01:09:47', NULL, '2026-10-30 01:09:47', NULL, 1);
INSERT INTO `sys_message` VALUES (2115855815351123970, 'SYSTEM', '[冒烟] 文章审核通过', '你的文章《测试》已通过审核并发布。', 'blog_article', '9', NULL, '系统', '2026-10-30 01:18:32', NULL, '2026-10-30 01:18:32', NULL, 1);
INSERT INTO `sys_message` VALUES (2116151208857210882, 'SYSTEM', '评论收到点赞', 'test 赞了你的评论：11111', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 20:52:19', NULL, '2026-10-30 20:52:19', NULL, 1);
INSERT INTO `sys_message` VALUES (2116151247822295043, 'SYSTEM', '文章收到点赞', 'test 赞了你的文章《欢迎使用 CloudWaer 博客》', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 20:52:28', NULL, '2026-10-30 20:52:28', NULL, 1);
INSERT INTO `sys_message` VALUES (2116151323672088577, 'SYSTEM', '文章收到点赞', 'test 赞了你的文章《欢迎使用 CloudWaer 博客》', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 20:52:46', NULL, '2026-10-30 20:52:46', NULL, 1);
INSERT INTO `sys_message` VALUES (2116151529792770050, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 20:53:36', NULL, '2026-10-30 20:53:36', NULL, 1);
INSERT INTO `sys_message` VALUES (2116153688768884737, 'SYSTEM', '评论收到点赞', 'test 赞了你的评论：我看看😀🥰🎉💯🐱', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:02:10', NULL, '2026-10-30 21:02:10', NULL, 1);
INSERT INTO `sys_message` VALUES (2116153804204519425, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:02:38', NULL, '2026-10-30 21:02:38', NULL, 1);
INSERT INTO `sys_message` VALUES (2116153804263239681, 'SYSTEM', '文章收到新评论', '这是测试账号 评论了你的文章《欢迎使用 CloudWaer 博客》：我不同意', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:02:38', NULL, '2026-10-30 21:02:38', NULL, 1);
INSERT INTO `sys_message` VALUES (2116154627756109825, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:05:54', NULL, '2026-10-30 21:05:54', NULL, 1);
INSERT INTO `sys_message` VALUES (2116155428268388353, 'SYSTEM', '评论审核通过', '你的评论已通过审核并展示。', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:09:05', NULL, '2026-10-30 21:09:05', NULL, 1);
INSERT INTO `sys_message` VALUES (2116155428268388355, 'SYSTEM', '你的评论有新回复', '管理员 回复了你：我在卡卡', 'blog_article', '2100000000000000021', NULL, '系统', '2026-10-30 21:09:05', NULL, '2026-10-30 21:09:05', NULL, 1);
INSERT INTO `sys_message` VALUES (2116180558763712513, 'SYSTEM', '博客主申请通过', '你的博客主申请已通过，快去创作吧。', 'blog_profile', NULL, NULL, '系统', '2026-10-30 22:48:57', NULL, '2026-10-30 22:48:57', NULL, 1);
INSERT INTO `sys_message` VALUES (2116185907847704577, 'SYSTEM', '博客主申请通过', '你的博客主申请已通过，快去创作吧。', 'blog_profile', NULL, NULL, '系统', '2026-10-30 23:10:12', NULL, '2026-10-30 23:10:12', NULL, 1);
INSERT INTO `sys_message` VALUES (2119881119644180482, 'SYSTEM', '动作通知测试', '来自节点动作', 'workflow_instance', '439d6ce7509648738cda8877fee1b7f1', NULL, '系统', '2026-11-10 03:53:39', NULL, '2026-11-10 03:53:39', NULL, 1);
INSERT INTO `sys_message` VALUES (2119881119845507073, 'SYSTEM', '新任务提醒', '您有新的待办任务: act-task', 'workflow_instance', '439d6ce7509648738cda8877fee1b7f1', NULL, '系统', '2026-11-10 03:53:39', NULL, '2026-11-10 03:53:39', NULL, 1);
INSERT INTO `sys_message` VALUES (2119883165600505857, 'SYSTEM', '工作流测试·待办提醒', '您有一条新的审批流程待处理（通知动作触发）', 'workflow_instance', '5274c808db4b4dd3801feaace0e91a8c', NULL, '系统', '2026-11-10 04:01:47', NULL, '2026-11-10 04:01:47', NULL, 1);
INSERT INTO `sys_message` VALUES (2119883165722140673, 'SYSTEM', '新任务提醒', '您有新的待办任务: 审批节点', 'workflow_instance', '5274c808db4b4dd3801feaace0e91a8c', NULL, '系统', '2026-11-10 04:01:47', NULL, '2026-11-10 04:01:47', NULL, 1);
INSERT INTO `sys_message` VALUES (2119883174442098690, 'SYSTEM', '工作流测试·已处理', '审批任务已由 smoke_u1 处理完成（通知动作触发）', 'workflow_instance', '5274c808db4b4dd3801feaace0e91a8c', NULL, '系统', '2026-11-10 04:01:49', NULL, '2026-11-10 04:01:49', NULL, 1);
INSERT INTO `sys_message` VALUES (2120469138424135682, 'SYSTEM', '工作流测试·待办提醒', '您有一条新的审批流程待处理（通知动作触发）', 'workflow_instance', 'e93ea33428da47309ebcd500e128b8dd', NULL, '系统', '2026-11-11 18:50:14', NULL, '2026-11-11 18:50:14', NULL, 1);
INSERT INTO `sys_message` VALUES (2120469138612879362, 'SYSTEM', '新任务提醒', '您有新的待办任务: 审批节点', 'workflow_instance', 'e93ea33428da47309ebcd500e128b8dd', NULL, '系统', '2026-11-11 18:50:14', NULL, '2026-11-11 18:50:14', NULL, 1);
INSERT INTO `sys_message` VALUES (2120469147303477249, 'SYSTEM', '工作流测试·已处理', '审批任务已由 smoke_u1 处理完成（通知动作触发）', 'workflow_instance', 'e93ea33428da47309ebcd500e128b8dd', NULL, '系统', '2026-11-11 18:50:16', NULL, '2026-11-11 18:50:16', NULL, 1);
INSERT INTO `sys_message` VALUES (2120469147378974722, 'SYSTEM', '有人抄送给您', 'smoke_u1 在流程中抄送给您，请查阅。', 'workflow_instance', 'e93ea33428da47309ebcd500e128b8dd', NULL, '系统', '2026-11-11 18:50:16', NULL, '2026-11-11 18:50:16', NULL, 1);
INSERT INTO `sys_message` VALUES (2120858588060794882, 'SYSTEM', '微服务消息测试 ①', '这是一条纯文本系统提示，用于验证微服务链路（gateway → admin-serve → 落库 + WS 推送）。', NULL, NULL, NULL, '系统', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);
INSERT INTO `sys_message` VALUES (2120858588186624002, 'SYSTEM', '微服务消息测试 ②（带跳转）', '这条消息带 bizType=biz_profile，点击「查看」应跳到个人主页。', 'blog_profile', '1', NULL, '系统', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);
INSERT INTO `sys_message` VALUES (2120858588253732866, 'NOTICE', '微服务消息测试 ③（公告）', '公告类型消息（msgType=NOTICE），收件箱应正常展示。', NULL, NULL, NULL, '系统', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);

-- ----------------------------
-- Table structure for sys_message_recipient
-- ----------------------------
DROP TABLE IF EXISTS `sys_message_recipient`;
CREATE TABLE `sys_message_recipient`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `message_id` bigint(20) NOT NULL COMMENT '消息 ID',
  `user_id` bigint(20) NOT NULL COMMENT '收件人用户 ID',
  `read_flag` int(11) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除 1-有效 2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `read_flag` ASC, `id` ASC) USING BTREE,
  INDEX `idx_message`(`message_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内信收件箱' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message_recipient
-- ----------------------------
INSERT INTO `sys_message_recipient` VALUES (2115845113722871810, 2115845113710288897, 1, 0, NULL, '2026-10-30 00:36:00', NULL, '2026-10-30 00:36:00', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2115845777295323137, 2115845777265963009, 1, 0, NULL, '2026-10-30 00:38:38', NULL, '2026-10-30 00:38:39', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2115852468124581891, 2115852468124581890, 1, 1, '2026-10-30 01:05:33', '2026-10-30 01:05:14', NULL, '2026-11-10 03:55:46', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2115853155919134723, 2115853155919134722, 1, 1, '2026-10-30 01:10:44', '2026-10-30 01:07:58', NULL, '2026-11-10 03:55:46', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2115853263393980418, 2115853263393980417, 1, 1, '2026-10-30 01:10:44', '2026-10-30 01:08:23', NULL, '2026-11-10 03:55:46', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2115853616420159491, 2115853616420159490, 1992208936665325569, 1, '2026-10-30 20:51:54', '2026-10-30 01:09:47', NULL, '2026-10-30 01:09:47', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2115855815351123971, 2115855815351123970, 1, 1, '2026-10-30 01:18:32', '2026-10-30 01:18:32', NULL, '2026-10-30 01:18:32', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116151208857210883, 2116151208857210882, 1, 1, '2026-10-30 20:52:22', '2026-10-30 20:52:19', NULL, '2026-11-10 03:55:45', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116151247822295044, 2116151247822295043, 1, 1, '2026-10-30 20:52:32', '2026-10-30 20:52:28', NULL, '2026-11-10 03:55:45', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116151323672088578, 2116151323672088577, 1, 1, '2026-10-30 20:53:31', '2026-10-30 20:52:46', NULL, '2026-11-10 03:55:45', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116151529792770051, 2116151529792770050, 1992208936665325569, 1, '2026-10-30 21:05:21', '2026-10-30 20:53:36', NULL, '2026-10-30 20:53:36', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2116153688768884738, 2116153688768884737, 1, 1, '2026-10-30 21:02:35', '2026-10-30 21:02:10', NULL, '2026-11-10 03:55:44', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116153804204519426, 2116153804204519425, 1992208936665325569, 1, '2026-10-30 21:05:21', '2026-10-30 21:02:38', NULL, '2026-10-30 21:02:38', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2116153804263239682, 2116153804263239681, 1, 1, '2026-10-30 21:02:41', '2026-10-30 21:02:38', NULL, '2026-11-10 03:55:44', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116154627756109826, 2116154627756109825, 1, 1, '2026-10-30 21:06:08', '2026-10-30 21:05:54', NULL, '2026-11-10 03:55:44', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116155428268388354, 2116155428268388353, 1, 1, '2026-10-30 21:09:18', '2026-10-30 21:09:05', NULL, '2026-11-10 03:55:44', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2116155428268388356, 2116155428268388355, 1992208936665325569, 1, '2026-10-30 21:09:08', '2026-10-30 21:09:05', NULL, '2026-10-30 21:09:05', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2116180558763712514, 2116180558763712513, 2116180125353697281, 1, '2026-10-30 23:09:31', '2026-10-30 22:48:57', NULL, '2026-10-30 22:48:57', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2116185907847704578, 2116185907847704577, 2116180125353697281, 1, '2026-10-30 23:55:44', '2026-10-30 23:10:12', NULL, '2026-10-30 23:10:12', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2119881119644180483, 2119881119644180482, 2119789131196428290, 1, '2026-11-10 03:57:43', '2026-11-10 03:53:39', NULL, '2026-11-10 03:57:43', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2119881119845507074, 2119881119845507073, 1, 1, '2026-11-10 03:55:33', '2026-11-10 03:53:39', NULL, '2026-11-10 03:55:42', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2119883165600505858, 2119883165600505857, 1, 1, '2026-11-10 04:02:13', '2026-11-10 04:01:47', NULL, '2026-11-11 18:53:48', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2119883165722140674, 2119883165722140673, 1, 1, '2026-11-10 04:02:23', '2026-11-10 04:01:47', NULL, '2026-11-11 18:53:47', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2119883174442098691, 2119883174442098690, 1, 1, '2026-11-10 04:02:24', '2026-11-10 04:01:49', NULL, '2026-11-11 18:53:47', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2120469138487050241, 2120469138424135682, 1, 1, '2026-11-11 18:50:45', '2026-11-11 18:50:14', NULL, '2026-11-11 20:04:12', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2120469138612879363, 2120469138612879362, 1, 1, '2026-11-11 18:50:44', '2026-11-11 18:50:14', NULL, '2026-11-11 19:25:48', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2120469147303477250, 2120469147303477249, 1, 1, '2026-11-11 18:50:42', '2026-11-11 18:50:16', NULL, '2026-11-11 19:25:48', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2120469147437694977, 2120469147378974722, 1, 1, '2026-11-11 18:50:36', '2026-11-11 18:50:16', NULL, '2026-11-11 19:25:48', NULL, 0);
INSERT INTO `sys_message_recipient` VALUES (2120858588123709442, 2120858588060794882, 1, 1, '2026-11-12 20:41:13', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2120858588186624003, 2120858588186624002, 1, 1, '2026-11-12 20:41:16', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);
INSERT INTO `sys_message_recipient` VALUES (2120858588253732867, 2120858588253732866, 1, 1, '2026-11-12 20:38:10', '2026-11-12 20:37:46', NULL, '2026-11-12 20:37:46', NULL, 1);

-- ----------------------------
-- Table structure for sys_oauth_provider
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_provider`;
CREATE TABLE `sys_oauth_provider`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `provider_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '供应商类型：GITHUB|GITEE|WECHAT|GOOGLE|CUSTOM',
  `provider_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '显示名称',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Client ID',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Client Secret（建议密文存储）',
  `auth_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权页地址',
  `token_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '获取 Token 地址',
  `user_info_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '获取用户信息地址',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权范围',
  `redirect_uri` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回调地址，可空',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录按钮图标 URL，可空',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `enabled` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否启用：0-否 1-是',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除 1-有效 2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_enabled`(`enabled` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '第三方登录供应商（可配置多组）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oauth_provider
-- ----------------------------
INSERT INTO `sys_oauth_provider` VALUES (1, 'GITHUB', 'GitHub', 'key', 'key', 'https://github.com/login/oauth/authorize', 'https://github.com/login/oauth/access_token', 'https://api.github.com/user', '', 'http://localhost:9999/auth/oauth/callback', NULL, 1, 1, '模板：填入 OAuth App 凭据并核对 redirect_uri 后启用', '2026-10-30 22:41:36', 'system', '2026-10-30 22:41:36', 'system', 1);

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `config_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置名称',
  `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'LOCAL | ALIYUN_OSS | TENCENT_COS',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Endpoint / Region',
  `bucket` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Bucket',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AccessKey（建议密文存储）',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SecretKey（建议密文存储）',
  `cdn_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'CDN/自定义域名，可空',
  `base_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对象键前缀，如 uploads/',
  `is_default` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否默认：1-是',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider`(`provider` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'OSS/对象存储供应商配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss_config
-- ----------------------------
INSERT INTO `sys_oss_config` VALUES (1, '本地磁盘', 'LOCAL', NULL, NULL, NULL, NULL, NULL, 'uploads', 0, 1, '开发默认，文件落 boot 本地目录', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system');
INSERT INTO `sys_oss_config` VALUES (2114379361417359361, 'aliyunoss', 'ALIYUN_OSS', 'oss-cn-guangzhou.aliyuncs.com', 'cloudwaer-ui', 'key', 'key', NULL, NULL, 1, 1, NULL, '2026-10-25 23:31:38', NULL, '2026-10-25 23:31:38', NULL);

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_type` int(11) NOT NULL COMMENT '权限类型：1-菜单，2-页面，3-操作',
  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径（菜单权限需要）',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父权限ID',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`permission_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1001, 'admin', '后台管理', 1, '', NULL, 0, 'Setting', '后台管理菜单', '2025-11-19 21:14:55', NULL, '2025-11-19 21:14:55', NULL, 1);
INSERT INTO `sys_permission` VALUES (1002, 'admin:permission', '菜单权限', 2, '/admin/permission', 1001, 1, 'Key', '权限管理页面', '2025-11-19 21:15:04', NULL, '2025-11-19 21:15:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1003, 'admin:permission:view', '查看权限', 3, '', 1002, 1, 'Search', '获取权限树 查看权限', '2025-11-19 21:15:07', NULL, '2025-11-19 21:15:07', NULL, 1);
INSERT INTO `sys_permission` VALUES (1004, 'admin:permission:add', '新增权限', 3, '', 1002, 2, 'FolderAdd', '权限页面-新增权限', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1005, 'admin:permission:edit', '编辑权限', 3, '', 1002, 3, 'EditPen', '权限页面-编辑权限', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1006, 'admin:permission:delete', '删除权限', 3, '', 1002, 4, 'Delete', '权限页面-删除权限', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (114214272234, 'codegen:generator:view', '代码生成页面', 2, '/codegen/CodeGenerator', 125634234412342343, 1, '', '代码生成管理页面', '2025-11-21 01:31:10', NULL, '2025-11-21 01:31:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (125634234412342343, 'codegen', '代码生成', 1, NULL, NULL, 100, 'Document', '代码生成管理菜单', '2025-11-21 01:29:10', NULL, '2025-11-21 01:29:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991143600931201000, 'admin:role', '角色管理', 2, '/admin/role', 1001, 3, 'Coin', '角色管理页面', '2025-11-19 21:56:43', NULL, '2025-11-19 21:56:43', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991205164119891970, 'admin:user', '用户管理', 2, '/admin/user', 1001, 2, 'Avatar', '用户管理页面', '2025-11-20 02:01:21', NULL, '2025-11-20 02:01:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242409023631362, 'admin:user:add', '添加用户', 3, '', 1991205164119891970, 0, 'FolderAdd', '新增用户', '2025-11-20 04:29:20', NULL, '2025-11-20 04:29:20', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242808984072194, 'admin:user:delete', '删除用户', 3, '', 1991205164119891970, 0, 'Delete', '删除用户', '2025-11-20 04:30:56', NULL, '2025-11-20 04:30:56', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242997413179393, 'admin:user:edit', '修改用户', 3, '', 1991205164119891970, 0, 'EditPen', '更新用户', '2025-11-20 04:31:41', NULL, '2025-11-20 04:31:41', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991243204884426753, 'admin:user:view', '查询用户', 3, '', 1991205164119891970, 0, 'Search', '分页查询用户列表', '2025-11-20 04:32:30', NULL, '2025-11-20 04:32:30', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244603546398722, 'admin:role:add', '新增角色', 3, '', 1991143600931201000, 0, '', '新增角色', '2025-11-20 04:38:04', NULL, '2025-11-20 04:38:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244778629230593, 'admin:role:delete', '删除角色', 3, '', 1991143600931201000, 0, '', '删除角色', '2025-11-20 04:38:45', NULL, '2025-11-20 04:38:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244964420120578, 'admin:role:edit', '修改角色', 3, '', 1991143600931201000, 0, '', '更新角色', '2025-11-20 04:39:30', NULL, '2025-11-20 04:39:30', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991245154136879106, 'admin:role:view', '查看角色', 3, '', 1991143600931201000, 0, '', '分页查询角色列表', '2025-11-20 04:40:15', NULL, '2025-11-20 04:40:15', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991268323568517122, 'admin:user:assign-role', '分配角色给用户', 3, '', 1991205164119891970, 0, '', '分配角色给用户', '2025-11-20 06:12:19', NULL, '2025-11-20 06:12:19', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991268510370234370, 'admin:role:assign-permission', '分配权限给角色', 3, '', 1991143600931201000, 0, '', '分配权限给角色', '2025-11-20 06:13:04', NULL, '2025-11-20 06:13:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991455913399685122, 'admin:gateway', '动态网关', 1, '', NULL, 2, 'Connection', '', '2025-11-20 18:37:44', NULL, '2025-11-20 18:37:44', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991456923035766785, 'admin:gateway:view', '网关列表', 2, '/gateway', 1991455913399685122, 0, 'DataAnalysis', '查看网关列表', '2025-11-20 18:41:45', NULL, '2025-11-20 18:41:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473796049752066, 'admin:gateway:add', '新增网关', 3, '', 1991456923035766785, 0, '', '新增网关路由', '2025-11-20 19:48:47', NULL, '2025-11-20 19:48:47', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473884234993666, 'admin:gateway:delete', '删除网关路由', 3, '', 1991456923035766785, 0, '', '删除网关路由', '2025-11-20 19:49:08', NULL, '2025-11-20 19:49:08', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473999733542914, 'admin:gateway:edit', '更新网关路由', 3, '', 1991456923035766785, 0, '', '', '2025-11-20 19:49:36', NULL, '2025-11-20 19:49:36', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735617, 'admin:gateway:refresh', '刷新网关路由', 3, '', 1991456923035766785, 0, '', '刷新网关路由', '2025-11-20 19:51:33', NULL, '2025-11-20 19:51:33', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735618, 'codegen:connection:list', '查询数据库连接列表', 3, '', 1991829450476220417, 10, '', '查询数据库连接列表权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735619, 'codegen:connection:page', '分页查询数据库连接', 3, '', 1991829450476220417, 11, '', '分页查询数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735620, 'codegen:connection:detail', '查询数据库连接详情', 3, '', 1991829450476220417, 12, '', '查询数据库连接详情权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735621, 'codegen:connection:test', '测试数据库连接', 3, '', 1991829450476220417, 13, '', '测试数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735622, 'codegen:connection:add', '新增数据库连接', 3, '', 1991829450476220417, 14, '', '新增数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735623, 'codegen:connection:edit', '更新数据库连接', 3, '', 1991829450476220417, 15, '', '更新数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735624, 'codegen:connection:delete', '删除数据库连接', 3, '', 1991829450476220417, 16, '', '删除数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735625, 'codegen:connection:toggle', '启用/禁用数据库连接', 3, '', 1991829450476220417, 17, '', '启用/禁用数据库连接权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735626, 'codegen:metadata:tables', '查询数据库表列表', 3, NULL, 114214272234, 20, NULL, '查询数据库表列表权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735627, 'codegen:metadata:table', '查询表结构元数据', 3, NULL, 114214272234, 21, NULL, '查询表结构元数据权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735628, 'codegen:metadata:tables:metadata', '批量查询表结构元数据', 3, NULL, 114214272234, 22, NULL, '批量查询表结构元数据权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735629, 'codegen:form:generate', '生成表单配置', 3, NULL, 114214272234, 30, NULL, '生成表单配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735630, 'codegen:form:generate:fields', '生成表单字段配置', 3, NULL, 114214272234, 31, NULL, '生成表单字段配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735631, 'codegen:form:save', '保存表单配置', 3, NULL, 114214272234, 32, NULL, '保存表单配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735632, 'codegen:form:config', '查询表单配置', 3, NULL, 114214272234, 33, NULL, '查询表单配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735633, 'codegen:form:config:id', '根据ID查询表单配置', 3, NULL, 114214272234, 34, NULL, '根据ID查询表单配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735634, 'codegen:form:fields', '更新表单字段配置', 3, NULL, 114214272234, 35, NULL, '更新表单字段配置权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735635, 'codegen:form:layout', '调整表单布局', 3, NULL, 114214272234, 36, NULL, '调整表单布局权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735636, 'codegen:generator:backend', '生成后端代码', 3, NULL, 114214272234, 40, NULL, '生成后端代码权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735637, 'codegen:generator:frontend', '生成前端代码', 3, NULL, 114214272234, 41, NULL, '生成前端代码权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735638, 'codegen:generator:permission', '生成权限SQL', 3, NULL, 114214272234, 42, NULL, '生成权限SQL权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735639, 'codegen:generator:all', '生成所有代码', 3, NULL, 114214272234, 43, NULL, '生成所有代码权限（后端、前端、权限SQL）', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735640, 'codegen:generator:preview', '预览代码', 3, NULL, 114214272234, 44, NULL, '代码生成预览权限', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991829450476220417, 'codegen:connection:view', '数据源管理', 2, '/codegen/DatabaseConnection', 125634234412342343, 0, '', '', '2025-11-21 19:22:02', NULL, '2025-11-21 19:22:02', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001100, 'admin:dict:view', '字典管理', 2, '/admin/dict', 1001, 4, 'Notebook', '访问字典管理页面', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001200, 'admin:dict:page', '分页查询', 3, '', 1993000000000001100, 10, '', '分页查询字典', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001210, 'admin:dict:detail', '查询详情', 3, '', 1993000000000001100, 11, '', '根据ID查询字典详情', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001300, 'admin:dict:add', '新增', 3, '', 1993000000000001100, 20, 'FolderAdd', '新增字典项', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001310, 'admin:dict:edit', '更新', 3, '', 1993000000000001100, 21, 'EditPen', '更新字典项', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001320, 'admin:dict:delete', '删除', 3, '', 1993000000000001100, 22, 'Delete', '删除字典项', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001400, 'admin:dict:refresh', '刷新缓存', 3, '', 1993000000000001100, 30, 'Refresh', '刷新字典缓存', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000001, 'workflow', '工作流', 1, '/workflow', 0, 1, 'Operation', '工作流模块', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000010, 'workflow:def:view', '模型设计', 2, '/workflow/definition', 2026021400000000001, 1, 'SetUp', '流程定义列表', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000011, 'workflow:def:list', '查询流程定义', 3, NULL, 2026021400000000010, 1, NULL, '查看流程定义列表', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000012, 'workflow:def:detail', '流程定义详情', 3, NULL, 2026021400000000010, 2, NULL, '查看流程定义详情', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000013, 'workflow:def:save', '保存流程定义', 3, NULL, 2026021400000000010, 3, NULL, '新建/编辑流程定义', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000014, 'workflow:def:publish', '发布/下线流程', 3, NULL, 2026021400000000010, 4, NULL, '发布或下线流程定义', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000016, 'workflow:def:delete', '删除流程定义', 3, NULL, 2026021400000000010, 5, NULL, '删除流程定义', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000020, 'workflow:form:view', '表单管理', 2, '/workflow/form', 2026021400000000001, 2, 'Document', '动态表单管理', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000021, 'workflow:form:list', '查询表单', 3, NULL, 2026021400000000020, 1, NULL, '查看表单列表', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000022, 'workflow:form:detail', '表单详情', 3, NULL, 2026021400000000020, 2, NULL, '查看表单详情', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000023, 'workflow:form:save', '保存表单', 3, NULL, 2026021400000000020, 3, NULL, '新建/编辑表单', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000024, 'workflow:form:delete', '删除表单', 3, NULL, 2026021400000000020, 4, NULL, '删除表单', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000030, 'workflow:apply:view', '流程申请', 2, '/workflow/apply', 2026021400000000001, 3, 'Promotion', '发起流程', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000031, 'workflow:instance:start', '发起流程', 3, NULL, 2026021400000000030, 1, NULL, '发起流程实例', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000040, 'workflow:instance:view', '流程监控', 2, '/workflow/monitor', 2026021400000000001, 4, 'Monitor', '流程实例监控', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000041, 'workflow:instance:list', '查询实例', 3, NULL, 2026021400000000040, 1, NULL, '查看流程实例列表', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000042, 'workflow:instance:detail', '实例详情', 3, NULL, 2026021400000000040, 2, NULL, '查看流程实例详情', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000043, 'workflow:instance:started', '我发起的', 3, NULL, 2026021400000000040, 3, NULL, '查看我发起的流程', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000044, 'workflow:instance:operate', '操作实例', 3, NULL, 2026021400000000040, 4, NULL, '挂起/激活/终止/重启', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000045, 'workflow:instance:delete', '删除实例', 3, NULL, 2026021400000000040, 5, NULL, '删除流程实例', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000050, 'workflow:task:view', '任务管理', 2, '/workflow/task', 2026021400000000001, 5, 'Tickets', '待办/已办任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000051, 'workflow:task:todo', '待办任务', 3, NULL, 2026021400000000050, 1, NULL, '查看待办任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000052, 'workflow:task:done', '已办任务', 3, NULL, 2026021400000000050, 2, NULL, '查看已办任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000053, 'workflow:task:list', '任务列表', 3, NULL, 2026021400000000050, 3, NULL, '按实例查任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000054, 'workflow:task:detail', '任务详情', 3, NULL, 2026021400000000050, 4, NULL, '查看任务详情', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000055, 'workflow:task:claim', '认领任务', 3, NULL, 2026021400000000050, 5, NULL, '认领待办任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000056, 'workflow:task:complete', '审批任务', 3, NULL, 2026021400000000050, 6, NULL, '审批/驳回/转办/退回', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2026021400000000057, 'workflow:task:delete', '删除任务', 3, NULL, 2026021400000000050, 7, NULL, '删除任务', '2026-11-03 06:55:45', 'admin', '2026-11-03 06:55:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (2100000000000000101, 'blog', '博客管理', 1, '', NULL, 2, 'Notebook', '博客管理目录', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000102, 'blog:article', '文章管理', 2, '/blog/articlemanagement', 2100000000000000101, 1, 'Document', '博客文章管理页（GET /blog/article/page）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000103, 'blog:article:view', '查看文章', 3, '', 2100000000000000102, 1, '', '分页查询与详情（GET /blog/article/page|detail）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000104, 'blog:article:add', '新增文章', 3, '', 2100000000000000102, 2, '', '新增博客文章（POST /blog/article/save）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000105, 'blog:article:edit', '修改文章', 3, '', 2100000000000000102, 3, '', '修改博客文章（PUT /blog/article/update）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000106, 'blog:article:delete', '删除文章', 3, '', 2100000000000000102, 4, '', '删除博客文章（DELETE /blog/article/delete）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000107, 'blog:article:import', '导入文章', 3, '', 2100000000000000102, 5, '', '导入 MD/TXT/PDF/DOCX 为文章草稿（POST /blog/article/import）', '2026-10-26 00:00:00', 'system', '2026-10-26 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000120, 'blog:category', '分类管理', 2, '/blog/categorymanagement', 2100000000000000101, 2, 'Folder', '博客分类管理页（GET /blog/category/list）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000121, 'blog:category:view', '查看分类', 3, '', 2100000000000000120, 1, '', '分类列表（GET /blog/category/list）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000122, 'blog:category:add', '新增分类', 3, '', 2100000000000000120, 2, '', '新增/保存分类（POST /blog/category/save）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000123, 'blog:category:edit', '修改分类', 3, '', 2100000000000000120, 3, '', '更新分类（PUT /blog/category/update）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000124, 'blog:category:delete', '删除分类', 3, '', 2100000000000000120, 4, '', '删除分类（DELETE /blog/category/delete）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000130, 'blog:tag', '标签管理', 2, '/blog/tagmanagement', 2100000000000000101, 3, 'PriceTag', '博客标签管理页（GET /blog/tag/list）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000131, 'blog:tag:view', '查看标签', 3, '', 2100000000000000130, 1, '', '标签列表（GET /blog/tag/list）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000132, 'blog:tag:add', '新增标签', 3, '', 2100000000000000130, 2, '', '新增/保存标签（POST /blog/tag/save）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000133, 'blog:tag:edit', '修改标签', 3, '', 2100000000000000130, 3, '', '更新标签（PUT /blog/tag/update）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000134, 'blog:tag:delete', '删除标签', 3, '', 2100000000000000130, 4, '', '删除标签（DELETE /blog/tag/delete）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000156, 'blog:blogger', '博客主管理', 2, '/blog/bloggermanagement', 2100000000000000101, 6, 'User', '博客主管理页：仅列 BLOGGER 角色用户（GET /admin/user/page?roleId=4）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000157, 'blog:blogger:view', '查看博客主', 3, '', 2100000000000000156, 1, '', '批量查询暂停状态（GET /blog/blogger/status）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000158, 'blog:blogger:suspend', '暂停/解封', 3, '', 2100000000000000156, 2, '', '暂停至指定时间与解除（POST /blog/blogger/suspend|release）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000159, 'blog:blogger:revoke', '取消博客主', 3, '', 2100000000000000156, 3, '', '移除 BLOGGER 角色（调 admin /user/assign-roles，服务端码 admin:user:assign-role）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000160, 'blog:setting', '网站设置', 2, '/blog/sitesetting', 2100000000000000101, 8, 'Setting', '博客站点设置页（GET /blog/setting）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000161, 'blog:setting:view', '查看设置', 3, '', 2100000000000000160, 1, '', '站点设置读取（GET /blog/setting；公开 GET /blog/open/setting）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000162, 'blog:setting:edit', '保存设置', 3, '', 2100000000000000160, 2, '', '保存站点设置（POST /blog/setting/save）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000170, 'blog:review', '审核中心', 2, '/blog/reviewcenter', 2100000000000000101, 7, 'CircleCheck', '集中审核评论等（GET /blog/comment/page）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000171, 'blog:review:view', '查看审核', 3, '', 2100000000000000170, 1, '', '审核中心查看', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000230, 'blog:collection', '合集管理', 2, '/blog/collectionmanagement', 2100000000000000101, 5, 'Collection', '博客合集管理页（GET /blog/collection/page）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000231, 'blog:collection:view', '查看合集', 3, '', 2100000000000000230, 1, '', '合集分页/详情（GET /blog/collection/page|detail）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000232, 'blog:collection:add', '新增合集', 3, '', 2100000000000000230, 2, '', '新增合集（POST /blog/collection/save）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000233, 'blog:collection:edit', '修改合集', 3, '', 2100000000000000230, 3, '', '更新合集（PUT /blog/collection/update）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000234, 'blog:collection:delete', '删除合集', 3, '', 2100000000000000230, 4, '', '删除合集并解除文章绑定（DELETE /blog/collection/delete）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000235, 'blog:collection:sort', '合集排序', 3, '', 2100000000000000230, 5, '', '合集内文章手动排序（PUT /blog/collection/article-sort）', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000250, 'sys:file', '文件管理', 2, '/system/filemanagement', 1001, 20, 'FolderOpened', '平台文件与 OSS 配置', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000251, 'sys:file:view', '查看文件', 3, '', 2100000000000000250, 1, '', '文件列表/下载', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000252, 'sys:file:upload', '上传文件', 3, '', 2100000000000000250, 2, '', '文件上传', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000253, 'sys:file:delete', '删除文件', 3, '', 2100000000000000250, 3, '', '删除并同步 OSS', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000254, 'sys:oss:config', 'OSS 配置', 3, '', 2100000000000000250, 4, '', '对象存储供应商配置', '2026-10-27 00:00:00', 'system', '2026-10-27 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000280, 'wiki', '知识库', 1, '', NULL, 40, 'Collection', '知识库管理目录', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000281, 'wiki:space', '空间管理', 2, '/wiki/spacemanagement', 2100000000000000280, 1, 'FolderOpened', '知识库空间列表', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000282, 'wiki:space:view', '查看空间', 3, '', 2100000000000000281, 1, '', 'GET /wiki/space/page|list|detail', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000283, 'wiki:space:add', '新增空间', 3, '', 2100000000000000281, 2, '', 'POST /wiki/space/save', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000284, 'wiki:space:edit', '修改空间', 3, '', 2100000000000000281, 3, '', 'PUT /wiki/space/update', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000285, 'wiki:space:delete', '删除空间', 3, '', 2100000000000000281, 4, '', 'DELETE /wiki/space/delete', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000286, 'wiki:doc', '文档管理', 2, '/wiki/docmanagement', 2100000000000000280, 2, 'Document', '文档树与编辑', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000287, 'wiki:doc:view', '查看文档', 3, '', 2100000000000000286, 1, '', 'GET /wiki/doc/tree|detail', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000288, 'wiki:doc:add', '新增文档', 3, '', 2100000000000000286, 2, '', 'POST /wiki/doc/save', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000289, 'wiki:doc:edit', '修改文档', 3, '', 2100000000000000286, 3, '', 'PUT /wiki/doc/update|move', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000290, 'wiki:doc:delete', '删除文档', 3, '', 2100000000000000286, 4, '', 'DELETE /wiki/doc/delete', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000291, 'wiki:share:view', '查看分享', 3, '', 2100000000000000286, 5, '', 'GET /wiki/share/page', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000292, 'wiki:share:add', '创建分享', 3, '', 2100000000000000286, 6, '', 'POST /wiki/share/save（只读/可写、有效期、密码）', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000293, 'wiki:share:revoke', '吊销分享', 3, '', 2100000000000000286, 7, '', 'DELETE /wiki/share/revoke', '2026-10-28 01:38:23', 'system', '2026-10-28 01:38:23', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000001, 'admin:setting', '服务器设置', 2, '/admin/globalsetting', 1001, 21, 'Tools', '全局动态设置页面', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000002, 'admin:setting:list', '查看设置', 3, '', 2115000000000000001, 1, '', '查询全局设置列表', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000003, 'admin:setting:save', '保存设置', 3, '', 2115000000000000001, 2, '', '保存（未发布）全局设置', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000004, 'admin:setting:publish', '发布设置', 3, '', 2115000000000000001, 3, '', '发布设置并广播各实例实时刷新', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000005, 'admin:setting:reset', '恢复默认', 3, '', 2115000000000000001, 4, '', '恢复默认（回到 yml/代码兜底）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000006, 'admin:oauth:list', '查看第三方登录', 3, '', 2115000000000000001, 5, '', '查询第三方登录供应商列表', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000007, 'admin:oauth:save', '保存第三方登录', 3, '', 2115000000000000001, 6, '', '新增/更新第三方登录供应商', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000008, 'admin:oauth:delete', '删除第三方登录', 3, '', 2115000000000000001, 7, '', '删除第三方登录供应商', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000201, 'message:center', '消息中心', 2, '/message/messagecenter', NULL, 1, 'Bell', '站内信收件箱（用户自助）', '2026-10-30 00:10:00', 'system', '2026-10-30 00:10:00', 'system', 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`role_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', '系统管理员', '2025-11-18 18:45:43', NULL, '2025-11-20 22:17:33', NULL, 1);
INSERT INTO `sys_role` VALUES (2, 'USER', '普通用户', '普通用户', '2025-11-18 18:45:43', NULL, '2025-11-18 18:45:43', NULL, 1);
INSERT INTO `sys_role` VALUES (3, 'GUEST', '访客', '注册默认角色：评论、点赞、申请博客主', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role` VALUES (4, 'BLOGGER', '博客主', '文章完整CRUD（仅限本人）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (2100000000000000191, 4, 2100000000000000102, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2100000000000000192, 4, 2100000000000000103, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2100000000000000193, 4, 2100000000000000104, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2100000000000000194, 4, 2100000000000000105, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2100000000000000195, 4, 2100000000000000106, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2116197593132494850, 3, 2100000000000000153, '2026-10-30 23:56:38', NULL, '2026-10-30 23:56:38', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2116197593132494851, 3, 2115000000000000201, '2026-10-30 23:56:38', NULL, '2026-10-30 23:56:38', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098060025857, 1, 1001, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997378, 1, 1002, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997379, 1, 1003, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997380, 1, 1004, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997381, 1, 1005, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997382, 1, 1006, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997383, 1, 114214272234, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997384, 1, 125634234412342343, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997385, 1, 1991143600931201000, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997386, 1, 1991205164119891970, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997387, 1, 1991242409023631362, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997388, 1, 1991242808984072194, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997389, 1, 1991242997413179393, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997390, 1, 1991243204884426753, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997391, 1, 1991244603546398722, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997392, 1, 1991244778629230593, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997393, 1, 1991244964420120578, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997394, 1, 1991245154136879106, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997395, 1, 1991268323568517122, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997396, 1, 1991268510370234370, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997397, 1, 1991455913399685122, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098080997398, 1, 1991456923035766785, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106241, 1, 1991473796049752066, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106242, 1, 1991473884234993666, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106243, 1, 1991473999733542914, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106244, 1, 1991474490890735617, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106245, 1, 1991474490890735618, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106246, 1, 1991474490890735619, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106247, 1, 1991474490890735620, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106248, 1, 1991474490890735621, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106249, 1, 1991474490890735622, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106250, 1, 1991474490890735623, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106251, 1, 1991474490890735624, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106252, 1, 1991474490890735625, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106253, 1, 1991474490890735626, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106254, 1, 1991474490890735627, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106255, 1, 1991474490890735628, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106256, 1, 1991474490890735629, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106257, 1, 1991474490890735630, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106258, 1, 1991474490890735631, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106259, 1, 1991474490890735632, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106260, 1, 1991474490890735633, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106261, 1, 1991474490890735634, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106262, 1, 1991474490890735635, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106263, 1, 1991474490890735636, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106264, 1, 1991474490890735637, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106265, 1, 1991474490890735638, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106266, 1, 1991474490890735639, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106267, 1, 1991474490890735640, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106268, 1, 1991829450476220417, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106269, 1, 1993000000000001100, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106270, 1, 1993000000000001200, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106271, 1, 1993000000000001210, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106272, 1, 1993000000000001300, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106273, 1, 1993000000000001310, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106274, 1, 1993000000000001320, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098148106275, 1, 1993000000000001400, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215106, 1, 2026021400000000001, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215107, 1, 2026021400000000010, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215108, 1, 2026021400000000011, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215109, 1, 2026021400000000012, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215110, 1, 2026021400000000013, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215111, 1, 2026021400000000014, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215112, 1, 2026021400000000016, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215113, 1, 2026021400000000020, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215114, 1, 2026021400000000021, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215115, 1, 2026021400000000022, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215116, 1, 2026021400000000023, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215117, 1, 2026021400000000024, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215118, 1, 2026021400000000030, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215119, 1, 2026021400000000031, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215120, 1, 2026021400000000040, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215121, 1, 2026021400000000041, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215122, 1, 2026021400000000042, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215123, 1, 2026021400000000043, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215124, 1, 2026021400000000044, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215125, 1, 2026021400000000045, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098215215126, 1, 2026021400000000050, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323970, 1, 2026021400000000051, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323971, 1, 2026021400000000052, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323972, 1, 2026021400000000053, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323973, 1, 2026021400000000054, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323974, 1, 2026021400000000055, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323975, 1, 2026021400000000056, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323976, 1, 2026021400000000057, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323977, 1, 2100000000000000101, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323978, 1, 2100000000000000102, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323979, 1, 2100000000000000103, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323980, 1, 2100000000000000104, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323981, 1, 2100000000000000105, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323982, 1, 2100000000000000106, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323983, 1, 2100000000000000107, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323984, 1, 2100000000000000120, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323985, 1, 2100000000000000121, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323986, 1, 2100000000000000122, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323987, 1, 2100000000000000123, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323988, 1, 2100000000000000124, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323989, 1, 2100000000000000130, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323990, 1, 2100000000000000131, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323991, 1, 2100000000000000132, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323992, 1, 2100000000000000133, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323993, 1, 2100000000000000134, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323994, 1, 2100000000000000156, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323995, 1, 2100000000000000157, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323996, 1, 2100000000000000158, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323997, 1, 2100000000000000159, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098282323998, 1, 2100000000000000160, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238529, 1, 2100000000000000161, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238530, 1, 2100000000000000162, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238531, 1, 2100000000000000170, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238532, 1, 2100000000000000171, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238533, 1, 2100000000000000230, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238534, 1, 2100000000000000231, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238535, 1, 2100000000000000232, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238536, 1, 2100000000000000233, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238537, 1, 2100000000000000234, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238538, 1, 2100000000000000235, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238539, 1, 2100000000000000250, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238540, 1, 2100000000000000251, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238541, 1, 2100000000000000252, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238542, 1, 2100000000000000253, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238543, 1, 2100000000000000254, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238544, 1, 2100000000000000280, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238545, 1, 2100000000000000281, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238546, 1, 2100000000000000282, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238547, 1, 2100000000000000283, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238548, 1, 2100000000000000284, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238549, 1, 2100000000000000285, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098345238550, 1, 2100000000000000286, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456257, 1, 2100000000000000287, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456258, 1, 2100000000000000288, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456259, 1, 2100000000000000289, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456260, 1, 2100000000000000290, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456261, 1, 2100000000000000291, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456262, 1, 2100000000000000292, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456263, 1, 2100000000000000293, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456264, 1, 2115000000000000001, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456265, 1, 2115000000000000002, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456266, 1, 2115000000000000003, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456267, 1, 2115000000000000004, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456268, 1, 2115000000000000005, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456269, 1, 2115000000000000006, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456270, 1, 2115000000000000007, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456271, 1, 2115000000000000008, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117562098479456272, 1, 2115000000000000201, '2026-11-03 18:18:41', NULL, '2026-11-03 18:18:41', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393314058241, 2, 2026021400000000001, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029761, 2, 2026021400000000010, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029762, 2, 2026021400000000011, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029763, 2, 2026021400000000012, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029764, 2, 2026021400000000013, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029765, 2, 2026021400000000014, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029766, 2, 2026021400000000016, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029767, 2, 2026021400000000020, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029768, 2, 2026021400000000021, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393335029769, 2, 2026021400000000022, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944321, 2, 2026021400000000023, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944322, 2, 2026021400000000024, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944323, 2, 2026021400000000030, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944324, 2, 2026021400000000031, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944325, 2, 2026021400000000040, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944326, 2, 2026021400000000041, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944327, 2, 2026021400000000042, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944328, 2, 2026021400000000043, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944329, 2, 2026021400000000044, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944330, 2, 2026021400000000045, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944331, 2, 2026021400000000050, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944332, 2, 2026021400000000051, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944333, 2, 2026021400000000052, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944334, 2, 2026021400000000053, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393397944335, 2, 2026021400000000054, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053186, 2, 2026021400000000055, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053187, 2, 2026021400000000056, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053188, 2, 2026021400000000057, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053189, 2, 2100000000000000101, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053190, 2, 2100000000000000102, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053191, 2, 2100000000000000103, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053192, 2, 2100000000000000104, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053193, 2, 2100000000000000105, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053194, 2, 2100000000000000106, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053195, 2, 2100000000000000120, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053196, 2, 2100000000000000121, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053197, 2, 2100000000000000122, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053198, 2, 2100000000000000123, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053199, 2, 2100000000000000124, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053200, 2, 2100000000000000130, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053201, 2, 2100000000000000131, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053202, 2, 2100000000000000132, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053203, 2, 2100000000000000133, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053204, 2, 2100000000000000134, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053205, 2, 2100000000000000151, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053206, 2, 2100000000000000152, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393465053207, 2, 2100000000000000153, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393527967745, 2, 1001, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550658, 2, 1002, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550659, 2, 1003, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550660, 2, 1004, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550661, 2, 1005, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550662, 2, 1006, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550663, 2, 1991205164119891970, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550664, 2, 1991242409023631362, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550665, 2, 1991242808984072194, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550666, 2, 1991242997413179393, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393540550667, 2, 1991243204884426753, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076609, 2, 1991268323568517122, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076610, 2, 1991143600931201000, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076611, 2, 1991244603546398722, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076612, 2, 1991244778629230593, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076613, 2, 1991244964420120578, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076614, 2, 1991245154136879106, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076615, 2, 1991268510370234370, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076616, 2, 1993000000000001100, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076617, 2, 1993000000000001200, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076618, 2, 1993000000000001210, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076619, 2, 1993000000000001300, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393595076620, 2, 1993000000000001310, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185473, 2, 1993000000000001320, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185474, 2, 1993000000000001400, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185475, 2, 2100000000000000250, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185476, 2, 2100000000000000251, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185477, 2, 2100000000000000252, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185478, 2, 2100000000000000253, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393662185479, 2, 2100000000000000254, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100033, 2, 2115000000000000001, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100034, 2, 2115000000000000002, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100035, 2, 2115000000000000003, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100036, 2, 2115000000000000004, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100037, 2, 2115000000000000005, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100038, 2, 2115000000000000006, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100039, 2, 2115000000000000007, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2117631393725100040, 2, 2115000000000000008, '2026-11-03 22:54:03', NULL, '2026-11-03 22:54:03', NULL, 1);

-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `setting_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键，如 captcha.enabled',
  `setting_value` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置值；NULL=未覆盖（走 yml/代码兜底）；SECRET 类型密文存储',
  `published_value` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '已发布值（快照重建源；NULL=从未发布）',
  `value_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STRING' COMMENT '值类型：STRING|BOOL|INT|LONG|JSON|SECRET',
  `group_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '分组：captcha|auth|jwt|security|oauth|file|system',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL,
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `update_time` datetime NOT NULL,
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除 1-有效 2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_setting_key`(`setting_key` ASC) USING BTREE,
  INDEX `idx_group_key`(`group_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '全局动态设置（DB 优先，yml 兜底）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_setting
-- ----------------------------
INSERT INTO `sys_setting` VALUES (2115000000000000101, 'captcha.enabled', 'true', 'true', 'BOOL', 'captcha', '登录验证码开关（覆盖 yml cloudwaer.captcha.enabled）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000102, 'captcha.expire-seconds', '60', '60', 'INT', 'captcha', '验证码有效期（秒），覆盖 cloudwaer.captcha.expire-seconds', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000103, 'jwt.expiration', NULL, NULL, 'LONG', 'jwt', 'Token 有效期（毫秒），覆盖 jwt.expiration', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000104, 'jwt.allow-multiple-login', 'true', 'true', 'BOOL', 'jwt', '是否允许多处登录，覆盖 jwt.allow-multiple-login', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000105, 'auth.register.enabled', 'true', 'true', 'BOOL', 'auth', '匿名注册开关（默认开启）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000114, 'security.password-transport.enabled', 'true', 'true', 'BOOL', 'security', '登录密码传输加密开关（RSA）', '2026-10-29 23:00:00', 'system', '2026-10-29 23:00:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000115, 'security.password-transport.public-key', '-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtdOp+wQL+HuGeXJXI7Uw\nMftWGJNP1LN8OSlPOZRFR/O57pYsSOmE7Vp8xA/4kQ1gYvtcXkhUsOUnHjYlUiBp\nwgIfe9Y2JW6Q69vz738nTGs4mghFnvBiBO22gTt1sybgrYi3RhzRtsoovDSEStAc\n+mkRwQSa9iW/AiJY1xu37t5cESoN3Mqx3Rxb8zIogGrEKr1khD5up2+anOjSQmwk\nQ8aa7loaNaG2mYgMDOr4LAHUtA6GNVRW3eHO04W5fsAPcie7cvy5ZnueyiPfVG04\nG89MBRpA7+6cdXaRnQRru2MBo5pjjxfa3zE1c3BXaGImmRWAMJ3uaAl7g9HEAAa9\nswIDAQAB\n-----END PUBLIC KEY-----', '-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtdOp+wQL+HuGeXJXI7Uw\nMftWGJNP1LN8OSlPOZRFR/O57pYsSOmE7Vp8xA/4kQ1gYvtcXkhUsOUnHjYlUiBp\nwgIfe9Y2JW6Q69vz738nTGs4mghFnvBiBO22gTt1sybgrYi3RhzRtsoovDSEStAc\n+mkRwQSa9iW/AiJY1xu37t5cESoN3Mqx3Rxb8zIogGrEKr1khD5up2+anOjSQmwk\nQ8aa7loaNaG2mYgMDOr4LAHUtA6GNVRW3eHO04W5fsAPcie7cvy5ZnueyiPfVG04\nG89MBRpA7+6cdXaRnQRru2MBo5pjjxfa3zE1c3BXaGImmRWAMJ3uaAl7g9HEAAa9\nswIDAQAB\n-----END PUBLIC KEY-----', 'STRING', 'security', '密码加密公钥 PEM（下发登录页）', '2026-10-29 23:00:00', 'system', '2026-10-29 23:00:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000116, 'security.password-transport.private-key', '-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC106n7BAv4e4Z5\nclcjtTAx+1YYk0/Us3w5KU85lEVH87nulixI6YTtWnzED/iRDWBi+1xeSFSw5Sce\nNiVSIGnCAh971jYlbpDr2/PvfydMaziaCEWe8GIE7baBO3WzJuCtiLdGHNG2yii8\nNIRK0Bz6aRHBBJr2Jb8CIljXG7fu3lwRKg3cyrHdHFvzMiiAasQqvWSEPm6nb5qc\n6NJCbCRDxpruWho1obaZiAwM6vgsAdS0DoY1VFbd4c7Thbl+wA9yJ7ty/Llme57K\nI99UbTgbz0wFGkDv7px1dpGdBGu7YwGjmmOPF9rfMTVzcFdoYiaZFYAwne5oCXuD\n0cQABr2zAgMBAAECggEAVy167E1OZ/7hxMzbltVCnFRDPVk0B2lfABEcWSEL7qme\n1hPOe87q2tptw5tctD9TnWGjMdlAMehMzyFq12uPCWP3MGrOXzgZ59v3oza14t/t\nrXyV1NSwSmim/mNB1Xmoc8t/A/DBZACdc09CtpP9sNo7lu3cVnRWlnFdYxV5JN9M\n0mKYWILTyE5nKCcLmkbJbIwJtWgiMpH8ZlRa7pmwu4LLEBbhGNrM/K9EMpfA8VH9\nybaSA+0mFIp/lqwOk5Zrliy86Lr+MFdfaBBYrRsAOrhyMsL/dwDnGjBsI/pBAtaW\naExQzEOnN9yFk7XpWTx9kcGGxI1llzd186nOMljbwQKBgQDL8vbK60I9twGhrFKi\nI4g1ow3EPISwk/qon9mXpQVyzXkDxWnKSlMXIHITPrFFkkr+Ozaw1wSLfo6llje7\nMeFh63DrbVAWSzJ6QkHqDIi97r9hyWDXWKRk2QtOCdVmTIjv1gPMlvSsJnpjjmVy\nfMfQGCmJu+aCRYY1xGWbPk4bEQKBgQDkO1bXoutwdQQqFXvBuoJK2U8D180adYO3\nYBLq4QR5/IoegnXNRgS2lSBT8NnoO4qv/L4fjWi5hC2JVXSqeAqptWDeFqdiBCa/\nWQGDoeeo+b7w53TknPDoP7EhnURCzge7fCCHShgbSU/ggkEYxpmMmUXWgIt5uCKK\nNRHGmTikgwKBgE2zGuLkDjCE8FIv3Kt47gPBQxVoGQVM20oy3Csx64l5TolpRIyV\nFGLTMaKzV8YxnfCNjqCAynJ6nM3pFQJ0VnF/yV1oIALeUzTQ2VvZup7hvnf6yLJC\nSx9AsVXh2TNIJUN6PahMOpr7bdVQAsZDU7yr9+dJduBarG7/i9ZBPnCBAoGAVRkb\nBEQm7UN0Tgy8nDtioW3tXE12mEsA1bro0tMbzDAxf7a9HdDhqQJpj2LanRC2YXAn\no7QiPCngoLhQS89+ilCq0aQ4vP83Q1l4ixAvRH36h/CFUUiDOtWkwhfVf+M6Vtmt\nd0RVQLdRJTDr/MTwM9BijdxYJQZXOpkXC6nNyOkCgYBRBYD0w1VX6vTEWjP68Fa9\nwxzGVeNQehaIrG7e5yfjJMDss0wDaZMaJz8OGLjLGbD/vQyCj/kx0XrPGfMOnuKX\nE+MwCiay89FFUj6tqf99UzKFTNmbX0oCxaiSm0crHc/raeDNSGYd/WW3J0ewOisd\nTGGvM94bDKby72qg1g0N+A==\n-----END PRIVATE KEY-----', '-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC106n7BAv4e4Z5\nclcjtTAx+1YYk0/Us3w5KU85lEVH87nulixI6YTtWnzED/iRDWBi+1xeSFSw5Sce\nNiVSIGnCAh971jYlbpDr2/PvfydMaziaCEWe8GIE7baBO3WzJuCtiLdGHNG2yii8\nNIRK0Bz6aRHBBJr2Jb8CIljXG7fu3lwRKg3cyrHdHFvzMiiAasQqvWSEPm6nb5qc\n6NJCbCRDxpruWho1obaZiAwM6vgsAdS0DoY1VFbd4c7Thbl+wA9yJ7ty/Llme57K\nI99UbTgbz0wFGkDv7px1dpGdBGu7YwGjmmOPF9rfMTVzcFdoYiaZFYAwne5oCXuD\n0cQABr2zAgMBAAECggEAVy167E1OZ/7hxMzbltVCnFRDPVk0B2lfABEcWSEL7qme\n1hPOe87q2tptw5tctD9TnWGjMdlAMehMzyFq12uPCWP3MGrOXzgZ59v3oza14t/t\nrXyV1NSwSmim/mNB1Xmoc8t/A/DBZACdc09CtpP9sNo7lu3cVnRWlnFdYxV5JN9M\n0mKYWILTyE5nKCcLmkbJbIwJtWgiMpH8ZlRa7pmwu4LLEBbhGNrM/K9EMpfA8VH9\nybaSA+0mFIp/lqwOk5Zrliy86Lr+MFdfaBBYrRsAOrhyMsL/dwDnGjBsI/pBAtaW\naExQzEOnN9yFk7XpWTx9kcGGxI1llzd186nOMljbwQKBgQDL8vbK60I9twGhrFKi\nI4g1ow3EPISwk/qon9mXpQVyzXkDxWnKSlMXIHITPrFFkkr+Ozaw1wSLfo6llje7\nMeFh63DrbVAWSzJ6QkHqDIi97r9hyWDXWKRk2QtOCdVmTIjv1gPMlvSsJnpjjmVy\nfMfQGCmJu+aCRYY1xGWbPk4bEQKBgQDkO1bXoutwdQQqFXvBuoJK2U8D180adYO3\nYBLq4QR5/IoegnXNRgS2lSBT8NnoO4qv/L4fjWi5hC2JVXSqeAqptWDeFqdiBCa/\nWQGDoeeo+b7w53TknPDoP7EhnURCzge7fCCHShgbSU/ggkEYxpmMmUXWgIt5uCKK\nNRHGmTikgwKBgE2zGuLkDjCE8FIv3Kt47gPBQxVoGQVM20oy3Csx64l5TolpRIyV\nFGLTMaKzV8YxnfCNjqCAynJ6nM3pFQJ0VnF/yV1oIALeUzTQ2VvZup7hvnf6yLJC\nSx9AsVXh2TNIJUN6PahMOpr7bdVQAsZDU7yr9+dJduBarG7/i9ZBPnCBAoGAVRkb\nBEQm7UN0Tgy8nDtioW3tXE12mEsA1bro0tMbzDAxf7a9HdDhqQJpj2LanRC2YXAn\no7QiPCngoLhQS89+ilCq0aQ4vP83Q1l4ixAvRH36h/CFUUiDOtWkwhfVf+M6Vtmt\nd0RVQLdRJTDr/MTwM9BijdxYJQZXOpkXC6nNyOkCgYBRBYD0w1VX6vTEWjP68Fa9\nwxzGVeNQehaIrG7e5yfjJMDss0wDaZMaJz8OGLjLGbD/vQyCj/kx0XrPGfMOnuKX\nE+MwCiay89FFUj6tqf99UzKFTNmbX0oCxaiSm0crHc/raeDNSGYd/WW3J0ewOisd\nTGGvM94bDKby72qg1g0N+A==\n-----END PRIVATE KEY-----', 'SECRET', 'security', '密码加密私钥 PEM（仅服务端）', '2026-10-29 23:00:00', 'system', '2026-10-29 23:00:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115832954620153858, 'log.enabled', 'true', 'true', 'BOOL', 'system', '请求日志开关', '2026-10-29 23:47:41', NULL, '2026-10-29 23:47:41', NULL, 1);
INSERT INTO `sys_setting` VALUES (2116203114816274433, 'proxy.enabled', 'true', 'true', 'BOOL', 'system', '出站 HTTP 代理开关（发布后热生效）', '2026-10-31 00:18:34', NULL, '2026-10-31 00:18:34', NULL, 1);
INSERT INTO `sys_setting` VALUES (2116203114862411777, 'proxy.host', '127.0.0.1', '127.0.0.1', 'STRING', 'system', '代理主机（如 127.0.0.1）', '2026-10-31 00:18:34', NULL, '2026-10-31 00:18:34', NULL, 1);
INSERT INTO `sys_setting` VALUES (2116203114862411778, 'proxy.port', '65533', '65533', 'INT', 'system', '代理端口（如 7890）', '2026-10-31 00:18:34', NULL, '2026-10-31 00:18:34', NULL, 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$QglwBinM.wSdYblvNtTIveMDfltN./vRjPczrA9FnZNQwU1SnvwU6', '管理员', 'chen_a_nan@qq.com', '13333333333', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/11/10/e924ef1d29f6427da38222ac2a1596c1.jpg', '2025-11-18 18:45:43', NULL, '2026-01-12 07:32:35', NULL, 1);
INSERT INTO `sys_user` VALUES (1992208936665325569, 'test', '$2a$10$B6/HWEMm37XBm3vAJDpIduGCMam.iF8LspVQg21weWSTghugFz.he', '这是测试账号', 'wx-gpt-plus@qq.com', '', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', '2025-11-22 20:29:59', NULL, '2026-01-12 07:32:39', NULL, 1);
INSERT INTO `sys_user` VALUES (2119789131196428290, 'smoke_u1', '$2a$10$f6phXTfmdGA8S5/j.F83U.lieW/QQMh/AdKXTYGJ8S.CHb6fsOqQq', 'smoke_u1', NULL, NULL, NULL, '2026-11-09 21:48:07', NULL, '2026-11-09 21:48:07', NULL, 1);
INSERT INTO `sys_user` VALUES (2119789131762659329, 'smoke_u2', '$2a$10$115F4N9FBdxK3ym7Yhsu.e2Si9KAyfvYtS9Do8vrM8ST5ilzXm8Ci', 'smoke_u2', NULL, NULL, NULL, '2026-11-09 21:48:07', NULL, '2026-11-09 21:48:07', NULL, 1);
INSERT INTO `sys_user` VALUES (2119789132618297345, 'smoke_u3', '$2a$10$pRxglJlh4ZNl/NP2OvJvl.bfzG0WJV87.N62rsQt.FOSH1Y8YO/6.', 'smoke_u3', NULL, NULL, NULL, '2026-11-09 21:48:08', NULL, '2026-11-09 21:48:08', NULL, 1);

-- ----------------------------
-- Table structure for sys_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_oauth`;
CREATE TABLE `sys_user_oauth`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '系统用户ID（sys_user.id）',
  `provider_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '供应商类型：GITHUB|GITEE|WECHAT|GOOGLE|CUSTOM',
  `provider_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID（GitHub 为数字 id）',
  `provider_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方用户名（GitHub login）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方邮箱快照',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方头像快照',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_provider_user`(`provider_type` ASC, `provider_user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_provider`(`user_id` ASC, `provider_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '第三方账号绑定（一个用户同供应商仅绑一个第三方账号）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_oauth
-- ----------------------------
INSERT INTO `sys_user_oauth` VALUES (2116570958196293633, 1, 'GITHUB', '169438231', 'ANANAkiX', '1066863570@qq.com', 'https://avatars.githubusercontent.com/u/169438231?v=4', '2026-11-01 00:40:15', NULL, '2026-11-01 00:40:15', NULL, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2, 1, '2025-11-20 02:28:41', NULL, '2025-11-20 02:28:52', NULL, 1);
INSERT INTO `sys_user_role` VALUES (1991967599781089282, 1, 1, '2025-11-22 04:30:59', NULL, '2025-11-22 04:30:59', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2114732977282048002, 1992208936665325569, 2, '2026-10-26 22:56:46', NULL, '2026-10-26 22:56:46', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2114732977344962561, 1992208936665325569, 4, '2026-10-26 22:56:46', NULL, '2026-10-26 22:56:46', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2119881197402382338, 2119789131762659329, 1, '2026-11-10 03:53:58', NULL, '2026-11-10 03:53:58', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2119881197666623489, 2119789132618297345, 1, '2026-11-10 03:53:58', NULL, '2026-11-10 03:53:58', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2120469137312645122, 2119789131196428290, 1, '2026-11-11 18:50:13', NULL, '2026-11-11 18:50:13', NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
