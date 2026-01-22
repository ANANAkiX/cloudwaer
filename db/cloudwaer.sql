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

 Date: 23/01/2026 02:14:01
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
INSERT INTO `sys_dict` VALUES (2013029000000000002, 'http_method', 'HTTP方法', 0, 'HTTP方法字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict` VALUES (2013029000000000003, 'flowable_priority', '任务优先级', 0, '流程任务优先级字典', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);


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

-- permission_type
INSERT INTO `sys_dict_item` VALUES (2013029000000000101, 2013029000000000001, '1', '1', '菜单', 0, '权限类型-菜单', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000102, 2013029000000000001, '2', '2', '页面', 1, '权限类型-页面', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000103, 2013029000000000001, '3', '3', '操作', 2, '权限类型-操作', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);

-- http_method
INSERT INTO `sys_dict_item` VALUES (2013029000000000201, 2013029000000000002, 'GET', 'GET', 'GET', 0, 'HTTP方法-GET', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000202, 2013029000000000002, 'POST', 'POST', 'POST', 1, 'HTTP方法-POST', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000203, 2013029000000000002, 'PUT', 'PUT', 'PUT', 2, 'HTTP方法-PUT', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000204, 2013029000000000002, 'DELETE', 'DELETE', 'DELETE', 3, 'HTTP方法-DELETE', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);

-- flowable_priority
INSERT INTO `sys_dict_item` VALUES (2013029000000000301, 2013029000000000003, 'low', 'low', '低级', 0, '任务优先级-低级', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000302, 2013029000000000003, 'normal', 'normal', '正常', 1, '任务优先级-正常', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000303, 2013029000000000003, 'urgent', 'urgent', '紧急', 2, '任务优先级-紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);
INSERT INTO `sys_dict_item` VALUES (2013029000000000304, 2013029000000000003, 'very_urgent', 'very_urgent', '非常紧急', 3, '任务优先级-非常紧急', '2026-01-23 00:00:00', 'system', '2026-01-23 00:00:00', 'system', 1);


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
INSERT INTO `sys_gateway_route` VALUES (1991546479613448194, 'cloudwaer-codegen-serve', 'lb://cloudwaer-codegen-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/codegen/**\"}}]', NULL, 0, '代码生成服务的网关转发配置', '2025-11-21 00:37:37', NULL, '2025-11-21 00:37:37', NULL, 1);
INSERT INTO `sys_gateway_route` VALUES (1992205324727853058, 'ws-cloudwaer-im-service', 'lb:ws://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/ws/**\"}}]', NULL, 0, '仅让 WebSocket 握手走这条，避免影响 REST 调用\n', '2025-11-22 20:15:37', NULL, '2026-01-06 05:56:00', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (1992239401883295746, 'rest-cloudwaer-im-service', 'lb://cloudwaer-im-service', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/im/**\"}}]', NULL, 1, '处理 /im/** 的 HTTP 接口', '2025-11-22 22:31:02', NULL, '2026-01-06 05:56:13', NULL, 0);
INSERT INTO `sys_gateway_route` VALUES (2011458231829864450, 'cloudwaer-flowable-serve', 'lb://cloudwaer-flowable-serve', '[{\"name\":\"Path\",\"args\":{\"pattern\":\"/flowable/**\"}}]', '[{\"name\":\"StripPrefix\",\"args\":{\"parts\":\"1\"}}]', 0, 'cloudwaer-flowable-serve 的基础网关转发', '2026-01-14 23:19:48', NULL, '2026-01-15 01:06:01', NULL, 1);

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
  `api_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API请求地址',
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求类型（GET, POST, PUT, DELETE）',
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
  INDEX `idx_permission_type`(`permission_type` ASC) USING BTREE,
  INDEX `idx_http_method`(`http_method` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1001, 'admin', '后台管理', 1, '', NULL, 1, 'Setting', '后台管理菜单', '', '', '2025-11-19 21:14:55', NULL, '2025-11-19 21:14:55', NULL, 1);
INSERT INTO `sys_permission` VALUES (1002, 'admin:permission', '菜单权限', 2, '/admin/permission', 1001, 1, 'Key', '权限管理页面', '', '', '2025-11-19 21:15:04', NULL, '2025-11-19 21:15:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1003, 'admin:permission:view', '查看权限', 3, '', 1002, 1, 'Search', '获取权限树 查看权限', '/admin/permission/tree', 'GET', '2025-11-19 21:15:07', NULL, '2025-11-19 21:15:07', NULL, 1);
INSERT INTO `sys_permission` VALUES (1004, 'admin:permission:add', '新增权限', 3, '', 1002, 2, 'FolderAdd', '权限页面-新增权限', '/admin/permission/save', 'POST', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1005, 'admin:permission:edit', '编辑权限', 3, '', 1002, 3, 'EditPen', '权限页面-编辑权限', '/admin/permission/update', 'PUT', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1006, 'admin:permission:delete', '删除权限', 3, '', 1002, 4, 'Delete', '权限页面-删除权限', '/admin/permission/delete', 'DELETE', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (114214272234, 'codegen:generator:view', '代码生成页面', 2, '/codegen/CodeGenerator', 125634234412342343, 1, '', '代码生成管理页面', '', '', '2025-11-21 01:31:10', NULL, '2025-11-21 01:31:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (125634234412342343, 'codegen', '代码生成', 1, NULL, NULL, 100, 'Document', '代码生成管理菜单', NULL, NULL, '2025-11-21 01:29:10', NULL, '2025-11-21 01:29:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991143600931201000, 'admin:role', '角色管理', 2, '/admin/role', 1001, 3, 'Coin', '角色管理页面', '', '', '2025-11-19 21:56:43', NULL, '2025-11-19 21:56:43', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991205164119891970, 'admin:user', '用户管理', 2, '/admin/user', 1001, 2, 'Avatar', '用户管理页面', '', '', '2025-11-20 02:01:21', NULL, '2025-11-20 02:01:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242409023631362, 'admin:user:add', '添加用户', 3, '', 1991205164119891970, 0, 'FolderAdd', '新增用户', '/admin/user/save', 'POST', '2025-11-20 04:29:20', NULL, '2025-11-20 04:29:20', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242808984072194, 'admin:user:delete', '删除用户', 3, '', 1991205164119891970, 0, 'Delete', '删除用户', '/admin/user/delete', 'DELETE', '2025-11-20 04:30:56', NULL, '2025-11-20 04:30:56', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991242997413179393, 'admin:user:edit', '修改用户', 3, '', 1991205164119891970, 0, 'EditPen', '更新用户', '/admin/user/update', 'PUT', '2025-11-20 04:31:41', NULL, '2025-11-20 04:31:41', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991243204884426753, 'admin:user:view', '查询用户', 3, '', 1991205164119891970, 0, 'Search', '分页查询用户列表', '/admin/user/page', 'GET', '2025-11-20 04:32:30', NULL, '2025-11-20 04:32:30', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244603546398722, 'admin:role:add', '新增角色', 3, '', 1991143600931201000, 0, '', '新增角色', '/admin/role/save', 'POST', '2025-11-20 04:38:04', NULL, '2025-11-20 04:38:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244778629230593, 'admin:role:delete', '删除角色', 3, '', 1991143600931201000, 0, '', '删除角色', '/admin/role/delete', 'DELETE', '2025-11-20 04:38:45', NULL, '2025-11-20 04:38:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991244964420120578, 'admin:role:edit', '修改角色', 3, '', 1991143600931201000, 0, '', '更新角色', '/admin/role/update', 'PUT', '2025-11-20 04:39:30', NULL, '2025-11-20 04:39:30', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991245154136879106, 'admin:role:view', '查看角色', 3, '', 1991143600931201000, 0, '', '分页查询角色列表', '/admin/role/page', 'GET', '2025-11-20 04:40:15', NULL, '2025-11-20 04:40:15', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991268323568517122, 'admin:user:assign-role', '分配角色给用户', 3, '', 1991205164119891970, 0, '', '分配角色给用户', '/admin/user/assign-roles', 'POST', '2025-11-20 06:12:19', NULL, '2025-11-20 06:12:19', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991268510370234370, 'admin:role:assign-permission', '分配权限给角色', 3, '', 1991143600931201000, 0, '', '分配权限给角色', '/admin/role/assign-permissions', 'POST', '2025-11-20 06:13:04', NULL, '2025-11-20 06:13:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991455913399685122, 'admin:gateway', '动态网关', 1, '', NULL, 1, 'Connection', '', '', '', '2025-11-20 18:37:44', NULL, '2025-11-20 18:37:44', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991456923035766785, 'admin:gateway:view', '网关列表', 2, '/gateway', 1991455913399685122, 0, 'DataAnalysis', '查看网关列表', '', '', '2025-11-20 18:41:45', NULL, '2025-11-20 18:41:45', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473796049752066, 'admin:gateway:add', '新增网关', 3, '', 1991456923035766785, 0, '', '新增网关路由', '/admin/gateway-route/save', 'POST', '2025-11-20 19:48:47', NULL, '2025-11-20 19:48:47', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473884234993666, 'admin:gateway:delete', '删除网关路由', 3, '', 1991456923035766785, 0, '', '删除网关路由', '/admin/gateway-route/delete', 'DELETE', '2025-11-20 19:49:08', NULL, '2025-11-20 19:49:08', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991473999733542914, 'admin:gateway:edit', '更新网关路由', 3, '', 1991456923035766785, 0, '', '', '/admin/gateway-route/update', 'PUT', '2025-11-20 19:49:36', NULL, '2025-11-20 19:49:36', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735617, 'admin:gateway:refresh', '刷新网关路由', 3, '', 1991456923035766785, 0, '', '刷新网关路由', '/admin/gateway-route/refresh', 'POST', '2025-11-20 19:51:33', NULL, '2025-11-20 19:51:33', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735618, 'codegen:connection:list', '查询数据库连接列表', 3, '', 1991829450476220417, 10, '', '查询数据库连接列表权限', '/codegen/database-connection/list', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735619, 'codegen:connection:page', '分页查询数据库连接', 3, '', 1991829450476220417, 11, '', '分页查询数据库连接权限', '/codegen/database-connection/page', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735620, 'codegen:connection:detail', '查询数据库连接详情', 3, '', 1991829450476220417, 12, '', '查询数据库连接详情权限', '/codegen/database-connection/detail', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735621, 'codegen:connection:test', '测试数据库连接', 3, '', 1991829450476220417, 13, '', '测试数据库连接权限', '/codegen/database-connection/test', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735622, 'codegen:connection:add', '新增数据库连接', 3, '', 1991829450476220417, 14, '', '新增数据库连接权限', '/codegen/database-connection/save', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735623, 'codegen:connection:edit', '更新数据库连接', 3, '', 1991829450476220417, 15, '', '更新数据库连接权限', '/codegen/database-connection/update', 'PUT', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735624, 'codegen:connection:delete', '删除数据库连接', 3, '', 1991829450476220417, 16, '', '删除数据库连接权限', '/codegen/database-connection/delete', 'DELETE', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735625, 'codegen:connection:toggle', '启用/禁用数据库连接', 3, '', 1991829450476220417, 17, '', '启用/禁用数据库连接权限', '/codegen/database-connection/toggle-enabled', 'PUT', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735626, 'codegen:metadata:tables', '查询数据库表列表', 3, NULL, 114214272234, 20, NULL, '查询数据库表列表权限', '/codegen/metadata/tables', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735627, 'codegen:metadata:table', '查询表结构元数据', 3, NULL, 114214272234, 21, NULL, '查询表结构元数据权限', '/codegen/metadata/table', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735628, 'codegen:metadata:tables:metadata', '批量查询表结构元数据', 3, NULL, 114214272234, 22, NULL, '批量查询表结构元数据权限', '/codegen/metadata/tables/metadata', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735629, 'codegen:form:generate', '生成表单配置', 3, NULL, 114214272234, 30, NULL, '生成表单配置权限', '/codegen/form/generate', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735630, 'codegen:form:generate:fields', '生成表单字段配置', 3, NULL, 114214272234, 31, NULL, '生成表单字段配置权限', '/codegen/form/generate-fields', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735631, 'codegen:form:save', '保存表单配置', 3, NULL, 114214272234, 32, NULL, '保存表单配置权限', '/codegen/form/save', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735632, 'codegen:form:config', '查询表单配置', 3, NULL, 114214272234, 33, NULL, '查询表单配置权限', '/codegen/form/config', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735633, 'codegen:form:config:id', '根据ID查询表单配置', 3, NULL, 114214272234, 34, NULL, '根据ID查询表单配置权限', '/codegen/form/config/{id}', 'GET', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735634, 'codegen:form:fields', '更新表单字段配置', 3, NULL, 114214272234, 35, NULL, '更新表单字段配置权限', '/codegen/form/fields', 'PUT', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735635, 'codegen:form:layout', '调整表单布局', 3, NULL, 114214272234, 36, NULL, '调整表单布局权限', '/codegen/form/layout', 'PUT', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735636, 'codegen:generator:backend', '生成后端代码', 3, NULL, 114214272234, 40, NULL, '生成后端代码权限', '/codegen/generator/backend', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735637, 'codegen:generator:frontend', '生成前端代码', 3, NULL, 114214272234, 41, NULL, '生成前端代码权限', '/codegen/generator/frontend', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735638, 'codegen:generator:permission', '生成权限SQL', 3, NULL, 114214272234, 42, NULL, '生成权限SQL权限', '/codegen/generator/permission', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991474490890735639, 'codegen:generator:all', '生成所有代码', 3, NULL, 114214272234, 43, NULL, '生成所有代码权限（后端、前端、权限SQL）', '/codegen/generator/all', 'POST', '2025-11-21 01:32:32', NULL, '2025-11-21 01:32:32', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991829450476220417, 'codegen:connection:view', '数据源管理', 2, '/codegen/DatabaseConnection', 125634234412342343, 0, '', '', '', '', '2025-11-21 19:22:02', NULL, '2025-11-21 19:22:02', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001100, 'admin:dict:view', '字典管理', 2, '/admin/dict', 1001, 4, 'Notebook', '访问字典管理页面', '', '', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001200, 'admin:dict:page', '分页查询', 3, '', 1993000000000001100, 10, '', '分页查询字典', '/admin/dict/page', 'GET', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001210, 'admin:dict:detail', '查询详情', 3, '', 1993000000000001100, 11, '', '根据ID查询字典详情', '/admin/dict/detail', 'GET', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001300, 'admin:dict:add', '新增', 3, '', 1993000000000001100, 20, 'FolderAdd', '新增字典项', '/admin/dict/save', 'POST', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001310, 'admin:dict:edit', '更新', 3, '', 1993000000000001100, 21, 'EditPen', '更新字典项', '/admin/dict/update', 'PUT', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001320, 'admin:dict:delete', '删除', 3, '', 1993000000000001100, 22, 'Delete', '删除字典项', '/admin/dict/delete', 'DELETE', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001400, 'admin:dict:refresh', '刷新缓存', 3, '', 1993000000000001100, 30, 'Refresh', '刷新字典缓存', '/admin/dict/refresh', 'POST', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011849591380627457, 'flowable', '流程管理', 1, '', NULL, 0, 'Cpu', 'flowable流程页面', '', '', '2026-01-16 01:14:56', NULL, '2026-01-16 01:14:56', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011850922522693634, 'flowable:model', '流程模型管理', 2, '/flowable/modelmanagement', 2011849591380627457, 1, 'Document', '流程模型的新增、编辑、发布和管理', '', '', '2026-01-16 01:20:13', NULL, '2026-01-16 01:20:13', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851483955449857, 'flowable:application:view', '流程申请', 2, '/flowable/processapplication', 2011849591380627457, 4, 'ChatLineRound', '用户申请和启动新的流程实例', '', '', '2026-01-16 01:22:27', NULL, '2026-01-16 01:22:27', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683906, 'flowable:monitor:view', '流程监控', 2, '/flowable/processmonitor', 2011849591380627457, 3, 'Crop', '监控和管理运行中的流程实例', '', '', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683908, 'flowable:model:list', '查看模型列表', 3, NULL, 2011850922522693634, 2, NULL, '查看流程模型列表', '/model/list', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683909, 'flowable:model:save', '保存模型', 3, NULL, 2011850922522693634, 3, NULL, '新增或编辑流程模型', '/model/save', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683910, 'flowable:model:delete', '删除模型', 3, NULL, 2011850922522693634, 4, NULL, '删除流程模型', '/model/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683911, 'flowable:model:publish', '发布模型', 3, NULL, 2011850922522693634, 5, NULL, '发布流程模型', '/model/publish', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683912, 'flowable:model:copy', '复制模型', 3, NULL, 2011850922522693634, 6, NULL, '复制流程模型', '/model/copy', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683913, 'flowable:model:detail', '查看模型详情', 3, NULL, 2011850922522693634, 7, NULL, '查看流程模型详情', '/model/detail', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683914, 'flowable:model:rollback', '回滚模型', 3, NULL, 2011850922522693634, 8, NULL, '回滚到指定版本', '/model/rollback', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683916, 'flowable:designer:save', '保存流程设计', 3, NULL, 2011850922522693634, 2, NULL, '保存BPMN流程设计', '/designer/save', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683917, 'flowable:designer:import', '导入BPMN', 3, NULL, 2011850922522693634, 3, NULL, '导入BPMN文件', '/designer/import', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683918, 'flowable:designer:export', '导出BPMN', 3, NULL, 2011850922522693634, 4, NULL, '导出BPMN文件', '/designer/export', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683919, 'flowable:designer:preview', '预览流程', 3, NULL, 2011850922522693634, 5, NULL, '预览流程图', '/designer/preview', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683920, 'flowable:designer:validate', '验证BPMN', 3, NULL, 2011850922522693634, 6, NULL, '验证BPMN文件格式', '/designer/validate', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683921, 'flowable:application:route', '流程申请页面', 2, '/flowable/process-application', 2011851483955449857, 1, NULL, '流程申请页面路由权限', '', '', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:14', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683922, 'flowable:process:start', '启动流程', 3, NULL, 2011851483955449857, 2, NULL, '启动新的流程实例', '/process/start', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683923, 'flowable:process:definitions', '查看流程定义', 3, NULL, 2011851483955449857, 3, NULL, '查看可申请的流程定义列表', '/process/definitions', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683924, 'flowable:process:detail', '查看流程详情', 3, NULL, 2011851483955449857, 4, NULL, '查看流程实例详情', '/process/detail', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683925, 'flowable:process:variables', '查看流程变量', 3, NULL, 2011851483955449857, 5, NULL, '查看流程实例变量', '/process/variables', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683926, 'flowable:process:history', '查看流程历史', 3, NULL, 2011851483955449857, 6, NULL, '查看流程操作历史', '/process/history', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683927, 'flowable:monitor:route', '流程监控页面', 2, '/flowable/process-monitor', 2011851625458683906, 1, NULL, '流程监控页面路由权限', '', '', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:18', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683928, 'flowable:monitor:instances', '查看流程实例', 3, NULL, 2011851625458683906, 2, NULL, '查看所有流程实例', '/monitor/instances', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683929, 'flowable:monitor:suspend', '挂起流程', 3, NULL, 2011851625458683906, 3, NULL, '挂起正在运行的流程', '/monitor/suspend', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683930, 'flowable:monitor:activate', '激活流程', 3, NULL, 2011851625458683906, 4, NULL, '激活已挂起的流程', '/monitor/activate', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683931, 'flowable:monitor:terminate', '终止流程', 3, NULL, 2011851625458683906, 5, NULL, '终止正在运行的流程', '/monitor/terminate', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683932, 'flowable:monitor:delete', '删除流程实例', 3, NULL, 2011851625458683906, 6, NULL, '删除流程实例', '/monitor/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683933, 'flowable:monitor:diagram', '查看流程图', 3, NULL, 2011851625458683906, 7, NULL, '查看流程实例图', '/monitor/diagram', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683934, 'flowable:monitor:batch', '批量操作', 3, NULL, 2011851625458683906, 8, NULL, '批量挂起/激活/终止流程', '/monitor/batch', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683935, 'flowable:task:view', '任务管理', 2, '/flowable/taskmanagement', 2011849591380627457, 5, 'Check', '任务管理菜单', '', '', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683936, 'flowable:task:route', '任务管理页面', 2, '/flowable/task-management', 2011851625458683935, 1, NULL, '任务管理页面路由权限', '', '', '2026-01-16 01:23:01', NULL, '2026-01-16 01:38:24', NULL, 0);
INSERT INTO `sys_permission` VALUES (2011851625458683937, 'flowable:task:todo', '查看待办任务', 3, NULL, 2011851625458683935, 2, NULL, '查看当前用户的待办任务', '/task/todo', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683938, 'flowable:task:done', '查看已办任务', 3, NULL, 2011851625458683935, 3, NULL, '查看当前用户的已办任务', '/task/done', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683939, 'flowable:task:claim', '认领任务', 3, NULL, 2011851625458683935, 4, NULL, '认领未分配的任务', '/task/claim', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683940, 'flowable:task:complete', '完成任务', 3, NULL, 2011851625458683935, 5, NULL, '完成当前任务', '/task/complete', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683941, 'flowable:task:detail', '查看任务详情', 3, NULL, 2011851625458683935, 6, NULL, '查看任务详情', '/task/detail', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683942, 'flowable:task:delete', '删除任务', 3, NULL, 2011851625458683935, 7, NULL, '删除任务', '/task/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683943, 'flowable:task:batch', '批量操作', 3, NULL, 2011851625458683935, 8, NULL, '批量认领或完成任务', '/task/batch', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683944, 'flowable:bpmn:deploy', '部署BPMN', 3, NULL, 2011850922522693634, 7, NULL, '部署BPMN流程定义', '/bpmn/deploy', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683945, 'flowable:bpmn:update', '更新BPMN', 3, NULL, 2011850922522693634, 8, NULL, '更新BPMN流程定义', '/bpmn/update', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683946, 'flowable:bpmn:delete', '删除部署', 3, NULL, 2011850922522693634, 9, NULL, '删除BPMN部署', '/bpmn/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683947, 'flowable:bpmn:extract', '提取节点', 3, NULL, 2011850922522693634, 10, NULL, '从BPMN提取节点信息', '/bpmn/extract-nodes', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683948, 'flowable:node:action:list', '查看节点动作', 3, NULL, 2011850922522693634, 9, NULL, '查看节点动作列表', '/node-action/list', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683949, 'flowable:node:action:save', '保存节点动作', 3, NULL, 2011850922522693634, 10, NULL, '保存节点动作配置', '/node-action/save', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683950, 'flowable:node:action:batch', '批量保存节点动作', 3, NULL, 2011850922522693634, 11, NULL, '批量保存节点动作', '/node-action/batch-save', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683951, 'flowable:node:action:delete', '删除节点动作', 3, NULL, 2011850922522693634, 12, NULL, '删除节点动作', '/node-action/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683952, 'flowable:node:action:status', '更新节点动作状态', 3, NULL, 2011850922522693634, 13, NULL, '启用或禁用节点动作', '/node-action/status', 'PUT', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683953, 'flowable:node:action:config', '获取节点动作配置', 3, NULL, 2011850922522693634, 14, NULL, '获取节点动作配置', '/node-action/config', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683954, 'flowable:file:upload', '文件上传', 3, '', 2013024515860238337, 1, '', '上传附件文件', '/file/upload', 'POST', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683955, 'flowable:file:download', '文件下载', 3, '', 2013024515860238337, 2, '', '下载附件文件', '/file/download', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683956, 'flowable:file:delete', '删除文件', 3, '', 2013024515860238337, 3, '', '删除附件文件', '/file/delete', 'DELETE', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683957, 'flowable:statistics:dashboard', '查看统计面板', 3, NULL, 2011849591380627457, 6, NULL, '查看Flowable统计面板', '/statistics/dashboard', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683958, 'flowable:statistics:report', '生成报表', 3, NULL, 2011849591380627457, 7, NULL, '生成流程统计报表', '/statistics/report', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683959, 'flowable:statistics:export', '导出报表', 3, NULL, 2011849591380627457, 8, NULL, '导出统计报表', '/statistics/export', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683960, 'flowable:config:global', '全局配置', 3, NULL, 2011849591380627457, 9, NULL, 'Flowable全局配置管理', '/config/global', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683961, 'flowable:config:engine', '引擎配置', 3, NULL, 2011849591380627457, 10, NULL, 'Flowable引擎配置', '/config/engine', 'GET', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2011851625458683962, 'flowable:config:update', '更新配置', 3, NULL, 2011849591380627457, 11, NULL, '更新Flowable配置', '/config/update', 'PUT', '2026-01-16 01:23:01', NULL, '2026-01-16 01:23:01', NULL, 1);
INSERT INTO `sys_permission` VALUES (2013024515860238337, 'other', '其他权限集合', 1, '', NULL, 999, '', '', '', '', '2026-01-19 07:03:39', NULL, '2026-01-19 07:03:39', NULL, 1);

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
INSERT INTO `sys_role` VALUES (1991511330100232193, 'TEST', '测试', '', '2025-11-20 22:17:56', NULL, '2025-11-20 22:18:04', NULL, 0);

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
INSERT INTO `sys_role_permission` VALUES (2012469302433832962, 2, 1001, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610177, 2, 1002, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610178, 2, 1003, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610179, 2, 1004, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610180, 2, 1005, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610181, 2, 1006, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610182, 2, 114214272234, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610183, 2, 125634234412342343, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610184, 2, 1991143600931201000, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610185, 2, 1991205164119891970, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302450610186, 2, 1991242409023631362, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719041, 2, 1991242808984072194, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719042, 2, 1991242997413179393, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719043, 2, 1991243204884426753, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719044, 2, 1991244603546398722, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719045, 2, 1991244778629230593, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719046, 2, 1991244964420120578, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719047, 2, 1991245154136879106, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719048, 2, 1991268323568517122, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719049, 2, 1991268510370234370, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719050, 2, 1991455913399685122, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719051, 2, 1991456923035766785, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719052, 2, 1991473796049752066, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302517719053, 2, 1991473884234993666, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827906, 2, 1991473999733542914, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827907, 2, 1991474490890735617, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827908, 2, 1991474490890735618, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827909, 2, 1991474490890735619, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827910, 2, 1991474490890735620, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827911, 2, 1991474490890735621, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827912, 2, 1991474490890735622, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827913, 2, 1991474490890735623, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827914, 2, 1991474490890735624, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827915, 2, 1991474490890735625, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827916, 2, 1991474490890735626, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827917, 2, 1991474490890735627, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827918, 2, 1991474490890735628, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827919, 2, 1991474490890735629, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827920, 2, 1991474490890735630, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302584827921, 2, 1991474490890735631, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936770, 2, 1991474490890735632, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936771, 2, 1991474490890735633, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936772, 2, 1991474490890735634, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936773, 2, 1991474490890735635, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936774, 2, 1991474490890735636, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936775, 2, 1991474490890735637, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936776, 2, 1991474490890735638, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936777, 2, 1991474490890735639, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936778, 2, 1991829450476220417, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936779, 2, 1993000000000001100, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936780, 2, 1993000000000001200, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936781, 2, 1993000000000001210, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936782, 2, 1993000000000001300, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936783, 2, 1993000000000001310, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936784, 2, 1993000000000001320, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302651936785, 2, 1993000000000001400, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045633, 2, 2011849591380627457, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045635, 2, 2011851625458683916, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045636, 2, 2011851625458683917, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045637, 2, 2011851625458683918, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045638, 2, 2011851625458683919, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045639, 2, 2011851625458683920, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045640, 2, 2011851625458683944, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045641, 2, 2011851625458683945, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045642, 2, 2011851625458683946, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045643, 2, 2011851625458683947, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045644, 2, 2011850922522693634, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045645, 2, 2011851625458683908, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045646, 2, 2011851625458683909, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302719045647, 2, 2011851625458683910, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154497, 2, 2011851625458683911, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154498, 2, 2011851625458683912, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154499, 2, 2011851625458683913, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154500, 2, 2011851625458683914, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154501, 2, 2011851625458683948, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154502, 2, 2011851625458683949, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154503, 2, 2011851625458683950, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154504, 2, 2011851625458683951, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154505, 2, 2011851625458683952, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154506, 2, 2011851625458683953, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154507, 2, 2011851625458683906, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154508, 2, 2011851625458683928, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154509, 2, 2011851625458683929, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154510, 2, 2011851625458683930, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154511, 2, 2011851625458683931, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302786154512, 2, 2011851625458683932, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069057, 2, 2011851625458683933, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069058, 2, 2011851625458683934, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069059, 2, 2011851483955449857, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069060, 2, 2011851625458683922, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069061, 2, 2011851625458683923, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069062, 2, 2011851625458683924, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069063, 2, 2011851625458683925, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069064, 2, 2011851625458683926, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069065, 2, 2011851625458683935, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069066, 2, 2011851625458683937, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069067, 2, 2011851625458683938, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069068, 2, 2011851625458683939, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069069, 2, 2011851625458683940, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069070, 2, 2011851625458683941, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069071, 2, 2011851625458683942, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069072, 2, 2011851625458683943, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069073, 2, 2011851625458683957, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069074, 2, 2011851625458683958, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302849069075, 2, 2011851625458683959, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372226, 2, 2011851625458683960, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372227, 2, 2011851625458683961, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372228, 2, 2011851625458683962, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372229, 2, 2011851625458683954, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372230, 2, 2011851625458683955, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2012469302920372231, 2, 2011851625458683956, '2026-01-17 18:17:26', NULL, '2026-01-17 18:17:26', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956613152770, 1, 1001, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956613152771, 1, 1002, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956613152772, 1, 1003, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956613152773, 1, 1004, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067330, 1, 1005, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067331, 1, 1006, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067332, 1, 114214272234, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067333, 1, 125634234412342343, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067334, 1, 1991143600931201000, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067335, 1, 1991205164119891970, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067336, 1, 1991242409023631362, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067337, 1, 1991242808984072194, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067338, 1, 1991242997413179393, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067339, 1, 1991243204884426753, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067340, 1, 1991244603546398722, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067341, 1, 1991244778629230593, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067342, 1, 1991244964420120578, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067343, 1, 1991245154136879106, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067344, 1, 1991268323568517122, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067345, 1, 1991268510370234370, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067346, 1, 1991455913399685122, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067347, 1, 1991456923035766785, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067348, 1, 1991473796049752066, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067349, 1, 1991473884234993666, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067350, 1, 1991473999733542914, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067351, 1, 1991474490890735617, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067352, 1, 1991474490890735618, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067353, 1, 1991474490890735619, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956676067354, 1, 1991474490890735620, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176193, 1, 1991474490890735621, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176194, 1, 1991474490890735622, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176195, 1, 1991474490890735623, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176196, 1, 1991474490890735624, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176197, 1, 1991474490890735625, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176198, 1, 1991474490890735626, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176199, 1, 1991474490890735627, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176200, 1, 1991474490890735628, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176201, 1, 1991474490890735629, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176202, 1, 1991474490890735630, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176203, 1, 1991474490890735631, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176204, 1, 1991474490890735632, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176205, 1, 1991474490890735633, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176206, 1, 1991474490890735634, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176207, 1, 1991474490890735635, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176208, 1, 1991474490890735636, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176209, 1, 1991474490890735637, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176210, 1, 1991474490890735638, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176211, 1, 1991474490890735639, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176212, 1, 1991829450476220417, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176213, 1, 1993000000000001100, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176214, 1, 1993000000000001200, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176215, 1, 1993000000000001210, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176216, 1, 1993000000000001300, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956743176217, 1, 1993000000000001310, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090753, 1, 1993000000000001320, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090754, 1, 1993000000000001400, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090755, 1, 2009000000000002101, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090756, 1, 2009000000000002102, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090757, 1, 2009000000000002103, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090758, 1, 2009000000000002104, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090759, 1, 2009000000000002105, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090760, 1, 2009000000000002106, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090761, 1, 2009000000000002107, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090762, 1, 2009000000000002108, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090763, 1, 2009000000000002201, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090764, 1, 2009000000000002202, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090765, 1, 2009000000000002203, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090766, 1, 2009000000000002204, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090767, 1, 2009000000000002301, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090768, 1, 2009000000000002302, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090769, 1, 2009000000000002303, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090770, 1, 2009000000000002304, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090771, 1, 2009000000000002305, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090772, 1, 2009000000000002306, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090773, 1, 2011435446835392513, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090774, 1, 2011452962718687234, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090775, 1, 2011477307084034049, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090776, 1, 2011477438734848002, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090777, 1, 2011477560004759554, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956806090778, 1, 2011849591380627457, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199617, 1, 2011850922522693634, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199618, 1, 2011851483955449857, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199619, 1, 2011851625458683906, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199620, 1, 2011851625458683908, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199621, 1, 2011851625458683909, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199622, 1, 2011851625458683910, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199623, 1, 2011851625458683911, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199624, 1, 2011851625458683912, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199625, 1, 2011851625458683913, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199626, 1, 2011851625458683914, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199627, 1, 2011851625458683916, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199628, 1, 2011851625458683917, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199629, 1, 2011851625458683918, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199630, 1, 2011851625458683919, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199631, 1, 2011851625458683920, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199632, 1, 2011851625458683922, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199633, 1, 2011851625458683923, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199634, 1, 2011851625458683924, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199635, 1, 2011851625458683925, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199636, 1, 2011851625458683926, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199637, 1, 2011851625458683928, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199638, 1, 2011851625458683929, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199639, 1, 2011851625458683930, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956873199640, 1, 2011851625458683931, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308482, 1, 2011851625458683932, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308483, 1, 2011851625458683933, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308484, 1, 2011851625458683934, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308485, 1, 2011851625458683935, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308486, 1, 2011851625458683937, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308487, 1, 2011851625458683938, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308488, 1, 2011851625458683939, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308489, 1, 2011851625458683940, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308490, 1, 2011851625458683941, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308491, 1, 2011851625458683942, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308492, 1, 2011851625458683943, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308493, 1, 2011851625458683944, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308494, 1, 2011851625458683945, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308495, 1, 2011851625458683946, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308496, 1, 2011851625458683947, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308497, 1, 2011851625458683948, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308498, 1, 2011851625458683949, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308499, 1, 2011851625458683950, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308500, 1, 2011851625458683951, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308501, 1, 2011851625458683952, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308502, 1, 2011851625458683953, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308503, 1, 2011851625458683957, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028956940308504, 1, 2011851625458683958, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028957003223042, 1, 2011851625458683959, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028957003223043, 1, 2011851625458683960, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028957003223044, 1, 2011851625458683961, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2013028957003223045, 1, 2011851625458683962, '2026-01-19 07:21:18', NULL, '2026-01-19 07:21:18', NULL, 1);

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
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$QglwBinM.wSdYblvNtTIveMDfltN./vRjPczrA9FnZNQwU1SnvwU6', '管理员', 'chen_a_nan@qq.com', '', NULL, '2025-11-18 18:45:43', NULL, '2026-01-12 07:32:35', NULL, 1);
INSERT INTO `sys_user` VALUES (1992208936665325569, 'test', '$2a$10$B6/HWEMm37XBm3vAJDpIduGCMam.iF8LspVQg21weWSTghugFz.he', '测试', 'wx-gpt-plus@qq.com', '', NULL, '2025-11-22 20:29:59', NULL, '2026-01-12 07:32:39', NULL, 1);

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
INSERT INTO `sys_user_role` VALUES (2008296925178875906, 1992208936665325569, 2, '2026-01-06 05:57:54', NULL, '2026-01-06 05:57:54', NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;
