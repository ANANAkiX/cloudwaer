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

 Date: 08/01/2026 23:29:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
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
-- Table structure for sys_dict_item
-- ----------------------------
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
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1992000000000001000, 'status', '状态', 0, '状态字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000002000, 'delete_flag', '删除标识', 0, '删除标识字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000003000, 'boolean', '布尔', 0, '布尔字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);
INSERT INTO `sys_dict` VALUES (1992000000000004000, 'flowable_type', '流程类型', 0, '流程类型字典', '2025-11-21 20:40:17', 'system', '2025-11-21 20:40:17', 'system', 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1001, 'admin', '后台管理', 1, '', NULL, 1, 'Setting', '后台管理菜单', '', '', '2025-11-19 21:14:55', NULL, '2025-11-19 21:14:55', NULL, 1);
INSERT INTO `sys_permission` VALUES (1002, 'admin:permission', '权限管理', 1, '/admin/permission', 1001, 1, 'Key', '权限管理页面', NULL, NULL, '2025-11-19 21:15:04', NULL, '2025-11-19 21:15:04', NULL, 1);
INSERT INTO `sys_permission` VALUES (1003, 'admin:permission:view', '查看权限', 3, '', 1002, 1, 'Search', '获取权限树 查看权限', '/admin/permission/tree', 'GET', '2025-11-19 21:15:07', NULL, '2025-11-19 21:15:07', NULL, 1);
INSERT INTO `sys_permission` VALUES (1004, 'admin:permission:add', '新增权限', 3, '', 1002, 2, 'FolderAdd', '权限页面-新增权限', '/admin/permission/save', 'POST', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1005, 'admin:permission:edit', '编辑权限', 3, '', 1002, 3, 'EditPen', '权限页面-编辑权限', '/admin/permission/update', 'PUT', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (1006, 'admin:permission:delete', '删除权限', 3, '', 1002, 4, 'Delete', '权限页面-删除权限', '/admin/permission/delete', 'DELETE', '2025-11-19 21:15:12', NULL, '2025-11-19 21:15:12', NULL, 1);
INSERT INTO `sys_permission` VALUES (114214272234, 'codegen:generator:view', '代码生成页面', 1, '/codegen/CodeGenerator', 125634234412342343, 1, '', '代码生成管理页面', '', '', '2025-11-21 01:31:10', NULL, '2025-11-21 01:31:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (125634234412342343, 'codegen', '代码生成', 1, NULL, NULL, 100, 'Document', '代码生成管理菜单', NULL, NULL, '2025-11-21 01:29:10', NULL, '2025-11-21 01:29:10', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991143600931201000, 'admin:role', '角色管理', 1, '/admin/role', 1001, 3, 'Coin', '角色管理页面', '', '', '2025-11-19 21:56:43', NULL, '2025-11-19 21:56:43', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991205164119891970, 'admin:user', '用户管理', 1, '/admin/user', 1001, 2, 'Avatar', '用户管理页面', '', '', '2025-11-20 02:01:21', NULL, '2025-11-20 02:01:21', NULL, 1);
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
INSERT INTO `sys_permission` VALUES (1991455913399685122, 'admin:gateway', '动态网关', 1, '/gateway', NULL, 1, 'Connection', '', '', '', '2025-11-20 18:37:44', NULL, '2025-11-20 18:37:44', NULL, 1);
INSERT INTO `sys_permission` VALUES (1991456923035766785, 'admin:gateway:view', '网关列表', 1, '/gateway/gateway', 1991455913399685122, 0, '', '查看网关列表', '', '', '2025-11-20 18:41:45', NULL, '2025-11-20 18:41:45', NULL, 1);
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
INSERT INTO `sys_permission` VALUES (1991829450476220417, 'codegen:connection:view', '数据源管理', 1, '/codegen/DatabaseConnection', 125634234412342343, 0, '', '', '', '', '2025-11-21 19:22:02', NULL, '2025-11-21 19:22:02', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001100, 'admin:dict:view', '字典管理', 1, '/admin/dict', 1001, 4, 'Notebook', '访问字典管理页面', '', '', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001200, 'admin:dict:page', '分页查询', 3, '', 1993000000000001100, 10, '', '分页查询字典', '/admin/dict/page', 'GET', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001210, 'admin:dict:detail', '查询详情', 3, '', 1993000000000001100, 11, '', '根据ID查询字典详情', '/admin/dict/detail', 'GET', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001300, 'admin:dict:add', '新增', 3, '', 1993000000000001100, 20, 'FolderAdd', '新增字典项', '/admin/dict/save', 'POST', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001310, 'admin:dict:edit', '更新', 3, '', 1993000000000001100, 21, 'EditPen', '更新字典项', '/admin/dict/update', 'PUT', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001320, 'admin:dict:delete', '删除', 3, '', 1993000000000001100, 22, 'Delete', '删除字典项', '/admin/dict/delete', 'DELETE', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);
INSERT INTO `sys_permission` VALUES (1993000000000001400, 'admin:dict:refresh', '刷新缓存', 3, '', 1993000000000001100, 30, 'Refresh', '刷新字典缓存', '/admin/dict/refresh', 'POST', '2025-11-21 20:54:21', NULL, '2025-11-21 20:54:21', NULL, 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1991964767162171393, 2, 1001, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767162171394, 2, 1002, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767162171395, 2, 1003, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767162171396, 2, 1004, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503041, 2, 1005, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503042, 2, 1006, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503043, 2, 1991143600931201000, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503044, 2, 1991205164119891970, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503045, 2, 1991242409023631362, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767212503046, 2, 1991242808984072194, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028993, 2, 1991242997413179393, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028994, 2, 1991243204884426753, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028995, 2, 1991244603546398722, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028996, 2, 1991244778629230593, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028997, 2, 1991244964420120578, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767267028998, 2, 1991245154136879106, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554945, 2, 1991268323568517122, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554946, 2, 1991268510370234370, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554947, 2, 1991455913399685122, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554948, 2, 1991456923035766785, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554949, 2, 1991473796049752066, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554950, 2, 1991473884234993666, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554951, 2, 1991473999733542914, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767321554952, 2, 1991474490890735617, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886593, 2, 1993000000000001100, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886594, 2, 1993000000000001200, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886595, 2, 1993000000000001210, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886596, 2, 1993000000000001300, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886597, 2, 1993000000000001310, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886598, 2, 1993000000000001320, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886599, 2, 1993000000000001400, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886600, 2, 125634234412342343, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886601, 2, 1991829450476220417, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886602, 2, 1991474490890735618, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767371886603, 2, 1991474490890735619, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767443189761, 2, 1991474490890735620, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767451578369, 2, 1991474490890735621, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767451578370, 2, 1991474490890735622, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767451578371, 2, 1991474490890735623, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767451578372, 2, 1991474490890735624, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744194, 2, 1991474490890735625, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744195, 2, 114214272234, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744196, 2, 1991474490890735626, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744197, 2, 1991474490890735627, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744198, 2, 1991474490890735628, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744199, 2, 1991474490890735629, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744200, 2, 1991474490890735630, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744201, 2, 1991474490890735631, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744202, 2, 1991474490890735632, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744203, 2, 1991474490890735633, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744204, 2, 1991474490890735634, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767476744205, 2, 1991474490890735635, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767518687233, 2, 1991474490890735636, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767518687234, 2, 1991474490890735637, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767518687235, 2, 1991474490890735638, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (1991964767518687236, 2, 1991474490890735639, '2025-11-22 04:19:44', NULL, '2025-11-22 04:19:44', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515363106817, 1, 1001, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078337, 1, 1002, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078338, 1, 1003, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078339, 1, 1004, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078340, 1, 1005, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078341, 1, 1006, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515384078342, 1, 114214272234, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381506, 1, 125634234412342343, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381507, 1, 1991143600931201000, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381508, 1, 1991205164119891970, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381509, 1, 1991242409023631362, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381510, 1, 1991242808984072194, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381511, 1, 1991242997413179393, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515455381512, 1, 1991243204884426753, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878978, 1, 1991244603546398722, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878979, 1, 1991244778629230593, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878980, 1, 1991244964420120578, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878981, 1, 1991245154136879106, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878982, 1, 1991268323568517122, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878983, 1, 1991268510370234370, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878984, 1, 1991455913399685122, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515530878985, 1, 1991456923035766785, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016321, 1, 1991473796049752066, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016322, 1, 1991473884234993666, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016323, 1, 1991473999733542914, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016324, 1, 1991474490890735617, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016325, 1, 1991474490890735618, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016326, 1, 1991474490890735619, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016327, 1, 1991474490890735620, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016328, 1, 1991474490890735621, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016329, 1, 1991474490890735622, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016330, 1, 1991474490890735623, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016331, 1, 1991474490890735624, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016332, 1, 1991474490890735625, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016333, 1, 1991474490890735626, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016334, 1, 1991474490890735627, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016335, 1, 1991474490890735628, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016336, 1, 1991474490890735629, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016337, 1, 1991474490890735630, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016338, 1, 1991474490890735631, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016339, 1, 1991474490890735632, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016340, 1, 1991474490890735633, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016341, 1, 1991474490890735634, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515577016342, 1, 1991474490890735635, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513793, 1, 1991474490890735636, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513794, 1, 1991474490890735637, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513795, 1, 1991474490890735638, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513796, 1, 1991474490890735639, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513797, 1, 1991829450476220417, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513798, 1, 1993000000000001200, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513799, 1, 1993000000000001100, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513800, 1, 1993000000000001210, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513801, 1, 1993000000000001300, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513802, 1, 1993000000000001310, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513803, 1, 1993000000000001320, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2008684515652513804, 1, 1993000000000001400, '2026-01-07 07:38:03', NULL, '2026-01-07 07:38:03', NULL, 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$W9xSSyrrbEJ8brPWaHEjk.73jEP3daj.ltzeIXAEtp/oLYSIhNgYC', '管理员', '', '', NULL, '2025-11-18 18:45:43', NULL, '2025-11-22 04:23:02', NULL, 1);
INSERT INTO `sys_user` VALUES (1992208936665325569, 'test', '$2a$10$B6/HWEMm37XBm3vAJDpIduGCMam.iF8LspVQg21weWSTghugFz.he', '测试', '', '', NULL, '2025-11-22 20:29:59', NULL, '2025-11-22 20:30:13', NULL, 1);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2, 1, '2025-11-20 02:28:41', NULL, '2025-11-20 02:28:52', NULL, 1);
INSERT INTO `sys_user_role` VALUES (1991967599781089282, 1, 1, '2025-11-22 04:30:59', NULL, '2025-11-22 04:30:59', NULL, 1);
INSERT INTO `sys_user_role` VALUES (2008296925178875906, 1992208936665325569, 2, '2026-01-06 05:57:54', NULL, '2026-01-06 05:57:54', NULL, 1);


-- ----------------------------
-- Flowable gateway route
-- ----------------------------
INSERT INTO `sys_gateway_route` VALUES (2009000000000000001, 'cloudwaer-flowable-serve', 'lb://cloudwaer-flowable-serve', '[{"name":"Path","args":{"pattern":"/flowable/**"}}]', NULL, 0, '流程服务的网关转发配置', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);

-- ----------------------------
-- Flowable permissions
-- ----------------------------
INSERT INTO `sys_permission` VALUES (2009000000000000100, 'flowable', '流程管理', 1, NULL, NULL, 200, 'Share', '流程管理菜单', NULL, NULL, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000000110, 'flowable:model', '流程设计', 1, '/flowable/Modeler', 2009000000000000100, 1, 'EditPen', '流程设计页面', NULL, NULL, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000000120, 'flowable:process', '流程实例', 1, '/flowable/Process', 2009000000000000100, 2, 'List', '流程实例页面', NULL, NULL, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000000130, 'flowable:task', '流程任务', 1, '/flowable/Task', 2009000000000000100, 3, 'Check', '流程任务页面', NULL, NULL, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);

INSERT INTO `sys_permission` VALUES (2009000000000001001, 'flowable:model:list', '模型列表', 3, NULL, 2009000000000000110, 10, NULL, '查询模型列表', '/flowable/model/list', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001002, 'flowable:model:detail', '模型详情', 3, NULL, 2009000000000000110, 11, NULL, '查询模型详情', '/flowable/model/detail', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001003, 'flowable:model:save', '保存模型', 3, NULL, 2009000000000000110, 12, NULL, '保存模型', '/flowable/model/save', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001004, 'flowable:model:publish', '发布模型', 3, NULL, 2009000000000000110, 13, NULL, '发布模型', '/flowable/model/publish', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001005, 'flowable:model:copy', '复制模型', 3, NULL, 2009000000000000110, 14, NULL, '复制模型', '/flowable/model/copy', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001006, 'flowable:model:rollback', '回滚模型', 3, NULL, 2009000000000000110, 15, NULL, '回滚模型', '/flowable/model/rollback', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001007, 'flowable:model:bpmn', '获取BPMN', 3, NULL, 2009000000000000110, 16, NULL, '获取BPMN', '/flowable/model/bpmn', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);

INSERT INTO `sys_permission` VALUES (2009000000000001101, 'flowable:process:start', '启动流程', 3, NULL, 2009000000000000120, 10, NULL, '启动流程', '/flowable/process/start', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001102, 'flowable:process:started', '我发起的流程', 3, NULL, 2009000000000000120, 11, NULL, '我发起的流程', '/flowable/process/started', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);

INSERT INTO `sys_permission` VALUES (2009000000000001201, 'flowable:task:todo', '待办任务', 3, NULL, 2009000000000000130, 10, NULL, '待办任务', '/flowable/task/todo', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001202, 'flowable:task:done', '已办任务', 3, NULL, 2009000000000000130, 11, NULL, '已办任务', '/flowable/task/done', 'GET', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001203, 'flowable:task:claim', '领取任务', 3, NULL, 2009000000000000130, 12, NULL, '领取任务', '/flowable/task/claim', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_permission` VALUES (2009000000000001204, 'flowable:task:complete', '完成任务', 3, NULL, 2009000000000000130, 13, NULL, '完成任务', '/flowable/task/complete', 'POST', '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);

-- ----------------------------
-- Flowable role permissions (role_id=1)
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (2009000000000100001, 1, 2009000000000000100, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100002, 1, 2009000000000000110, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100003, 1, 2009000000000000120, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100004, 1, 2009000000000000130, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100005, 1, 2009000000000001001, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100006, 1, 2009000000000001002, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100007, 1, 2009000000000001003, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100008, 1, 2009000000000001004, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100009, 1, 2009000000000001005, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100010, 1, 2009000000000001006, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100011, 1, 2009000000000001007, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100012, 1, 2009000000000001101, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100013, 1, 2009000000000001102, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100014, 1, 2009000000000001201, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100015, 1, 2009000000000001202, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100016, 1, 2009000000000001203, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
INSERT INTO `sys_role_permission` VALUES (2009000000000100017, 1, 2009000000000001204, '2026-01-14 00:00:00', NULL, '2026-01-14 00:00:00', NULL, 1);
SET FOREIGN_KEY_CHECKS = 1;


