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

 Date: 29/10/2026 22:18:11
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
INSERT INTO `sys_dict` VALUES (1992000000000004000, 'flowable_type', '流程类型', 0, '流程类型字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (2013027807789092865, 'wf_process_status', '流程实例', 0, '流程实例状态', '2026-01-19 07:16:44', NULL, '2026-01-19 07:17:12', NULL, 1);
INSERT INTO `sys_dict` VALUES (2013029000000000001, 'permission_type', '权限类型', 0, '权限类型字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict` VALUES (2013029000000000003, 'flowable_priority', '任务优先级', 0, '流程任务优先级字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict` VALUES (2013029000000000004, 'wf_model_status', '模型版本状态', 0, '流程模型版本状态字典', '2026-10-23 23:21:34', 'system', '2026-10-23 23:21:34', 'system', 1);

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
INSERT INTO `sys_dict_item` VALUES (1992000000000004001, 1992000000000004000, 'leave', 'leave', '请假类型', 0, '流程类型-请假', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000004002, 1992000000000004000, 'ea', 'ea', '审批类型', 1, '流程类型-审批', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (1992000000000004003, 1992000000000004000, 'expense', 'expense', '报销类型', 2, '流程类型-报销', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013028106117353473, 2013027807789092865, 'running', '0', '运行中', 0, '', '2026-01-19 07:17:55', NULL, '2026-01-19 07:17:55', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013028154754502658, 2013027807789092865, 'completed', '1', '已完成', 1, '', '2026-01-19 07:18:07', NULL, '2026-01-19 07:19:34', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013028202070446082, 2013027807789092865, 'suspended', '2', '已挂起', 2, '', '2026-01-19 07:18:18', NULL, '2026-01-19 07:19:43', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013028251974275073, 2013027807789092865, 'rejected', '3', '被拒绝', 3, '', '2026-01-19 07:18:30', NULL, '2026-01-19 07:19:38', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013028311856353281, 2013027807789092865, 'terminated', '4', '已终止', 4, '', '2026-01-19 07:18:44', NULL, '2026-01-19 07:19:48', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013028468014485506, 2013027807789092865, 'canceled', '5', '已取消', 5, '', '2026-01-19 07:19:22', NULL, '2026-01-19 07:19:28', NULL, 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000101, 2013029000000000001, '1', '1', '菜单', 0, '权限类型-菜单', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000102, 2013029000000000001, '2', '2', '页面', 1, '权限类型-页面', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000103, 2013029000000000001, '3', '3', '操作', 2, '权限类型-操作', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000301, 2013029000000000003, 'low', 'low', '低级', 0, '任务优先级-低级', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000302, 2013029000000000003, 'normal', 'normal', '正常', 1, '任务优先级-正常', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000303, 2013029000000000003, 'urgent', 'urgent', '紧急', 2, '任务优先级-紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000304, 2013029000000000003, 'very_urgent', 'very_urgent', '非常紧急', 3, '任务优先级-非常紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000401, 2013029000000000004, '0', '0', '草稿', 0, '模型版本-草稿', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000402, 2013029000000000004, '1', '1', '已发布', 1, '模型版本-已发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000403, 2013029000000000004, '2', '2', '已归档', 2, '模型版本-已归档', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000404, 2013029000000000004, '3', '3', '已更新未发布', 3, '模型版本-已更新未发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000405, 2013029000000000004, '4', '4', '已回滚未发布', 4, '模型版本-已回滚未发布', '2026-10-23 23:21:38', 'system', '2026-10-23 23:21:38', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000406, 2013029000000000004, '5', '5', '已关闭', 5, '模型版本-已关闭', '2026-10-23 23:49:00', 'system', '2026-10-23 23:49:00', 'system', 1);

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
INSERT INTO `sys_gateway_route` VALUES (1991546479613448194, 'cloudwaer-codegen-serve', 'lb://cloudwaer-codegen-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/codegen/**\"}}]', NULL, 0, '代码生成服务的网关转发配置', '2025-11-21 00:37:37', NULL, '2025-11-21 00:37:37', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (1992205324727853058, 'ws-cloudwaer-im-service', 'lb:ws://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/ws/**\"}}]', NULL, 0, '仅让 WebSocket 握手走这条，避免影响 REST 调用\n', '2025-11-22 20:15:37', NULL, '2026-01-06 05:56:00', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (1992239401883295746, 'rest-cloudwaer-im-service', 'lb://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/**\"}}]', NULL, 1, '处理 /im/** 的 HTTP 接口', '2025-11-22 22:31:02', NULL, '2026-01-06 05:56:13', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (2011458231829864450, 'cloudwaer-flowable-serve', 'lb://cloudwaer-flowable-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/flowable/**\"}}]', NULL, 0, 'cloudwaer-flowable-serve 的基础网关转发（与 admin/codegen 一致，保留 /flowable 前缀，勿加 StripPrefix）', '2026-01-14 23:19:48', NULL, '2026-10-23 21:20:13', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (2115773090837475330, 'cloudwaer-wiki-serve', 'lb://cloudwaer-wiki-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/wiki/**\"}}]', NULL, 0, 'cloudwaer-wiki-serve的基础网关转发', '2026-10-29 19:49:49', NULL, '2026-10-29 19:49:49', NULL, 1);

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
INSERT INTO `sys_permission` VALUES (1991455913399685122, 'admin:gateway', '动态网关', 1, '', NULL, 1, 'Connection', '', '2025-11-20 18:37:44', NULL, '2025-11-20 18:37:44', NULL, 1);
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
INSERT INTO `sys_permission` VALUES (2011849591380627457, 'flowable', '流程管理', 1, '', NULL, 0, 'Cpu', 'flowable流程页面', '2026-01-16 01:14:56', NULL, '2026-01-16 01:14:56', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011850922522693634, 'flowable:model', '流程模型管理', 2, '/flowable/modelmanagement', 2011849591380627457, 1, 'Document', '流程模型的新增、编辑、发布和管理', '2026-01-16 01:20:13', NULL, '2026-01-16 01:20:13', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851483955449857, 'flowable:application:view', '流程申请', 2, '/flowable/processapplication', 2011849591380627457, 4, 'ChatLineRound', '用户申请和启动新的流程实例', '2026-01-16 01:22:27', NULL, '2026-01-16 01:22:27', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683906, 'flowable:monitor:view', '流程监控', 2, '/flowable/processmonitor', 2011849591380627457, 3, 'Crop', '监控和管理运行中的流程实例', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683908, 'flowable:model:list', '查看模型列表', 3, NULL, 2011850922522693634, 2, NULL, '查看流程模型列表', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683909, 'flowable:model:save', '保存模型', 3, NULL, 2011850922522693634, 3, NULL, '新增或编辑流程模型', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683910, 'flowable:model:delete', '删除模型', 3, NULL, 2011850922522693634, 4, NULL, '删除流程模型', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683911, 'flowable:model:publish', '发布模型', 3, NULL, 2011850922522693634, 5, NULL, '发布流程模型', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683912, 'flowable:model:copy', '复制模型', 3, NULL, 2011850922522693634, 6, NULL, '复制流程模型', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683913, 'flowable:model:detail', '查看模型详情', 3, NULL, 2011850922522693634, 7, NULL, '查看流程模型详情', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683914, 'flowable:model:rollback', '回滚模型', 3, NULL, 2011850922522693634, 8, NULL, '回滚到指定版本', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683916, 'flowable:designer:save', '保存流程设计', 3, NULL, 2011850922522693634, 2, NULL, '保存BPMN流程设计', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683917, 'flowable:designer:import', '导入BPMN', 3, NULL, 2011850922522693634, 3, NULL, '导入BPMN文件', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683918, 'flowable:designer:export', '导出BPMN', 3, NULL, 2011850922522693634, 4, NULL, '导出BPMN文件', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683919, 'flowable:designer:preview', '预览流程', 3, NULL, 2011850922522693634, 5, NULL, '预览流程图', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683920, 'flowable:designer:validate', '验证BPMN', 3, NULL, 2011850922522693634, 6, NULL, '验证BPMN文件格式', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683921, 'flowable:application:route', '流程申请页面', 2, '/flowable/process-application', 2011851483955449857, 1, NULL, '流程申请页面路由权限', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:14', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683922, 'flowable:process:start', '启动流程', 3, NULL, 2011851483955449857, 2, NULL, '启动新的流程实例', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683923, 'flowable:process:definitions', '查看流程定义', 3, NULL, 2011851483955449857, 3, NULL, '查看可申请的流程定义列表', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683924, 'flowable:process:detail', '查看流程详情', 3, NULL, 2011851483955449857, 4, NULL, '查看流程实例详情', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683925, 'flowable:process:variables', '查看流程变量', 3, NULL, 2011851483955449857, 5, NULL, '查看流程实例变量', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683926, 'flowable:process:history', '查看流程历史', 3, NULL, 2011851483955449857, 6, NULL, '查看流程操作历史', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683927, 'flowable:monitor:route', '流程监控页面', 2, '/flowable/process-monitor', 2011851625458683906, 1, NULL, '流程监控页面路由权限', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:18', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683928, 'flowable:monitor:instances', '查看流程实例', 3, NULL, 2011851625458683906, 2, NULL, '查看所有流程实例', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683929, 'flowable:monitor:suspend', '挂起流程', 3, NULL, 2011851625458683906, 3, NULL, '挂起正在运行的流程', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683930, 'flowable:monitor:activate', '激活流程', 3, NULL, 2011851625458683906, 4, NULL, '激活已挂起的流程', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683931, 'flowable:monitor:terminate', '终止流程', 3, NULL, 2011851625458683906, 5, NULL, '终止正在运行的流程', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683932, 'flowable:monitor:delete', '删除流程实例', 3, NULL, 2011851625458683906, 6, NULL, '删除流程实例', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683933, 'flowable:monitor:diagram', '查看流程图', 3, NULL, 2011851625458683906, 7, NULL, '查看流程实例图', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683934, 'flowable:monitor:batch', '批量操作', 3, NULL, 2011851625458683906, 8, NULL, '批量挂起/激活/终止流程', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683935, 'flowable:task:view', '任务管理', 2, '/flowable/taskmanagement', 2011849591380627457, 5, 'Check', '任务管理菜单', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683936, 'flowable:task:route', '任务管理页面', 2, '/flowable/task-management', 2011851625458683935, 1, NULL, '任务管理页面路由权限', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:24', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683937, 'flowable:task:todo', '查看待办任务', 3, NULL, 2011851625458683935, 2, NULL, '查看当前用户的待办任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683938, 'flowable:task:done', '查看已办任务', 3, NULL, 2011851625458683935, 3, NULL, '查看当前用户的已办任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683939, 'flowable:task:claim', '认领任务', 3, NULL, 2011851625458683935, 4, NULL, '认领未分配的任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683940, 'flowable:task:complete', '完成任务', 3, NULL, 2011851625458683935, 5, NULL, '完成当前任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683941, 'flowable:task:detail', '查看任务详情', 3, NULL, 2011851625458683935, 6, NULL, '查看任务详情', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683942, 'flowable:task:delete', '删除任务', 3, NULL, 2011851625458683935, 7, NULL, '删除任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683943, 'flowable:task:batch', '批量操作', 3, NULL, 2011851625458683935, 8, NULL, '批量认领或完成任务', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683944, 'flowable:bpmn:deploy', '部署BPMN', 3, NULL, 2011850922522693634, 7, NULL, '部署BPMN流程定义', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683945, 'flowable:bpmn:update', '更新BPMN', 3, NULL, 2011850922522693634, 8, NULL, '更新BPMN流程定义', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683946, 'flowable:bpmn:delete', '删除部署', 3, NULL, 2011850922522693634, 9, NULL, '删除BPMN部署', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683947, 'flowable:bpmn:extract', '提取节点', 3, NULL, 2011850922522693634, 10, NULL, '从BPMN提取节点信息', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683948, 'flowable:node:action:list', '查看节点动作', 3, NULL, 2011850922522693634, 9, NULL, '查看节点动作列表', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683949, 'flowable:node:action:save', '保存节点动作', 3, NULL, 2011850922522693634, 10, NULL, '保存节点动作配置', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683950, 'flowable:node:action:batch', '批量保存节点动作', 3, NULL, 2011850922522693634, 11, NULL, '批量保存节点动作', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683951, 'flowable:node:action:delete', '删除节点动作', 3, NULL, 2011850922522693634, 12, NULL, '删除节点动作', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683952, 'flowable:node:action:status', '更新节点动作状态', 3, NULL, 2011850922522693634, 13, NULL, '启用或禁用节点动作', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683953, 'flowable:node:action:config', '获取节点动作配置', 3, NULL, 2011850922522693634, 14, NULL, '获取节点动作配置', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683957, 'flowable:statistics:dashboard', '查看统计面板', 3, NULL, 2011849591380627457, 6, NULL, '查看Flowable统计面板', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683958, 'flowable:statistics:report', '生成报表', 3, NULL, 2011849591380627457, 7, NULL, '生成流程统计报表', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683959, 'flowable:statistics:export', '导出报表', 3, NULL, 2011849591380627457, 8, NULL, '导出统计报表', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683960, 'flowable:config:global', '全局配置', 3, NULL, 2011849591380627457, 9, NULL, 'Flowable全局配置管理', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683961, 'flowable:config:engine', '引擎配置', 3, NULL, 2011849591380627457, 10, NULL, 'Flowable引擎配置', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683962, 'flowable:config:update', '更新配置', 3, NULL, 2011849591380627457, 11, NULL, '更新Flowable配置', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2013024515860238337, 'other', '其他权限集合', 1, '', NULL, 999, '', '', '2026-01-19 07:03:39', NULL, '2026-01-19 07:03:39', NULL, 0);
INSERT INTO `sys_permission` VALUES (2100000000000000101, 'blog', '博客管理', 1, '', NULL, 0, 'Notebook', '博客管理目录', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
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
INSERT INTO `sys_permission` VALUES (2100000000000000140, 'blog:comment', '评论审核', 2, '/blog/commentaudit', 2100000000000000101, 4, 'ChatLineSquare', '博客评论审核页（GET /blog/comment/page）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000141, 'blog:comment:view', '查看评论', 3, '', 2100000000000000140, 1, '', '评论分页（GET /blog/comment/page）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000142, 'blog:comment:audit', '审核评论', 3, '', 2100000000000000140, 2, '', '审核评论（POST /blog/comment/audit）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000143, 'blog:comment:delete', '删除评论', 3, '', 2100000000000000140, 3, '', '删除评论（DELETE /blog/comment/delete）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000144, 'blog:comment:add', '发表评论', 3, '', 2100000000000000140, 4, '', '登录用户发表评论（POST /blog/comment/add）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000150, 'blog:author', '博客主申请', 2, '/blog/authorapply', 2100000000000000101, 5, 'UserFilled', '博客主申请审核页（GET /blog/author/page）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 0);
INSERT INTO `sys_permission` VALUES (2100000000000000151, 'blog:author:view', '查看申请', 3, '', 2100000000000000150, 1, '', '申请分页（GET /blog/author/page）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000152, 'blog:author:handle', '处理申请', 3, '', 2100000000000000150, 2, '', '通过/拒绝申请（POST /blog/author/handle）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2100000000000000153, 'blog:author:apply', '申请博客主', 3, '', 2100000000000000150, 3, '', '自助申请（POST /blog/author/apply）', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
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
INSERT INTO `sys_permission` VALUES (2115000000000000001, 'admin:setting', '服务器设置', 2, '/admin/globalsetting', 1001, 5, 'Tools', '全局动态设置页面', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000002, 'admin:setting:list', '查看设置', 3, '', 2115000000000000001, 1, '', '查询全局设置列表', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000003, 'admin:setting:save', '保存设置', 3, '', 2115000000000000001, 2, '', '保存（未发布）全局设置', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000004, 'admin:setting:publish', '发布设置', 3, '', 2115000000000000001, 3, '', '发布设置并广播各实例实时刷新', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000005, 'admin:setting:reset', '恢复默认', 3, '', 2115000000000000001, 4, '', '恢复默认（回到 yml/代码兜底）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000006, 'admin:oauth:list', '查看第三方登录', 3, '', 2115000000000000001, 5, '', '查询第三方登录供应商列表', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000007, 'admin:oauth:save', '保存第三方登录', 3, '', 2115000000000000001, 6, '', '新增/更新第三方登录供应商', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_permission` VALUES (2115000000000000008, 'admin:oauth:delete', '删除第三方登录', 3, '', 2115000000000000001, 7, '', '删除第三方登录供应商', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);

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
INSERT INTO `sys_role_permission` VALUES (2100000000000000205, 3, 2100000000000000153, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850690, 2, 2100000000000000101, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850691, 2, 2100000000000000102, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850692, 2, 2100000000000000103, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850693, 2, 2100000000000000104, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850694, 2, 2100000000000000105, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850695, 2, 2100000000000000106, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850696, 2, 2100000000000000120, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850697, 2, 2100000000000000121, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850698, 2, 2100000000000000122, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850699, 2, 2100000000000000123, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850700, 2, 2100000000000000124, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850701, 2, 2100000000000000130, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121588850702, 2, 2100000000000000131, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542465, 2, 2100000000000000132, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542466, 2, 2100000000000000133, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542467, 2, 2100000000000000134, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542474, 2, 2100000000000000151, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542475, 2, 2100000000000000152, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2114026121668542476, 2, 2100000000000000153, '2026-10-25 00:07:59', NULL, '2026-10-25 00:07:59', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435515420673, 1, 2100000000000000156, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435515420674, 1, 2100000000000000157, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435515420675, 1, 2100000000000000158, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435578335233, 1, 2100000000000000159, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435578335234, 1, 2100000000000000280, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435578335235, 1, 2100000000000000281, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435578335236, 1, 2100000000000000282, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435578335237, 1, 2100000000000000283, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249793, 1, 2100000000000000284, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249794, 1, 2100000000000000285, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249795, 1, 2100000000000000286, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249796, 1, 2100000000000000287, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249797, 1, 2100000000000000288, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249798, 1, 2100000000000000289, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249799, 1, 2100000000000000290, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249800, 1, 2100000000000000291, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249801, 1, 2100000000000000292, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249802, 1, 2100000000000000293, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249803, 1, 1001, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249804, 1, 1002, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249805, 1, 1003, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249806, 1, 1004, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249807, 1, 1005, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249808, 1, 1006, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249809, 1, 114214272234, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435641249810, 1, 125634234412342343, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164354, 1, 1991143600931201000, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164355, 1, 1991205164119891970, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164356, 1, 1991242409023631362, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164357, 1, 1991242808984072194, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164358, 1, 1991242997413179393, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164359, 1, 1991243204884426753, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164360, 1, 1991244603546398722, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164361, 1, 1991244778629230593, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164362, 1, 1991244964420120578, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164363, 1, 1991245154136879106, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164364, 1, 1991268323568517122, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164365, 1, 1991268510370234370, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164366, 1, 1991455913399685122, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164367, 1, 1991456923035766785, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164368, 1, 1991473796049752066, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164369, 1, 1991473884234993666, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164370, 1, 1991473999733542914, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164371, 1, 1991474490890735617, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164372, 1, 1991474490890735618, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164373, 1, 1991474490890735619, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164374, 1, 1991474490890735620, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164375, 1, 1991474490890735621, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164376, 1, 1991474490890735622, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164377, 1, 1991474490890735623, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164378, 1, 1991474490890735624, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164379, 1, 1991474490890735625, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435704164380, 1, 1991474490890735626, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078914, 1, 1991474490890735627, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078915, 1, 1991474490890735628, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078916, 1, 1991474490890735629, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078917, 1, 1991474490890735630, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078918, 1, 1991474490890735631, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078919, 1, 1991474490890735632, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078920, 1, 1991474490890735633, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078921, 1, 1991474490890735634, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078922, 1, 1991474490890735635, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078923, 1, 1991474490890735636, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078924, 1, 1991474490890735637, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078925, 1, 1991474490890735638, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078926, 1, 1991474490890735639, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078927, 1, 1991474490890735640, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078928, 1, 1991829450476220417, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078929, 1, 1993000000000001100, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078930, 1, 1993000000000001200, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078931, 1, 1993000000000001210, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078932, 1, 1993000000000001300, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078933, 1, 1993000000000001310, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078934, 1, 1993000000000001320, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078935, 1, 1993000000000001400, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078936, 1, 2011849591380627457, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078937, 1, 2011850922522693634, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078938, 1, 2011851483955449857, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078939, 1, 2011851625458683906, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078940, 1, 2011851625458683908, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078941, 1, 2011851625458683909, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078942, 1, 2011851625458683910, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078943, 1, 2011851625458683911, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078944, 1, 2011851625458683912, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435767078945, 1, 2011851625458683913, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187777, 1, 2011851625458683914, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187778, 1, 2011851625458683916, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187779, 1, 2011851625458683917, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187780, 1, 2011851625458683918, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187781, 1, 2011851625458683919, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187782, 1, 2011851625458683920, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187783, 1, 2011851625458683922, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187784, 1, 2011851625458683923, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187785, 1, 2011851625458683924, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187786, 1, 2011851625458683925, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187787, 1, 2011851625458683926, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187788, 1, 2011851625458683928, '2026-10-29 21:50:23', NULL, '2026-10-29 21:50:23', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187789, 1, 2011851625458683929, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187790, 1, 2011851625458683930, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435834187791, 1, 2011851625458683931, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879553, 1, 2011851625458683932, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879554, 1, 2011851625458683933, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879555, 1, 2011851625458683934, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879556, 1, 2011851625458683935, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879557, 1, 2011851625458683937, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879558, 1, 2011851625458683938, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879559, 1, 2011851625458683939, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879560, 1, 2011851625458683940, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879561, 1, 2011851625458683941, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879562, 1, 2011851625458683942, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879563, 1, 2011851625458683943, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879564, 1, 2011851625458683944, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879565, 1, 2011851625458683945, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879566, 1, 2011851625458683946, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879567, 1, 2011851625458683947, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879568, 1, 2011851625458683948, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879569, 1, 2011851625458683949, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435913879570, 1, 2011851625458683950, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211201, 1, 2011851625458683951, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211202, 1, 2011851625458683952, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211203, 1, 2011851625458683953, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211204, 1, 2011851625458683957, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211205, 1, 2011851625458683958, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211206, 1, 2011851625458683959, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211207, 1, 2011851625458683960, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211208, 1, 2011851625458683961, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211209, 1, 2011851625458683962, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211210, 1, 2100000000000000101, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211211, 1, 2100000000000000102, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211212, 1, 2100000000000000103, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211213, 1, 2100000000000000104, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211214, 1, 2100000000000000105, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803435964211215, 1, 2100000000000000106, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125761, 1, 2100000000000000107, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125762, 1, 2100000000000000120, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125763, 1, 2100000000000000121, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125764, 1, 2100000000000000122, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125765, 1, 2100000000000000123, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125766, 1, 2100000000000000124, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125767, 1, 2100000000000000130, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125768, 1, 2100000000000000131, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125769, 1, 2100000000000000132, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125770, 1, 2100000000000000133, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125771, 1, 2100000000000000134, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125772, 1, 2100000000000000151, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125773, 1, 2100000000000000152, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125774, 1, 2100000000000000153, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125775, 1, 2100000000000000160, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125776, 1, 2100000000000000161, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125777, 1, 2100000000000000162, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125778, 1, 2100000000000000170, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125779, 1, 2100000000000000171, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125780, 1, 2100000000000000230, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125781, 1, 2100000000000000231, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125782, 1, 2100000000000000232, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125783, 1, 2100000000000000233, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125784, 1, 2100000000000000234, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125785, 1, 2100000000000000235, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436027125786, 1, 2100000000000000250, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040321, 1, 2100000000000000251, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040322, 1, 2100000000000000252, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040323, 1, 2100000000000000254, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040324, 1, 2100000000000000253, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040325, 1, 2115000000000000001, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040326, 1, 2115000000000000002, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040327, 1, 2115000000000000003, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040328, 1, 2115000000000000004, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040329, 1, 2115000000000000005, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040330, 1, 2115000000000000006, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040331, 1, 2115000000000000007, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2115803436090040332, 1, 2115000000000000008, '2026-10-29 21:50:24', NULL, '2026-10-29 21:50:24', NULL, 1);

-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `setting_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键，如 captcha.enabled',
  `setting_value` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置值；NULL=未覆盖（走 yml/代码兜底）；SECRET 类型密文存储',
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
INSERT INTO `sys_setting` VALUES (2115000000000000101, 'captcha.enabled', 'false', 'BOOL', 'captcha', '登录验证码开关（覆盖 yml cloudwaer.captcha.enabled）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000102, 'captcha.expire-seconds', '60', 'INT', 'captcha', '验证码有效期（秒），覆盖 cloudwaer.captcha.expire-seconds', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000103, 'jwt.expiration', NULL, 'LONG', 'jwt', 'Token 有效期（毫秒），覆盖 jwt.expiration', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000104, 'jwt.allow-multiple-login', 'true', 'BOOL', 'jwt', '是否允许多处登录，覆盖 jwt.allow-multiple-login', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);
INSERT INTO `sys_setting` VALUES (2115000000000000105, 'auth.register.enabled', NULL, 'BOOL', 'auth', '匿名注册开关（默认开启）', '2026-10-29 20:46:00', 'system', '2026-10-29 20:46:00', 'system', 1);

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
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$QglwBinM.wSdYblvNtTIveMDfltN./vRjPczrA9FnZNQwU1SnvwU6', '管理员', 'chen_a_nan@qq.com', '', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', '2025-11-18 18:45:43', NULL, '2026-01-12 07:32:35', NULL, 1);
INSERT INTO `sys_user` VALUES (1992208936665325569, 'test', '$2a$10$B6/HWEMm37XBm3vAJDpIduGCMam.iF8LspVQg21weWSTghugFz.he', '这是测试账号', 'wx-gpt-plus@qq.com', '', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', '2025-11-22 20:29:59', NULL, '2026-01-12 07:32:39', NULL, 1);

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

SET FOREIGN_KEY_CHECKS = 1;
