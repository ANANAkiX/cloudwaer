-- =============================================================
-- 目标库：cloudwaer
-- 用途：第三方登录（GitHub 等）用户绑定表 + GitHub 供应商模板种子
-- 依赖全量版本：db/cloudwaer.sql（含 sys_oauth_provider，见 global-setting-tables-20261029.sql）
-- 说明：仅增量，未回写全量转储
-- =============================================================

USE `cloudwaer`;

-- ----------------------------
-- 1) sys_user_oauth 第三方账号绑定（平台库，跨业务共用）
--    provider_type 与 sys_oauth_provider.provider_type 对齐：GITHUB|GITEE|WECHAT|GOOGLE|CUSTOM
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
-- 2) GitHub 供应商模板（默认停用）
--    在后台「全局设置 → 第三方登录」填入 GitHub OAuth App 的 Client ID/Secret 后启用；
--    redirect_uri 填浏览器可达的后端回调地址（如 http://localhost:9999/auth/oauth/callback），
--    需与 GitHub OAuth App 中登记的 Authorization callback URL 一致。
-- ----------------------------
INSERT INTO `sys_oauth_provider`
  (`id`, `provider_type`, `provider_name`, `client_id`, `client_secret`, `auth_url`, `token_url`, `user_info_url`, `scope`, `redirect_uri`, `icon`, `sort`, `enabled`, `remark`, `create_time`, `create_user`, `update_time`, `update_user`, `status`)
VALUES
  (1, 'GITHUB', 'GitHub', '', '', 'https://github.com/login/oauth/authorize', 'https://github.com/login/oauth/access_token', 'https://api.github.com/user', 'read:user user:email', 'http://localhost:9999/auth/oauth/callback', NULL, 1, 0, '模板：填入 OAuth App 凭据并核对 redirect_uri 后启用', NOW(), 'system', NOW(), 'system', 1);
