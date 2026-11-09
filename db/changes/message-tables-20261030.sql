-- ============================================================
-- 目标库：cloudwaer（平台库）
-- 用途：站内信（平台能力）骨架
--   sys_message            消息主体（NOTICE 公告 / SYSTEM 系统提示 / PRIVATE 私信预留）
--   sys_message_recipient  收件箱（一人一行：已读/删除）
--   sys_permission         消息中心菜单（用户自助收件箱，无按钮权限）
-- 依赖全量版本：cloudwaer.sql（2026-10-27 导出）
-- 后续业务接入（blog/admin 系统提示）只需调 MessageService / Feign sendMessage：
--   文章审核结果、评论审核结果、评论被回复、文章被点赞
-- ============================================================

USE `cloudwaer`;

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

-- 消息中心菜单（用户自助页面，挂顶级；路由 /message/messagecenter → views/message/MessageCenter.vue）
INSERT INTO `sys_permission` VALUES (2115000000000000201, 'message:center', '消息中心', 2, '/message/messagecenter', NULL, 1, 'Bell', '站内信收件箱（用户自助）', '2026-10-30 00:10:00', 'system', '2026-10-30 00:10:00', 'system', 1);

-- ADMIN 角色（id=1）授权消息中心菜单
INSERT INTO `sys_role_permission` VALUES (2115000000000000211, 1, 2115000000000000201, '2026-10-30 00:10:00', 'system', '2026-10-30 00:10:00', 'system', 1);
