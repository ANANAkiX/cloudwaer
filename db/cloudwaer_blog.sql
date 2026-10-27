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

 Date: 28/10/2026 00:36:25
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
  `collection_id` bigint(20) NULL DEFAULT NULL COMMENT '合集ID（blog_collection.id），可空',
  `collection_sort` int(11) NULL DEFAULT NULL COMMENT '合集内排序，NULL=按发布时间排序',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID（sys_user.id）',
  `author_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者昵称（冗余）',
  `author_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者头像URL（冗余）',
  `publish_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发布状态：0-草稿，1-已发布，2-已下线',
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
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_collection`(`collection_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_article
-- ----------------------------
INSERT INTO `blog_article` VALUES (2100000000000000021, '欢迎使用 CloudWaer 博客', '这是 cloudwaer-blog 的第一篇文章，演示 Markdown 渲染、标签与热度功能 。', '# 欢迎使用 CloudWaer 博客\n\n这是 **cloudwaer-blog** 的第一篇文章，用于演示 Markdown 渲染、标签、评论与热度功能。\n\n## 快速开始\n\n1. 在管理后台发布文章\n2. 在首页查看文章流与热度排行\n3. 在详情页体验评论互动\n\n```java\npublic class HelloBlog {\n    public static void main(String[] args) {\n        System.out.println(\"Hello CloudWaer Blog!\");\n    }\n}\n```\n\n祝写作愉快！', '/blog/uploads/0082f5534cf743a8a1a6419f8d00c8ab.png', 2100000000000000001, NULL, NULL, 1, '管理员', NULL, 1, 1, 1, 291, 8, 8, 15.077194320280533, '2026-10-24 00:00:00', NULL, '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_article` VALUES (2114430621076082689, 'Cloudwaer 登录认证业务白皮书', 'Cloudwaer 登录认证业务白皮书', '# Cloudwaer 登录认证业务白皮书\n\n| 项 | 内容 |\n|----|------|\n| 版本 | 1.1 |\n| 日期 | 2026-10-25 |\n| 范围 | 登录方式、Token 体系、多端隔离（clientId）、网关/单体鉴权、前端对接 |\n| 状态 | 与当前代码实现一致 |\n\n---\n\n## 1. 概述\n\nCloudwaer 采用 **自研会话 + JWT 声明** 的认证方案：\n\n- 对外 Token 为 **UUID 字符串**（非完整 JWT）\n- 服务端将 UUID → JWT / 用户 / 角色 / 权限 / **客户端标识** 存入 **Redis**\n- 前端携带 `Authorization: Bearer {uuid}` 与 `X-Client-Id`\n- **微服务**：网关 `AuthGlobalFilter` 校验 Redis + 客户端绑定  \n- **单体 boot**：`PermissionAuthorizationFilter` 做登录/权限与客户端绑定校验\n\n不引入 OAuth2 Authorization Server；自有双端（管理后台 / 博客前台）通过 **clientId** 实现 Token 不通用。\n\n---\n\n## 2. 登录方式（可插拔）\n\n### 2.1 架构\n\n```text\nPOST /auth/login\n  → AuthService.login\n    → LoginAuthenticationManager（按 loginType 调度）\n      → LoginAuthenticator 实现\n```\n\n| LoginType | 实现类 | 说明 | 状态 |\n|-----------|--------|------|------|\n| `ACCESS_PASSWORD` | `AccountPasswordAuthenticator` | 用户名 + 密码 | 已实现 |\n| `EMAIL_PASSWORD` | `EmailPasswordAuthenticator` | 邮箱 + 密码 | 已实现 |\n| `MOBILE_CODE` | — | 手机验证码 | 枚举预留 |\n| `THIRD_PARTY` | — | 第三方登录 | 枚举预留 |\n\n扩展方式：实现 `LoginAuthenticator` 并声明 `supports(LoginType)`，Spring 自动收集，无需改调度器。\n\n### 2.2 登录请求\n\n```http\nPOST /auth/login\nContent-Type: application/json\n\n{\n  \"username\": \"admin\",\n  \"password\": \"******\",\n  \"loginType\": \"ACCESS_PASSWORD\",\n  \"scope\": \"DEFAULT\",\n  \"clientId\": \"admin\"\n}\n```\n\n| 字段 | 必填 | 说明 |\n|------|------|------|\n| username / email | 按 loginType | 账号标识 |\n| password | 是 | 明文传输，服务端 BCrypt 比对 |\n| loginType | 是 | `ACCESS_PASSWORD` / `EMAIL_PASSWORD` |\n| clientId | 是 | `admin`（管理后台）/ `blog`（博客前台） |\n| scope | 否 | 预留，默认可传 `DEFAULT` |\n\n**验证码（可选，单体 boot）**：  \n`cloudwaer.captcha.enabled=true` 时，请求需带：\n\n```http\nX-Captcha-Id: {uuid}\nX-Captcha-Code: {用户输入}\n```\n\n由 `CaptchaValidationFilter` 校验 Redis 中 `captcha:{id}`。\n\n### 2.3 登录成功响应\n\n```json\n{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": {\n    \"token\": \"a1b2c3...（32位UUID）\"\n  }\n}\n```\n\n前端仅保存 `token`，后续请求全部携带。\n\n---\n\n## 3. 认证成功主链路（用户名密码）\n\n```mermaid\nsequenceDiagram\n  participant UI as 前端\n  participant CAP as CaptchaFilter(boot可选)\n  participant AUTH as Authentication\n  participant MGR as LoginAuthenticationManager\n  participant ACC as AccountPasswordAuthenticator\n  participant ADM as Admin(Feign)\n  participant RED as Redis TokenService\n\n  UI->>AUTH: POST /auth/login + clientId\n  Note over CAP: 单体且开启验证码时先过滤\n  AUTH->>MGR: authenticate(dto)\n  MGR->>ACC: supports(ACCESS_PASSWORD)\n  ACC->>ADM: getUserByUsername\n  ADM-->>ACC: UserDTO(password BCrypt, roleIds)\n  ACC->>ACC: passwordEncoder.matches\n  ACC->>ADM: getPermissionsByUserId\n  ACC->>RED: generateAndStoreToken(..., clientId)\n  RED-->>ACC: tokenUuid\n  ACC-->>UI: { token: tokenUuid }\n```\n\n步骤说明：\n\n1. Feign 调用 Admin 查询用户（含密码哈希、角色）\n2. BCrypt 校验密码；失败统一返回「账号或密码错误」（防用户枚举）\n3. 拉取权限码列表（失败不阻断登录，权限为空）\n4. `TokenService.generateAndStoreToken` 生成 UUID 并写 Redis\n5. 仅返回 UUID，不返回完整 JWT\n\n---\n\n## 4. Token 数据模型\n\n### 4.1 存储结构\n\n| Redis Key | 内容 |\n|-----------|------|\n| `{tokenPrefix}{uuid}` | JSON：jwtToken, userId, username, roleIds, permissions, **clientId**, createTime |\n| `{userTokenListPrefix}{userId}` | Set：该用户全部有效 uuid（多端） |\n\nTTL = `jwt.expiration`（毫秒配置转秒）。\n\n### 4.2 JWT Claims（服务端内部）\n\n| Claim | 含义 |\n|-------|------|\n| sub | 用户名 |\n| userId | 用户 ID |\n| tokenUuid | 会话 UUID |\n| **clientId** | 绑定客户端 |\n| roleIds / roleId | 角色 |\n| permissions | 权限码列表 |\n\nJWT 存在 Redis 中，业务侧通过 `SecurityContextUtil` 解析。\n\n### 4.3 多端登录\n\n- `allowMultipleLogin=true`：同一用户可有多条 uuid\n- `allowMultipleLogin=false`：登录前 `clearUserTokens(userId)`\n- 权限/角色变更：`updateUserTokenPermissions` 重写各 uuid 的 JWT 与 Redis，**无需强制重登**\n\n---\n\n## 5. 多端 Token 隔离（clientId）\n\n### 5.1 问题\n\n管理端（cloudwaer-ui）与博客前台（cloudwaer-blog-ui）共用账号体系时，若 Token 通用，博客登录态可被用于调用后台管理接口，存在越权面。\n\n### 5.2 方案\n\n| 端 | login `clientId` | 请求头 `X-Client-Id` |\n|----|------------------|----------------------|\n| cloudwaer-ui | `admin` | `admin` |\n| cloudwaer-blog-ui | `blog` | `blog` |\n\n校验逻辑（`TokenService.validateTokenClient`）：\n\n1. Token 必须在 Redis 存在（`validateToken`）\n2. 若 Token 已绑定 clientId，则必须与请求头一致\n3. **旧 Token 无 clientId → 兼容放行**（平滑升级）；重新登录后自动绑定\n\n校验点：\n\n- 微服务：`AuthGlobalFilter`（Gateway）\n- 单体：`PermissionAuthorizationFilter`（有 Token 时）\n\n不匹配时返回 **401 未授权**（与未登录同语义，避免暴露内部细节）。\n\n### 5.3 效果\n\n| 场景 | 结果 |\n|------|------|\n| blog 登录 token + `X-Client-Id: blog` 调博客接口 | 成功 |\n| blog 登录 token 改 `X-Client-Id: admin` 或去掉头调管理接口 | 401 |\n| admin 登录 token 调博客写接口且未带/错带 clientId | 401 |\n| 同一人两端各登录 | 两条独立 uuid，互不影响（多端开启时） |\n\n### 5.4 非目标\n\n- 不实现 OAuth2 授权码/第三方登录（无跨产品 SSO 需求前不引入）\n- 不强制 HttpOnly Cookie（跨端口 localStorage 隔离仍在；需要时再演进）\n\n---\n\n## 6. 请求链路中的鉴权层次\n\n```text\n浏览器\n  → (微服务) Gateway AuthGlobalFilter\n       · 白名单: /auth/**, /actuator/**\n       · 校验: Token 存在 + Redis 有效 + clientId 绑定\n  → 业务服务 PermissionAuthorizationFilter\n       · @PermissionCode / @RequireLogin\n       · 有 Token 时再验 clientId（单体主路径）\n       · enable=false 仅内部调用（X-Internal-Call）\n  → Controller / Service\n```\n\n| 模式 | 登录态 | 细粒度权限 |\n|------|--------|------------|\n| 微服务 | 网关 Redis + clientId | 服务内 `@PermissionCode` |\n| 单体 boot | `PermissionAuthorizationFilter` | 同左 |\n\n`SecurityContextUtil`：Header 取 uuid → Redis 取 JWT → Claims 得 userId / permissions / roleIds。\n\n---\n\n## 7. 注册与角色\n\n- `POST /auth/register` → Feign Admin `registerUser`\n- 默认角色：**GUEST**（`sys_role`）\n- 注册后可登录；写文章等需 **BLOGGER/ADMIN** 及对应权限码\n- 博客主申请：自助打标 → 管理端通过并分配 BLOGGER（角色变更刷新 Token）\n\n---\n\n## 8. 前端约定\n\n### 8.1 管理端 cloudwaer-ui\n\n```text\n登录 payload.clientId = \"admin\"\naxios 拦截器: Authorization: Bearer {token}\n              X-Client-Id: admin\n401 → 跳转 /login\n60s 轮询 GET /auth/token/valid\n```\n\n### 8.2 博客前台 cloudwaer-blog-ui\n\n```text\n登录 payload.clientId = \"blog\"\nlocalStorage: blog_token\naxios: Authorization: Bearer {token}\n       X-Client-Id: blog\n```\n\n### 8.3 通用 API\n\n| 方法 | 路径 | 说明 |\n|------|------|------|\n| POST | `/auth/login` | 登录 |\n| POST | `/auth/logout` | 删 Redis Token |\n| GET | `/auth/token/valid` | 校验当前 Token |\n| POST | `/auth/register` | 注册（GUEST） |\n| GET | `/auth/captcha` | 验证码图（boot 可选） |\n\n---\n\n## 9. 安全设计要点\n\n| 项 | 策略 |\n|----|------|\n| 密码存储 | BCrypt |\n| 对外会话 | 短 UUID，非完整 JWT 下发 |\n| 会话可撤销 | Redis 删除 uuid 即失效 |\n| 多端控制 | Set + allowMultipleLogin |\n| 客户端隔离 | clientId 写入 Token + 请求头校验 |\n| 防枚举 | 登录失败统一错误文案 |\n| 内部接口 | `@PermissionCode(enable=false)` + `X-Internal-Call` |\n| 白名单 | 仅 `/auth/**`、actuator 等 |\n\n---\n\n## 10. 演进建议（非当前实现）\n\n1. 登录失败次数锁定（IP/用户名 Redis 计数）  \n2. 登录响应附带 permissions，减少首屏请求  \n3. 改密/停用强制清 Token  \n4. 第三方登录 / 多产品 SSO 时再评估 OAuth2  \n5. 审计日志（成功/失败/踢下线）\n\n---\n\n## 11. 关键代码索引\n\n| 模块 | 路径 |\n|------|------|\n| 登录入口 | `cloudwaer-authentication/.../controller/AuthController.java` |\n| 调度 | `.../authenticator/LoginAuthenticationManager.java` |\n| 账号密码 | `.../authenticator/impl/AccountPasswordAuthenticator.java` |\n| Token | `cloudwaer-common-core/.../service/TokenService.java` |\n| 安全上下文 | `.../util/SecurityContextUtil.java` |\n| 网关过滤 | `cloudwaer-gateway/.../filter/AuthGlobalFilter.java` |\n| 权限过滤 | `cloudwaer-common-security/.../PermissionAuthorizationFilter.java` |\n| 验证码 | `cloudwaer-boot/.../CaptchaValidationFilter.java` |\n| 常量 | `CommonConstants.CLIENT_ID_HEADER` / `CLIENT_ADMIN` / `CLIENT_BLOG` |\n\n---\n\n## 12. 变更记录\n\n| 日期 | 说明 |\n|------|------|\n| 2026-10-25 | 初版：登录链路梳理 + clientId 多端隔离落地 |', NULL, 2100000000000000001, 2114417609275510785, NULL, 1, '管理员', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', 1, 0, 0, 47, 2, 1, 7.880748275277469, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article` VALUES (2114430765892816898, 'CloudWaer Blog 白皮书 — 从 0 到 1 搭建指南', '白皮书 — 从 0 到 1 搭建指南', '# CloudWaer Blog 白皮书 — 从 0 到 1 搭建指南\n\n| 项 | 内容 |\n|----|------|\n| 版本 | 1.0 |\n| 日期 | 2026-10-25 |\n| 范围 | 产品定位、总体架构、数据库、后端/前端设计、从零搭建步骤、关键决策与踩坑 |\n| 状态 | 与当前代码实现一致（P0–P3 主线已完成） |\n| 配套 | 登录/Token/clientId 详见 `doc/login-auth-whitepaper.md`；任务清单见 `TASKS.md` 第六节 |\n\n---\n\n## 1. 产品定位\n\n**CloudWaer Blog** 是搭建在 Cloudwaer 微服务平台上的**博客为主 + 微论坛轻互动**的多作者内容平台。\n\n- **博客为主**：文章（Markdown）、标签、分类、热度、置顶/推荐、归档、搜索\n- **微论坛轻互动**：两级评论（楼中楼）、话题短帖（topic + topic_reply）、点赞\n- **多作者入驻**：注册即得访客身份，可申请成为博客主，文章 CRUD 仅限本人\n\n### 1.1 角色模型\n\n```mermaid\nflowchart LR\n    A[\"匿名游客\"] -->|\"注册 clientId=blog\"| B[\"访客 GUEST<br/>评论/点赞/发帖/申请博客主\"]\n    B -->|\"申请 + 管理员审核\"| C[\"博客主 BLOGGER<br/>文章完整 CRUD（仅本人）\"]\n    D[\"管理员 ADMIN\"] -->|\"审核/管理全站\"| C\n    C -.行级隔离 author_id.-> E[(\"cloudwaer_blog\")]\n```\n\n| 角色 | sys_role | 能力 |\n|------|----------|------|\n| 匿名 | — | 浏览已发布内容、搜索、归档 |\n| 访客 GUEST | id=3（注册默认） | 评论、点赞、发话题、申请博客主 |\n| 博客主 BLOGGER | id=4（申请+审核） | 上述 + 文章 CRUD（`author_id` 限本人） |\n| 管理员 ADMIN | id=1 | 全站管理、审核中心、无行级限制 |\n\n### 1.2 端划分\n\n| 端 | 位置 | 端口 | 职责 | clientId |\n|----|------|------|------|----------|\n| 博客前台 + 写作 | `cloudwaer-blog-ui/` | 4100 | 公开展示、写文章、登录/注册 | `blog` |\n| 管理后台 | `cloudwaer-ui/` | 4000 | 内容治理、审核中心、用户/权限 | `admin` |\n| 后端 | `cloudwaer/cloudwaer-blog/` | 9999（boot） | 全部 blog 业务 API | — |\n\n---\n\n## 2. 总体架构\n\n### 2.1 架构图\n\n```mermaid\nflowchart TB\n    subgraph FE[\"前端\"]\n        BUI[\"cloudwaer-blog-ui :4100<br/>展示/写作/登录\"]\n        AUI[\"cloudwaer-ui :4000<br/>管理/审核\"]\n    end\n    subgraph GW[\"入口\"]\n        BOOT[\"cloudwaer-boot :9999<br/>（单体聚合，或 Gateway 微服务）\"]\n    end\n    subgraph BE[\"cloudwaer-blog-serve（cloudwaer-blog 模块）\"]\n        OP[\"/blog/open/** 公开读\"]\n        AR[\"/blog/article/** 文章\"]\n        CM[\"/blog/comment/** 评论\"]\n        TP[\"/blog/topic/** 话题\"]\n        LK[\"/blog/like/** 点赞\"]\n        AU[\"/blog/author/** 申请\"]\n        UP[\"/blog/upload/** 上传\"]\n    end\n    subgraph DB[\"独立库\"]\n        BDB[(\"cloudwaer_blog\")]\n    end\n    subgraph SYS[\"平台库\"]\n        SDB[(\"cloudwaer（sys_* 用户/权限）\")]\n    end\n    BUI -->|\"Bearer + X-Client-Id: blog\"| BOOT\n    AUI -->|\"Bearer + X-Client-Id: admin\"| BOOT\n    BOOT --> BE\n    OP --> BDB\n    AR --> BDB\n    CM --> BDB\n    TP --> BDB\n    LK --> BDB\n    AU --> SDB\n    AU --> BDB\n    UP --> BDB\n```\n\n### 2.2 核心约定\n\n- **一模块一库**：blog 数据全部在 `cloudwaer_blog`，与 `sys_*` 所在的 `cloudwaer` 库隔离；boot 多数据源由包路径切面 `BootPackageDataSourceConfig` + 请求路径过滤器 `DynamicDataSourceRouteFilter` 显式路由（`/blog/` → `blog`）\n- **两种运行模式互斥**：本地推荐单体 boot（禁 Nacos）；微服务模式按 Authentication → Admin-Serve → Gateway 顺序启动，网关动态路由 `/blog/**` **勿加 StripPrefix**\n- **多端 Token 隔离**：对外 Token 为 UUID，登录必须带 `clientId`（admin/blog），请求头带 `X-Client-Id`，两端 Token 不通用（详见登录白皮书）\n- **RBAC 三方一致**：`@PermissionCode` 注解码 ↔ SQL 种子码 ↔ 前端按钮码，风格 `模块:资源:动作`\n- **增量 SQL 规范**：一律 `cloudwaer/db/changes/作用-YYYYMMDD.sql`；全量转储 `db/*.sql` 以最新导出为准\n\n### 2.3 技术选型\n\n| 层 | 选型 | 说明 |\n|----|------|------|\n| 后端 | Java 17 / Spring Boot 3.2 / Cloud 2023 / MyBatis-Plus | 复用 cloudwaer-common（Result/BaseEntity/权限过滤器） |\n| 注册/登录 | authentication 模块 + Feign 内部接口 | `/auth/register`、`/auth/login` |\n| 前端 | Vue3 + TS + Vite + pinia + axios | blog-ui 自定义主题，不套 Element 后台风格 |\n| 渲染 | marked + highlight.js | Markdown + 代码高亮 + TOC |\n| 存储 | MySQL 8 + Redis | 业务数据 + Token 会话 |\n\n---\n\n## 3. 数据库设计\n\n库：**`cloudwaer_blog`**（脚本 `cloudwaer/db/cloudwaer_blog.sql`；增量 `db/changes/`）。`sys_*` 种子（角色/权限/网关路由）入 `cloudwaer` 库。\n\n### 3.1 表清单\n\n| 表 | 说明 | 关键字段 |\n|----|------|----------|\n| `blog_article` | 文章 | title/summary/content(MD)/cover_url/category_id/**author_id**/publish_status/is_top/is_recommend/view_count/like_count/comment_count/**heat_score**/publish_time |\n| `blog_category` | 分类 | name/slug/description/sort |\n| `blog_tag` | 标签 | name/slug |\n| `blog_article_tag` | 文章-标签关联 | article_id/tag_id，`uk_article_tag` 唯一索引 |\n| `blog_comment` | 两级评论 | article_id/**parent_id**/**root_id**/user_id/nickname/content/like_count/**audit_status** |\n| `blog_topic` | 话题短帖（微论坛） | author_id/title/content/audit_status/view_count |\n| `blog_topic_reply` | 话题回复 | topic_id/parent_id/root_id/content/audit_status |\n| `blog_like` | 点赞记录 | target_type/target_id/user_id，`uk` 防重复 |\n| `blog_author_apply` | 博客主申请单 | user_id/reason/audit_status |\n\n> 约定：实体继承 `BaseEntity`（雪花 id、create/update 审计、`status` 逻辑删除 0/1/2）；**中间表唯一索引冲突必须物理删除**（`@TableLogic` 逻辑删会残留物理行，见踩坑 9.1）。\n\n### 3.2 状态机\n\n- 文章 `publish_status`：0 草稿 → 1 已发布 → 2 已下线（草稿对匿名不可见）\n- 评论/话题/申请 `audit_status`：0 待审 → 1 通过 / 2 拒绝（管理端审核中心流转）\n- 热度 `heat_score`：`对数加权(浏览/点赞/评论) × 时间衰减`，交互增量 + 定时重算（30 天衰减窗口）\n\n---\n\n## 4. 后端设计（cloudwaer-blog 模块）\n\n### 4.1 模块结构\n\n```text\ncloudwaer/cloudwaer-blog/\n├── cloudwaer-blog-api/          # DTO（ArticleDTO/CommentDTO/TopicDTO/...）\n└── cloudwaer-blog-serve/\n    ├── BlogServeApplication.java     # 微服务启动类\n    ├── controller/                   # Open/Article/Comment/Topic/Like/Author/Upload\n    ├── service/ + service/impl/      # 业务实现（含 author_id 隔离）\n    ├── entity/ + mapper/             # blog_* 实体与 Mapper\n    └── resources/bootstrap.yml       # 微服务 Nacos 配置\n```\n\n接线四处：root `pom.xml` modules、`cloudwaer-common-version` 版本管理、boot 内嵌依赖（排 nacos 三件套）、boot 多数据源（yml + 过滤器 + 切面）。\n\n### 4.2 API 清单\n\n**公开读（匿名 = 方法不加权限注解；注意 `enable=false` 反而要求内部调用头）**\n\n| 方法 | 路径 | 说明 |\n|------|------|------|\n| GET | `/blog/open/article/list` | 已发布分页（keyword/categoryId/tagId） |\n| GET | `/blog/open/article/detail` | 详情，浏览量 +1 |\n| GET | `/blog/open/article/archive` | 归档（发布时间倒序） |\n| GET | `/blog/open/category/list` `/tag/list` | 分类/标签 |\n| GET | `/blog/open/comment/list` 等 | 已过审评论树 |\n| GET | `/blog/open/topic/list` | 话题列表 |\n\n**管理/用户写（带权限码 + Token）**\n\n| 路径 | 权限码 | 角色 |\n|------|--------|------|\n| `/blog/article/save|update|delete|page|detail` | `blog:article:add/edit/delete/view` | BLOGGER（本人）/ ADMIN |\n| `/blog/comment/*` | `blog:comment:add|audit|delete` | GUEST 发表 / ADMIN 审核 |\n| `/blog/topic/*` | `blog:topic:add|delete|audit` | GUEST 发帖 / ADMIN |\n| `/blog/like/toggle` | `blog:like:toggle` | 登录用户 |\n| `/blog/author/apply|audit` | `blog:author:apply|audit` | GUEST 申请 / ADMIN 审核 |\n| `/blog/upload/image` | 登录即可 | 图片 → `/blog/uploads/**` 静态映射 |\n\n### 4.3 注册与入驻\n\n```text\nPOST /auth/register {username,password,nickname,clientId:\"blog\"}\n  → AdminFeignClient.registerUser（X-Internal-Call）\n    → /admin/user/register：查重 + 解析 GUEST 角色 + BCrypt + 绑定\n```\n\n- 注册成功即可评论/发帖；**申请博客主**走 `blog_author_apply` 单，管理员在审核中心通过后 `assignRoles(BLOGGER)`（会刷新在线 Token 权限）\n- 登录必须带 `clientId=blog`；blog-ui 用 localStorage `blog_token`，请求头 `X-Client-Id: blog`\n\n### 4.4 行级数据隔离（必读）\n\n功能权限只管「能不能」，数据权限管「对谁」：\n\n```java\n// service 层统一收口（非 ADMIN 仅本人）\nLong uid = SecurityContextUtil.getCurrentUserId();\nif (!isAdmin(uid) && !uid.equals(article.getAuthorId())) {\n    throw new BusinessException(\"无权操作他人文章\");\n}\n```\n\n- ADMIN 判定：角色含 role id=1\n- update/delete/save 均校验；列表查询对非 ADMIN 追加 `author_id = uid`\n\n---\n\n## 5. 前端设计\n\n### 5.1 cloudwaer-blog-ui（:4100）\n\n```text\ncloudwaer-blog-ui/\n├── src/\n│   ├── api/            request.ts（Result 解码拦截器）+ blog.ts（类型化接口）\n│   ├── layouts/        BlogLayout（顶栏导航/搜索/页脚）\n│   ├── views/          Home / Article / Write / Login / Tag / Category / Archive / About\n│   ├── stores/         用户与设置（暗色/字号/布局/中英）\n│   └── style.css       设计令牌：纸白 #FAF9F6 / 墨 #23262B / 靛蓝 #4C6EF5\n├── vite.config.ts      :4100，/api → VITE_GATEWAY_ORIGIN（默认 boot :9999）\n└── package.json        vue3 + router + pinia + axios + marked + highlight.js\n```\n\n- **首页**：精选大卡（置顶/推荐）+ 文章流 + 侧栏（分类/标签云/关于）+ 搜索\n- **详情**：Markdown + 代码高亮 + TOC 目录 + 阅读进度 + 浏览量上报（调 detail 即 +1）\n- **写作** `/write`：登录（clientId=blog）+ `blog:article:add` 后可用，Markdown 编辑\n- **管理端文章页只管浏览/预览/编辑/删除**，发布写作在 blog-ui（架构决策 2026-10-25）\n\n### 5.2 cloudwaer-ui 管理端\n\n- `views/blog/`：文章管理（**卡片布局 + 预览**）、审核中心（评论/话题/申请）、标签/分类\n- 列表页强制 `el-card` + header 布局（见 AGENTS.md「管理端列表页 UI 风格」）\n- 按钮码接入 `config/permission.ts` + `checkButtonPermission`\n\n---\n\n## 6. 从 0 搭建步骤（复刻路径）\n\n> 前置：JDK 17、Maven 3.9+、Node 18+、MySQL 8、Redis；已可运行的 cloudwaer 平台（boot 能起、admin 能登录）。\n\n### Step 0 · 约定先行\n\n1. 确认「一模块一库」：目标库名 `cloudwaer_blog`\n2. 确认增量 SQL 只进 `cloudwaer/db/changes/作用-YYYYMMDD.sql`\n3. 确认权限码风格 `模块:资源:动作`，前端 URL 一律带 `/blog/**` 前缀\n\n### Step 1 · 后端模块骨架\n\n1. 建 `cloudwaer/cloudwaer-blog/{cloudwaer-blog-api,cloudwaer-blog-serve}` 两层 pom（仿 `cloudwaer-admin`）\n2. 四处接线：root modules、`cloudwaer-common-version`、boot 依赖（排 bootstrap/nacos×2）、boot 多数据源三件套（`application.yml` 加 `blog` 源、过滤器 `/blog/→blog`、切面 `push(\"blog\")`）\n3. `BlogServeApplication` + `bootstrap.yml`；`mvn -pl cloudwaer-blog/cloudwaer-blog-serve -am -DskipTests package` + `spring-javaformat:apply`\n\n### Step 2 · 数据库\n\n1. `db/cloudwaer_blog.sql`：五表（article/category/tag/article_tag/comment）+ 示例种子，`CREATE DATABASE cloudwaer_blog` + `USE`\n2. 增量（topic/like/author_apply 等）放 `db/changes/`，文件头注明目标库\n3. 导入后 `SHOW TABLES` 核对\n\n### Step 3 · 注册与角色\n\n1. admin：`RegisterDTO` + `AdminFeignClient.registerUser` + `/admin/user/register`（`enable=false`）+ `UserService.register`（查重、GUEST、BCrypt）\n2. authentication：`RegisterRequestDTO` + `/auth/register`\n3. 种子：`sys_role` GUEST/BLOGGER + `sys_permission` 菜单/页面/按钮 + `sys_role_permission` + `sys_gateway_route`\n4. 冒烟：注册 → 查库角色 → 登录（clientId=blog）拿 Token → 重复注册被拒\n\n### Step 4 · 文章 CRUD\n\n1. entity/mapper/service/controller（`/blog/article/*`），状态机 + 标签全量替换\n2. save 自动落 `author_id/author_name`；update 时草稿首次发布写 `publish_time`\n3. 冒烟：save → page → update(发布+换标签) → detail → delete\n\n### Step 5 · 公开读 + 网关路由\n\n1. `BlogOpenController`（list/detail/archive/category/tag），**方法不加注解即匿名**\n2. detail 浏览量原子自增：`UPDATE ... SET view_count = view_count + 1`\n3. 网关路由种子 `lb://cloudwaer-blog-serve` + `/blog/**`（无 StripPrefix）\n4. 冒烟：无 Token 列表/详情/草稿不可见\n\n### Step 6 · 互动与治理（评论/话题/点赞/申请）\n\n1. 两级评论（parent_id/root_id）、话题短帖、点赞 toggle、申请单\n2. 审核状态机 + 管理端审核中心\n3. `author_id` 行级隔离补进所有写路径\n\n### Step 7 · 前端 blog-ui\n\n1. Vite 脚手架 + 设计令牌 + BlogLayout + request/api 层（`X-Client-Id: blog`）\n2. 首页/详情（marked + hljs + TOC）→ 写作页/登录页 → 设置面板\n3. `npm run build` + dev(:4100) 联调冒烟（浏览器走一遍：列表→详情→搜索→写作）\n\n### Step 8 · 管理端与收尾\n\n1. `cloudwaer-ui`：`api/blog.ts` + views/blog/* + permission.ts 按钮码\n2. RBAC 三方比对（`scripts/check-rbac.sh`）\n3. 文档同步：AGENTS.md、TASKS.md 勾选、memory 归档\n\n---\n\n## 7. 关键设计决策\n\n| 决策 | 选择 | 理由 |\n|------|------|------|\n| 产品形态 | 博客为主 + 微论坛 | 互动上限 vs 复杂度折中；UGC 全量论坛成本过高 |\n| 数据库 | 独立库 cloudwaer_blog | 与 sys_* 隔离；集成模块一模块一库 |\n| 多端 Token | clientId 隔离，不上 OAuth2 | 自有双端够用；第三方/SSO 再评估 |\n| 公开接口鉴权 | 方法不加注解 = 匿名 | `enable=false` 语义是「仅内部调用」 |\n| 写作入口 | blog-ui `/write`，管理端只治理 | 发布链路与治理链路分离 |\n| 数据隔离 | service 层 author_id | 功能权限与数据权限分层，防越权 |\n| 增量 SQL | `db/changes/作用-时间.sql` | 可审计、可回放，全量转储只做基线 |\n\n---\n\n## 8. 踩坑手册（按出现顺序）\n\n1. **中间表逻辑删除撞唯一索引**：`@TableLogic` delete 是 UPDATE status=0，物理行残留 → 关联表用 `@Delete` 物理删除\n2. **`sys_permission` 14 列**：无 api_url/http_method，种子 INSERT 别写 16 列\n3. **Windows repackage 假成功**：日志显示替换但外层 jar 未更新 → 重打前删 `target` 旧 jar\n4. **Flowable XSD 阻塞启动**：`test-process-1.bpmn20.xml` 校验失败 → `--flowable.check-process-definitions=false`（既有问题，待专项修）\n5. **9999 常被 IDEA 调试占用**：冒烟用 `--server.port=9998`（Feign 跟随 `server.port` 自包含）\n6. **PowerShell + mysql -e 内嵌 JSON 必炸**：写临时 .sql 用 `source`；PS 双引号不转义 `\\\"`\n7. **公开接口别用 enable=false**：会被当成内部接口要求 `X-Internal-Call`，匿名应「不加注解」\n8. **登录 Feign 取权限被 401 吞掉** → 全站 403：内部接口必须 `enable=false` + 自动加 `X-Internal-Call`\n9. **highlight.js 全量引入**：Article chunk ~1MB，后续 manualChunks/按需注册语言\n\n---\n\n## 9. 运行与部署\n\n```bash\n# 后端（单体，推荐）\ncd cloudwaer\nmvn -pl cloudwaer-boot -am -DskipTests package\njava -jar cloudwaer-boot/target/cloudwaer-boot-1.0.0.jar        # :9999\n# 若 Flowable XSD 报错：加 --flowable.check-process-definitions=false\n\n# 前端\ncd cloudwaer-blog-ui && npm install && npm run dev              # :4100\ncd cloudwaer-ui && npm install && npm run dev                   # :4000\n```\n\n- 数据库初始化：`db/cloudwaer.sql` → `db/changes/` 相关增量 → `db/cloudwaer_blog.sql`（+ forum/like 等增量）\n- 微服务模式：Nacos 配置各 serve 数据源，按 Authentication → Admin → Blog → Gateway 启动，网关 `/actuator/gateway/refresh`\n- 冒烟清单：注册 → 登录(blog) → 写文章 → 发布 → 匿名列表/详情 → 评论审核 → 点赞 → 申请博客主\n\n---\n\n## 10. 路线图\n\n| 阶段 | 内容 | 状态 |\n|------|------|------|\n| P0 | 模块骨架/独立库/注册/CRUD/公开读/网关路由/脚手架/首页详情 | ✅ |\n| P1 | 标签分类、评论两级+审核、申请博客主、行级隔离、话题、热度、点赞/搜索、管理页、RBAC 校验 | ✅ |\n| P2 | 标签云/热门/归档、图片上传、暗色/字号/布局、关于/友链、SEO | ✅（基础） |\n| P3 | 管理端卡片布局、审核中心、写作迁 blog-ui、登录 clientId | ✅ |\n| 后续 | 单元测试、typecheck 清零、论坛/点赞全量转储补齐、Flowable XSD 修复、热度定时任务加固 | 见 TASKS.md |\n\n---\n\n*本文档由 cloudwaer-blog 项目实践沉淀；改动请同步 `TASKS.md` 与 `memory/MEMORY.md`。*', NULL, 2100000000000000001, 2114417609275510785, NULL, 1, '管理员', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', 1, 0, 0, 49, 2, 1, 7.920231965644408, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);
INSERT INTO `blog_article` VALUES (2114743784115978242, 'cloudwaer-blog-whitepaper', NULL, '# CloudWaer Blog 白皮书 — 从 0 到 1 搭建指南\n\n| 项 | 内容 |\n|----|------|\n| 版本 | 1.1 |\n| 日期 | 2026-10-27 |\n| 范围 | 产品定位、总体架构、数据库、后端/前端设计、从零搭建步骤、关键决策与踩坑 |\n| 状态 | 与当前代码实现一致（P0–P3 主线 + 站点设置/导入/合集/布局编排已完成） |\n| 配套 | 登录/Token/clientId 详见 `doc/login-auth-whitepaper.md`；任务清单见 `TASKS.md` 第六、七节 |\n\n---\n\n## 1. 产品定位\n\n**CloudWaer Blog** 是搭建在 Cloudwaer 微服务平台上的**博客为主 + 微论坛轻互动**的多作者内容平台。\n\n- **博客为主**：文章（Markdown）、标签、分类、合集、热度、置顶/推荐、归档、搜索；支持 MD/TXT/PDF/DOCX 导入\n- **微论坛轻互动**：两级评论（楼中楼）、话题短帖（topic + topic_reply）、点赞\n- **多作者入驻**：注册即得访客身份，可申请成为博客主；文章/合集 CRUD 仅限本人（`author_id` 行级隔离）\n- **站点可配置**：标题/图标/导航/页面文案/首页与文章布局由 `blog_site_setting` 驱动，前台按配置渲染\n\n### 1.1 角色模型\n\n```mermaid\nflowchart LR\n    A[\"匿名游客\"] -->|\"注册 clientId=blog\"| B[\"访客 GUEST<br/>评论/点赞/发帖/申请博客主\"]\n    B -->|\"申请 + 管理员审核\"| C[\"博客主 BLOGGER<br/>文章完整 CRUD（仅本人）\"]\n    D[\"管理员 ADMIN\"] -->|\"审核/管理全站\"| C\n    C -.行级隔离 author_id.-> E[(\"cloudwaer_blog\")]\n```\n\n| 角色 | sys_role | 能力 |\n|------|----------|------|\n| 匿名 | — | 浏览已发布内容、搜索、归档 |\n| 访客 GUEST | id=3（注册默认） | 评论（仅需登录）、点赞、发话题、申请博客主 |\n| 博客主 BLOGGER | id=4（申请+审核） | 上述 + 文章/合集 CRUD（`author_id` 限本人） |\n| 管理员 ADMIN | id=1 | 全站管理、审核中心、无行级限制 |\n\n### 1.2 端划分\n\n| 端 | 位置 | 端口 | 职责 | clientId |\n|----|------|------|------|----------|\n| 博客前台 + 写作 | `cloudwaer-blog-ui/` | 4100 | 公开展示、写文章、登录/注册 | `blog` |\n| 管理后台 | `cloudwaer-ui/` | 4000 | 内容治理、审核中心、用户/权限 | `admin` |\n| 后端 | `cloudwaer/cloudwaer-blog/` | 9999（boot） | 全部 blog 业务 API | — |\n\n---\n\n## 2. 总体架构\n\n### 2.1 架构图\n\n```mermaid\nflowchart TB\n    subgraph FE[\"前端\"]\n        BUI[\"cloudwaer-blog-ui :4100<br/>展示/写作/登录\"]\n        AUI[\"cloudwaer-ui :4000<br/>管理/审核\"]\n    end\n    subgraph GW[\"入口\"]\n        BOOT[\"cloudwaer-boot :9999<br/>（单体聚合，或 Gateway 微服务）\"]\n    end\n    subgraph BE[\"cloudwaer-blog-serve（cloudwaer-blog 模块）\"]\n        OP[\"/blog/open/** 公开读\"]\n        AR[\"/blog/article/** 文章（+ /my 创作中心）\"]\n        CM[\"/blog/comment/** 评论\"]\n        TP[\"/blog/topic/** 话题\"]\n        LK[\"/blog/like/** 点赞\"]\n        AU[\"/blog/author/** 申请\"]\n        UP[\"/blog/upload/** 上传\"]\n        CL[\"/blog/collection/** 合集\"]\n        ST[\"/blog/setting/** 站点设置\"]\n    end\n    subgraph DB[\"独立库\"]\n        BDB[(\"cloudwaer_blog\")]\n    end\n    subgraph SYS[\"平台库\"]\n        SDB[(\"cloudwaer（sys_* 用户/权限）\")]\n    end\n    BUI -->|\"Bearer + X-Client-Id: blog\"| BOOT\n    AUI -->|\"Bearer + X-Client-Id: admin\"| BOOT\n    BOOT --> BE\n    OP --> BDB\n    AR --> BDB\n    CM --> BDB\n    TP --> BDB\n    LK --> BDB\n    AU --> SDB\n    AU --> BDB\n    UP --> BDB\n    CL --> BDB\n    ST --> BDB\n```\n\n### 2.2 核心约定\n\n- **一模块一库**：blog 数据全部在 `cloudwaer_blog`，与 `sys_*` 所在的 `cloudwaer` 库隔离；boot 多数据源由包路径切面 `BootPackageDataSourceConfig` + 请求路径过滤器 `DynamicDataSourceRouteFilter` 显式路由（`/blog/` → `blog`）\n- **两种运行模式互斥**：本地推荐单体 boot（禁 Nacos）；微服务模式按 Authentication → Admin-Serve → Gateway 顺序启动，网关动态路由 `/blog/**` **勿加 StripPrefix**\n- **多端 Token 隔离**：对外 Token 为 UUID，登录必须带 `clientId`（admin/blog），请求头带 `X-Client-Id`，两端 Token 不通用（详见登录白皮书）\n- **RBAC 三方一致**：`@PermissionCode` 注解码 ↔ SQL 种子码 ↔ 前端按钮码，风格 `模块:资源:动作`\n- **增量 SQL 规范**：一律 `cloudwaer/db/changes/作用-YYYYMMDD.sql`；全量转储 `db/*.sql` 以最新导出为准\n\n### 2.3 技术选型\n\n| 层 | 选型 | 说明 |\n|----|------|------|\n| 后端 | Java 17 / Spring Boot 3.2 / Cloud 2023 / MyBatis-Plus | 复用 cloudwaer-common（Result/BaseEntity/权限过滤器） |\n| 注册/登录 | authentication 模块 + Feign 内部接口 | `/auth/register`、`/auth/login` |\n| 前端 | Vue3 + TS + Vite + pinia + axios | blog-ui 自定义主题，不套 Element 后台风格 |\n| 渲染 | marked + highlight.js | Markdown + 代码高亮 + TOC |\n| 存储 | MySQL 8 + Redis | 业务数据 + Token 会话 |\n\n---\n\n## 3. 数据库设计\n\n库：**`cloudwaer_blog`**（脚本 `cloudwaer/db/cloudwaer_blog.sql`；增量 `db/changes/`）。`sys_*` 种子（角色/权限/网关路由）入 `cloudwaer` 库。\n\n### 3.1 表清单\n\n| 表 | 说明 | 关键字段 |\n|----|------|----------|\n| `blog_article` | 文章 | title/summary/content(MD)/cover_url/category_id/**author_id**/**author_avatar**/**collection_id**/**collection_sort**/publish_status/is_top/is_recommend/view_count/like_count/comment_count/**heat_score**/publish_time |\n| `blog_category` | 分类 | name/slug/description/sort |\n| `blog_tag` | 标签 | name/slug |\n| `blog_article_tag` | 文章-标签关联 | article_id/tag_id，`uk_article_tag` 唯一索引 |\n| `blog_comment` | 两级评论 | article_id/**parent_id**/**root_id**/user_id/nickname/content/like_count/**audit_status** |\n| `blog_topic` | 话题短帖（微论坛） | author_id/title/content/audit_status/view_count |\n| `blog_topic_reply` | 话题回复 | topic_id/parent_id/root_id/content/audit_status |\n| `blog_like` | 点赞记录 | target_type/target_id/user_id，`uk` 防重复 |\n| `blog_author_apply` | 博客主申请单 | user_id/reason/audit_status |\n| `blog_collection` | 合集 | name/description/cover_url/**author_id**/author_name/**article_count**/sort/is_featured/visibility |\n| `blog_site_setting` | 站点设置 | key-value；扁平键 + JSON 键（nav/pages/articleLayout/homeLayout，value 已扩 TEXT） |\n\n> 约定：实体继承 `BaseEntity`（雪花 id、create/update 审计、`status` 逻辑删除 0/1/2）；**中间表唯一索引冲突必须物理删除**（`@TableLogic` 逻辑删会残留物理行，见踩坑 9.1）。\n\n### 3.2 状态机与合集\n\n- 文章 `publish_status`：0 草稿 → 1 已发布 → 2 已下线（草稿对匿名不可见）\n- 评论/话题/申请 `audit_status`：0 待审 → 1 通过 / 2 拒绝（管理端审核中心流转）\n- 热度 `heat_score`：`对数加权(浏览/点赞/评论) × 时间衰减`，交互增量 + 定时重算（30 天衰减窗口）\n- 合集：一篇文章最多属 **一个** 合集（`collection_id` 可空）；合集内顺序 `collection_sort`（NULL 则按 `publish_time` 降序）；删除合集只解绑不删文章；`article_count` 仅统计已发布文章\n\n---\n\n## 4. 后端设计（cloudwaer-blog 模块）\n\n### 4.1 模块结构\n\n```text\ncloudwaer/cloudwaer-blog/\n├── cloudwaer-blog-api/          # DTO（Article/Comment/Topic/Collection/SiteSetting/...）\n└── cloudwaer-blog-serve/\n    ├── BlogServeApplication.java     # 微服务启动类\n    ├── controller/                   # Open/Article/Comment/Topic/Like/Author/Upload/Collection/Setting\n    ├── service/ + service/impl/      # 业务实现（含 author_id 隔离）\n    ├── util/ArticleFileParser.java   # MD/TXT/PDF/DOCX → Markdown\n    ├── task/BlogHeatTask.java        # 热度定时重算\n    ├── entity/ + mapper/             # blog_* 实体与 Mapper\n    └── resources/bootstrap.yml       # 微服务 Nacos 配置\n```\n\n接线四处：root `pom.xml` modules、`cloudwaer-common-version` 版本管理、boot 内嵌依赖（排 nacos 三件套）、boot 多数据源（yml + 过滤器 + 切面）。\n\n### 4.2 API 清单\n\n**公开读（匿名 = 方法不加权限注解；注意 `enable=false` 反而要求内部调用头）**\n\n| 方法 | 路径 | 说明 |\n|------|------|------|\n| GET | `/blog/open/article/list` | 已发布分页（keyword/categoryId/tagId） |\n| GET | `/blog/open/article/detail` | 详情，浏览量 +1；含合集信息与合集内兄弟文章 |\n| GET | `/blog/open/article/archive` | 归档（发布时间倒序） |\n| GET | `/blog/open/article/hot` | 热门文章（按 heat_score） |\n| GET | `/blog/open/category/list` `/tag/list` | 分类/标签 |\n| GET | `/blog/open/comment/list` 等 | 已过审评论树 |\n| GET | `/blog/open/topic/list` | 话题列表 |\n| GET | `/blog/open/setting` | 站点设置（标题/导航/布局等，驱动前台渲染） |\n\n**管理/用户写（带权限码 + Token）**\n\n| 路径 | 权限码 | 角色 |\n|------|--------|------|\n| `/blog/article/save|update|delete|page|detail` | `blog:article:add/edit/delete/view` | BLOGGER（本人）/ ADMIN |\n| `/blog/article/import` | `blog:article:add` | multipart ≤20MB，MD/TXT/PDF/DOCX → 表单回填 |\n| `/blog/article/my/*` | `@RequireLogin` | 创作中心：本人文章 CRUD（blog-ui） |\n| `/blog/comment/*` | `@RequireLogin` | 发表仅需登录；删除=本人或 ADMIN（按钮码已收敛） |\n| `/blog/topic/*` | `blog:topic:add|delete|audit` | GUEST 发帖 / ADMIN |\n| `/blog/like/toggle` | `blog:like:toggle` | 登录用户 |\n| `/blog/author/apply|audit` | `blog:author:apply|audit` | GUEST 申请 / ADMIN 审核 |\n| `/blog/upload/image` | 登录即可 | 图片 → `/blog/uploads/**` 静态映射 |\n| `/blog/collection/*` | `blog:collection:view/add/edit/delete/sort` | 管理端；非 ADMIN 仅本人 |\n| `/blog/collection/my/*` | `@RequireLogin` | 创作中心：合集 CRUD、绑定/移出文章、合集内排序 |\n| `/blog/setting` `GET` / `POST /save` | `blog:setting:view` / `edit` | 管理端站点配置 |\n\n### 4.3 注册与入驻\n\n```text\nPOST /auth/register {username,password,nickname,clientId:\"blog\"}\n  → AdminFeignClient.registerUser（X-Internal-Call）\n    → /admin/user/register：查重 + 解析 GUEST 角色 + BCrypt + 绑定\n```\n\n- 注册成功即可评论/发帖；**申请博客主**走 `blog_author_apply` 单，管理员在审核中心通过后 `assignRoles(BLOGGER)`（会刷新在线 Token 权限）\n- 登录必须带 `clientId=blog`；blog-ui 用 localStorage `blog_token`，请求头 `X-Client-Id: blog`\n\n### 4.4 行级数据隔离（必读）\n\n功能权限只管「能不能」，数据权限管「对谁」：\n\n```java\n// service 层统一收口（非 ADMIN 仅本人）\nLong uid = SecurityContextUtil.getCurrentUserId();\nif (!isAdmin(uid) && !uid.equals(article.getAuthorId())) {\n    throw new BusinessException(\"无权操作他人文章\");\n}\n```\n\n- ADMIN 判定：角色含 role id=1\n- update/delete/save 均校验；列表查询对非 ADMIN 追加 `author_id = uid`\n- **合集** 同规则：管理端 CRUD 与创作中心 `/my/*` 均校验 `collection.author_id`\n\n### 4.5 站点设置（blog_site_setting）\n\n- 存储：key-value；扁平键 `siteTitle/siteDescription/favicon/enableSearch/layout/footerText`\n- 复杂配置：独立键 `nav` / `pages` / `articleLayout` / `homeLayout`，value 为 **JSON 字符串**（列已扩 TEXT）\n- DTO `SiteSettingDTO` 将 JSON 键展开为 `NavSettingItem` / `PageSettingItem` / `ArticleLayoutSetting` / `HomeLayoutSetting`\n- 管理端 `SiteSetting.vue` 分组 Tab（站点/导航/页面/布局）；前台 `GET /blog/open/setting` 匿名读，导航与首页/文章侧栏模块按配置渲染（空则回落默认）\n\n### 4.6 文章导入\n\n- `POST /blog/article/import`：multipart 文件（MD/TXT/PDF/DOCX，≤20MB）\n- `ArticleFileParser` 解析为 Markdown；**标题默认文件名**（去扩展名）；**不直接落库**，前端回填表单后再 save/my/save\n- 依赖：pdfbox + poi-ooxml；按钮码可用 `blog:article:import` 或复用 add\n\n---\n\n## 5. 前端设计\n\n### 5.1 cloudwaer-blog-ui（:4100）\n\n```text\ncloudwaer-blog-ui/\n├── src/\n│   ├── api/            request.ts（Result 解码拦截器）+ blog.ts（类型化接口）\n│   ├── layouts/        BlogLayout（顶栏导航/搜索/头像悬停菜单/页脚，按站点配置渲染）\n│   ├── views/          Home / Article / Write / Login / Profile / Forum / Topic / Tag / Category / Archive / About\n│   ├── components/\n│   │   ├── home/       Featured/ArticleList/Nav/TagCloud/HotArticles/... 卡片\n│   │   └── article/    Author/ReadingInfo/Action/Related/Toc 卡片 + Mermaid 渲染\n│   ├── config/         layoutModules.ts（首页/文章侧栏模块编排默认值）\n│   ├── stores/         auth + settings（暗色/字号/布局/中英 + 站点名）\n│   └── style.css       设计令牌：纸白 #FAF9F6 / 墨 #23262B / 靛蓝 #4C6EF5\n├── vite.config.ts      :4100，/api → VITE_GATEWAY_ORIGIN（默认 boot :9999）\n└── package.json        vue3 + router + pinia + axios + marked + highlight.js + mermaid + element-plus\n```\n\n- **首页**：约 80% 三栏容器 `min(80vw,1600)`，左右 `220|1fr|280`；精选大卡 + 文章流 + 侧栏；模块可由 `homeLayout.modules` 编排\n- **详情**：约 80% 三栏 `200|1fr|240`（左作者/阅读信息/互动/相关；中正文；右 TOC）；Markdown + 代码高亮 + **Mermaid 图** + 阅读进度 + 合集/相关阅读切换；TOC 滚动高亮；浏览量调 detail 即 +1\n- **写作** `/write`：登录（clientId=blog）+ `blog:article:add` 后可用；**分屏 MD 编辑+预览**（深色跟随主题）；标签可搜可建；可选合集；支持文件导入\n- **个人中心** `/profile`：概览 / 我的文章 / **我的合集**（建改删、加入/移出文章）/ 账号\n- **顶栏**：放大；写文章入口；头像悬停矩形菜单（设置/个人中心/退出）\n- **管理端文章页只管浏览/预览/编辑/删除**，发布写作在 blog-ui（架构决策 2026-10-25）\n\n### 5.2 cloudwaer-ui 管理端\n\n- `views/blog/`：文章管理（**卡片布局 + 预览/前台**）、审核中心（评论/话题/申请）、标签/分类、**合集管理**、**站点设置**（含布局编辑）\n- 列表页强制 `el-card` + header 布局（见 AGENTS.md「管理端列表页 UI 风格」）\n- 按钮码接入 `config/permission.ts` + `checkButtonPermission`；blog 相关表格列居中、分页居中\n\n---\n\n## 6. 从 0 搭建步骤（复刻路径）\n\n> 前置：JDK 17、Maven 3.9+、Node 18+、MySQL 8、Redis；已可运行的 cloudwaer 平台（boot 能起、admin 能登录）。\n\n### Step 0 · 约定先行\n\n1. 确认「一模块一库」：目标库名 `cloudwaer_blog`\n2. 确认增量 SQL 只进 `cloudwaer/db/changes/作用-YYYYMMDD.sql`\n3. 确认权限码风格 `模块:资源:动作`，前端 URL 一律带 `/blog/**` 前缀\n\n### Step 1 · 后端模块骨架\n\n1. 建 `cloudwaer/cloudwaer-blog/{cloudwaer-blog-api,cloudwaer-blog-serve}` 两层 pom（仿 `cloudwaer-admin`）\n2. 四处接线：root modules、`cloudwaer-common-version`、boot 依赖（排 bootstrap/nacos×2）、boot 多数据源三件套（`application.yml` 加 `blog` 源、过滤器 `/blog/→blog`、切面 `push(\"blog\")`）\n3. `BlogServeApplication` + `bootstrap.yml`；`mvn -pl cloudwaer-blog/cloudwaer-blog-serve -am -DskipTests package` + `spring-javaformat:apply`\n\n### Step 2 · 数据库\n\n1. `db/cloudwaer_blog.sql`：核心表（article/category/tag/article_tag/comment 等）+ 示例种子，`CREATE DATABASE cloudwaer_blog` + `USE`\n2. 增量（topic/like/author_apply/site_setting/collection 等）放 `db/changes/`，文件头注明目标库\n3. 导入后 `SHOW TABLES` 核对；合集/设置相关增量见第 9 节清单\n\n### Step 3 · 注册与角色\n\n1. admin：`RegisterDTO` + `AdminFeignClient.registerUser` + `/admin/user/register`（`enable=false`）+ `UserService.register`（查重、GUEST、BCrypt）\n2. authentication：`RegisterRequestDTO` + `/auth/register`\n3. 种子：`sys_role` GUEST/BLOGGER + `sys_permission` 菜单/页面/按钮 + `sys_role_permission` + `sys_gateway_route`\n4. 冒烟：注册 → 查库角色 → 登录（clientId=blog）拿 Token → 重复注册被拒\n\n### Step 4 · 文章 CRUD\n\n1. entity/mapper/service/controller（`/blog/article/*`），状态机 + 标签全量替换\n2. save 自动落 `author_id/author_name`；update 时草稿首次发布写 `publish_time`\n3. 冒烟：save → page → update(发布+换标签) → detail → delete\n\n### Step 5 · 公开读 + 网关路由\n\n1. `BlogOpenController`（list/detail/archive/category/tag），**方法不加注解即匿名**\n2. detail 浏览量原子自增：`UPDATE ... SET view_count = view_count + 1`\n3. 网关路由种子 `lb://cloudwaer-blog-serve` + `/blog/**`（无 StripPrefix）\n4. 冒烟：无 Token 列表/详情/草稿不可见\n\n### Step 6 · 互动与治理（评论/话题/点赞/申请）\n\n1. 两级评论（parent_id/root_id）、话题短帖、点赞 toggle、申请单\n2. 审核状态机 + 管理端审核中心\n3. `author_id` 行级隔离补进所有写路径\n\n### Step 7 · 前端 blog-ui\n\n1. Vite 脚手架 + 设计令牌 + BlogLayout + request/api 层（`X-Client-Id: blog`）\n2. 首页/详情（marked + hljs + TOC）→ 写作页/登录页 → 设置面板\n3. `npm run build` + dev(:4100) 联调冒烟（浏览器走一遍：列表→详情→搜索→写作）\n\n### Step 8 · 管理端与收尾\n\n1. `cloudwaer-ui`：`api/blog.ts` + views/blog/* + permission.ts 按钮码\n2. RBAC 三方比对（`scripts/check-rbac.sh`）\n3. 文档同步：AGENTS.md、TASKS.md 勾选、memory 归档\n\n---\n\n## 7. 关键设计决策\n\n| 决策 | 选择 | 理由 |\n|------|------|------|\n| 产品形态 | 博客为主 + 微论坛 | 互动上限 vs 复杂度折中；UGC 全量论坛成本过高 |\n| 数据库 | 独立库 cloudwaer_blog | 与 sys_* 隔离；集成模块一模块一库 |\n| 多端 Token | clientId 隔离，不上 OAuth2 | 自有双端够用；第三方/SSO 再评估 |\n| 公开接口鉴权 | 方法不加注解 = 匿名 | `enable=false` 语义是「仅内部调用」 |\n| 写作入口 | blog-ui `/write`，管理端只治理 | 发布链路与治理链路分离 |\n| 数据隔离 | service 层 author_id | 功能权限与数据权限分层，防越权 |\n| 增量 SQL | `db/changes/作用-时间.sql` | 可审计、可回放，全量转储只做基线 |\n| 站点配置 | key-value + 复杂键 JSON 字符串 | 扁平键兼容存量；nav/pages/layout 一次扩展，无需频繁 ALTER |\n| 前台布局 | 配置驱动模块编排，空则回落默认 | 不改代码可调首页/文章侧栏；联调有安全默认 |\n| 合集 | 一文一合集 + collection_sort | 贴近系列文场景；避免多对多 join 复杂度 |\n| 评论权限 | 收敛到 @RequireLogin + 归属/角色校验 | 去掉细粒度按钮码；审核后置到审核中心，降低 GUEST 入驻摩擦 |\n| 文章导入 | 仅解析回填，不直接落库 | 标题/分类/标签仍由作者确认后再保存，避免脏数据 |\n\n---\n\n## 8. 踩坑手册（按出现顺序）\n\n1. **中间表逻辑删除撞唯一索引**：`@TableLogic` delete 是 UPDATE status=0，物理行残留 → 关联表用 `@Delete` 物理删除\n2. **`sys_permission` 14 列**：无 api_url/http_method，种子 INSERT 别写 16 列\n3. **Windows repackage 假成功**：日志显示替换但外层 jar 未更新 → 重打前删 `target` 旧 jar\n4. **Flowable XSD 阻塞启动**：`test-process-1.bpmn20.xml` 校验失败 → `--flowable.check-process-definitions=false`（既有问题，待专项修）\n5. **9999 常被 IDEA 调试占用**：冒烟用 `--server.port=9998`（Feign 跟随 `server.port` 自包含）\n6. **PowerShell + mysql -e 内嵌 JSON 必炸**：写临时 .sql 用 `source`；PS 双引号不转义 `\\\"`\n7. **公开接口别用 enable=false**：会被当成内部接口要求 `X-Internal-Call`，匿名应「不加注解」\n8. **登录 Feign 取权限被 401 吞掉** → 全站 403：内部接口必须 `enable=false` + 自动加 `X-Internal-Call`\n9. **highlight.js 全量引入**：Article chunk ~1MB，后续 manualChunks/按需注册语言\n10. **TagSelect allow-create 存数字 id**：新建标签须拦截并走 `saveTag` 取回真实 ID；提交前滤掉纯数字 tagIds（Long 反序列化）\n11. **合集删除要物理清 article 绑定字段**：`@TableLogic` 删合集后 article 的 collection_id 残留 → Mapper `@Update` 置 NULL\n12. **sys_permission 主键占用**：合集权限种子勿用 170~175（已占），用 2100000000000000230+ 段，可重复执行先清后插\n\n---\n\n## 9. 运行与部署\n\n```bash\n# 后端（单体，推荐）\ncd cloudwaer\nmvn -pl cloudwaer-boot -am -DskipTests package\njava -jar cloudwaer-boot/target/cloudwaer-boot-1.0.0.jar        # :9999\n# 若 Flowable XSD 报错：加 --flowable.check-process-definitions=false\n\n# 前端\ncd cloudwaer-blog-ui && npm install && npm run dev              # :4100\ncd cloudwaer-ui && npm install && npm run dev                   # :4000\n```\n\n- 数据库初始化：`db/cloudwaer.sql` → `db/changes/` 相关增量 → `db/cloudwaer_blog.sql`（+ forum/like/settings/collection 等增量）\n- 近期关键增量（按依赖顺序）：\n\n```sql\n-- cloudwaer_blog\nSOURCE cloudwaer/db/changes/blog-site-settings-20261025.sql\nSOURCE cloudwaer/db/changes/blog-site-setting-json-20261026.sql\nSOURCE cloudwaer/db/changes/blog-article-import-20261026.sql\nSOURCE cloudwaer/db/changes/blog-collection-20261027.sql\nSOURCE cloudwaer/db/changes/blog-article-author-avatar-20261027.sql\n-- cloudwaer（权限收敛，可选）\nSOURCE cloudwaer/db/changes/blog-comment-permission-cleanup-20261027.sql\n```\n\n- 微服务模式：Nacos 配置各 serve 数据源，按 Authentication → Admin → Blog → Gateway 启动，网关 `/actuator/gateway/refresh`\n- 冒烟清单：注册 → 登录(blog) → 写文章（选合集/导入）→ 发布 → 匿名列表/详情（合集侧栏/Mermaid）→ 评论 → 点赞 → 个人中心管合集 → 管理端站点设置\n\n---\n\n## 10. 路线图\n\n| 阶段 | 内容 | 状态 |\n|------|------|------|\n| P0 | 模块骨架/独立库/注册/CRUD/公开读/网关路由/脚手架/首页详情 | ✅ |\n| P1 | 标签分类、评论两级+审核、申请博客主、行级隔离、话题、热度、点赞/搜索、管理页、RBAC 校验 | ✅ |\n| P2 | 标签云/热门/归档、图片上传、暗色/字号/布局、关于/友链、SEO | ✅（基础） |\n| P3 | 管理端卡片布局、审核中心、写作迁 blog-ui、登录 clientId | ✅ |\n| P3.x | 网站设置 + JSON 高度定制、文章导入、Mermaid、三栏布局、顶栏/个人中心、博客合集 | ✅ |\n| 后续 | 合集公开列表页/精选墙、评论实时通知、ES 搜索、单元测试、typecheck 清零、Flowable XSD 修复 | 见 TASKS.md |\n\n---\n\n## 11. 变更记录\n\n| 日期 | 说明 |\n|------|------|\n| 2026-10-25 | 初版：P0–P3 主线与登录 clientId 对齐 |\n| 2026-10-27 | 1.1：合集、站点设置 JSON、文章导入、Mermaid/三栏/顶栏/个人中心、评论权限收敛、author_avatar、增量 SQL 与路线图 |\n\n---\n\n*本文档由 cloudwaer-blog 项目实践沉淀；改动请同步 `TASKS.md` 与 `memory/MEMORY.md`。*', '/blog/uploads/cea7529cd76d4d16b7ae39d30a0b7d3d.jpg', 2100000000000000002, 2114746497058930689, NULL, 1992208936665325569, '这是测试账号', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', 1, 0, 0, 4, 0, 0, 1.556674262151302, '2026-10-26 23:40:41', NULL, '2026-10-26 23:39:43', NULL, '2026-10-27 03:29:22', NULL, 0);
INSERT INTO `blog_article` VALUES (2114791832003207169, 'B7??-????', NULL, '# ??`n????', NULL, NULL, NULL, NULL, 1, '管理员', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', 1, 0, 0, 0, 0, 0, 0, '2026-10-27 02:50:38', NULL, '2026-10-27 02:50:38', NULL, '2026-10-27 02:52:15', NULL, 0);

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
INSERT INTO `blog_article_tag` VALUES (2114136515846283266, 2114136515846283265, 2100000000000000011, '2026-10-25 07:26:39', NULL, '2026-10-25 07:26:39', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114136669290700801, 2114136656665845762, 2100000000000000012, '2026-10-25 07:27:15', NULL, '2026-10-25 07:27:15', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114417722274254851, 2114417722274254850, 2100000000000000014, '2026-10-26 02:04:04', NULL, '2026-10-26 02:04:04', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114417722274254852, 2114417722274254850, 2100000000000000013, '2026-10-26 02:04:04', NULL, '2026-10-26 02:04:04', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114417722274254853, 2114417722274254850, 2100000000000000011, '2026-10-26 02:04:04', NULL, '2026-10-26 02:04:04', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114417722274254854, 2114417722274254850, 2100000000000000012, '2026-10-26 02:04:04', NULL, '2026-10-26 02:04:04', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114417722274254855, 2114417722274254850, 2114417635842232321, '2026-10-26 02:04:04', NULL, '2026-10-26 02:04:04', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430621101248513, 2114430621076082689, 2100000000000000014, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430621101248514, 2114430621076082689, 2100000000000000013, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430621101248515, 2114430621076082689, 2100000000000000011, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430621101248516, 2114430621076082689, 2100000000000000012, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430621101248517, 2114430621076082689, 2114417635842232321, '2026-10-26 02:55:19', NULL, '2026-10-26 02:55:19', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430765892816899, 2114430765892816898, 2100000000000000014, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430765892816900, 2114430765892816898, 2100000000000000011, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430765892816901, 2114430765892816898, 2100000000000000013, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430765892816902, 2114430765892816898, 2100000000000000012, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);
INSERT INTO `blog_article_tag` VALUES (2114430765892816903, 2114430765892816898, 2114417635842232321, '2026-10-26 02:55:53', NULL, '2026-10-26 02:55:53', NULL, 1);

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
INSERT INTO `blog_author_apply` VALUES (2114732681529090049, 1992208936665325569, 'test', 'test', NULL, 1, 'admin', '2026-10-26 22:56:01', '审核中心通过并分配 BLOGGER', '2026-10-26 22:55:36', NULL, '2026-10-26 22:55:36', NULL, 1);

-- ----------------------------
-- Table structure for blog_blogger_status
-- ----------------------------
DROP TABLE IF EXISTS `blog_blogger_status`;
CREATE TABLE `blog_blogger_status`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（sys_user.id）',
  `suspend_until` datetime NULL DEFAULT NULL COMMENT '暂停截止时间；NULL=永久暂停',
  `suspend_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '暂停原因',
  `revoked` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0-暂停生效中，1-已解除',
  `handle_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最近操作人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_until`(`suspend_until` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客主暂停状态' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_blogger_status
-- ----------------------------
INSERT INTO `blog_blogger_status` VALUES (2114740801454129154, 1992208936665325569, '2026-10-29 00:00:00', '暂停', 0, 'admin', '2026-10-26 23:27:52', NULL, '2026-10-26 23:27:52', NULL, 1);

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
-- Table structure for blog_collection
-- ----------------------------
DROP TABLE IF EXISTS `blog_collection`;
CREATE TABLE `blog_collection`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合集名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合集简介',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图URL（预留）',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID（sys_user.id），合集仅本人可见可管',
  `author_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者昵称（冗余）',
  `article_count` int(11) NOT NULL DEFAULT 0 COMMENT '文章数（冗余，已发布）',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '合集展示排序（个人中心用，越大越靠前）',
  `is_featured` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否精选：0-否，1-是（预留）',
  `visibility` tinyint(4) NOT NULL DEFAULT 1 COMMENT '可见性：0-私密，1-公开（预留，默认公开）',
  `extra` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展JSON（预留）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author`(`author_id` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '博客合集表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_collection
-- ----------------------------
INSERT INTO `blog_collection` VALUES (2114417609275510785, '白皮书', 'cloudwaer项目合集', NULL, 1, 'admin', 3, 0, 0, 1, NULL, '2026-10-26 02:03:37', NULL, '2026-10-26 02:03:37', NULL, 1);
INSERT INTO `blog_collection` VALUES (2114746497058930689, '测试合集', NULL, NULL, 1992208936665325569, 'test', 1, 0, 0, 1, NULL, '2026-10-26 23:50:30', NULL, '2026-10-26 23:50:30', NULL, 1);

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
  `quality_score` double NOT NULL DEFAULT 0 COMMENT '质量分（离线落库，排序用；事件触发重算）',
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
INSERT INTO `blog_comment` VALUES (2100000000000000041, 2100000000000000021, 0, 0, 1, '管理员', NULL, '欢迎使用 CloudWaer 博客，这是第一条评论。', NULL, 1, 2.7273, 1, '127.0.0.1', '2026-10-24 00:00:00', 'system', '2026-10-24 00:00:00', 'system', 1);
INSERT INTO `blog_comment` VALUES (2114480718241517569, 2100000000000000021, 0, 0, 1, 'admin', NULL, '11111', NULL, 0, 4.8387, 1, '127.0.0.1', '2026-10-26 06:14:23', NULL, '2026-10-26 06:14:23', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114480843995140097, 2100000000000000021, 2114480718241517569, 2114480718241517569, 1, 'admin', NULL, '行了 我同意了', NULL, 0, 1.9355, 1, '127.0.0.1', '2026-10-26 06:14:53', NULL, '2026-10-26 06:14:53', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114480950496907265, 2100000000000000021, 2114480843995140097, 2114480718241517569, 1, 'admin', NULL, '试一试在多一层', NULL, 0, 0, 1, '127.0.0.1', '2026-10-26 06:15:18', NULL, '2026-10-26 06:15:18', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114482974055649281, 2100000000000000021, 2100000000000000041, 2100000000000000041, 1, 'admin', NULL, '行了 我知道了', NULL, 1, 1.9355, 1, '127.0.0.1', '2026-10-26 06:23:21', NULL, '2026-10-26 06:23:21', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114487632207761410, 2100000000000000021, 2114480843995140097, 2114480718241517569, 1, '管理员', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', '我看看😀🥰🎉💯🐱', '[\"/blog/uploads/6270bf8eeca14ca39d42e913d6e9e535.jpg\"]', 0, 0.9677, 1, '127.0.0.1', '2026-10-26 06:41:51', NULL, '2026-10-26 06:41:51', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114487892107808769, 2114430621076082689, 0, 0, 1, '管理员', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/641197b5ea264452bf564d9b5adea979.jpg', '测试一下评论😀😁😂', '[\"/blog/uploads/3c59f10beb6345b292e7d87942fda420.jpg\"]', 1, 1.9355, 1, '127.0.0.1', '2026-10-26 06:42:53', NULL, '2026-10-26 06:42:53', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114500444338450433, 2100000000000000021, 2114487632207761410, 2114480718241517569, 1992208936665325569, '测试', NULL, '我也看看', NULL, 1, 2.9032, 1, '127.0.0.1', '2026-10-26 07:32:46', NULL, '2026-10-26 07:32:46', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114500591688544257, 2100000000000000021, 2114500444338450433, 2114480718241517569, 1992208936665325569, '这是测试账号', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', '我在看看👎', '[\"/blog/uploads/3b0362a551f047c3ba4d28b9d625a28e.jpg\"]', 0, 0, 1, '127.0.0.1', '2026-10-26 07:33:21', NULL, '2026-10-26 07:33:21', NULL, 1);
INSERT INTO `blog_comment` VALUES (2114500866432233473, 2114430765892816898, 0, 0, 1992208936665325569, '这是测试账号', 'https://cloudwaer-ui.oss-cn-guangzhou.aliyuncs.com/uploads/2026/10/26/8c887bf5a46243cc91852d8852681890.jpg', '好', '[\"/blog/uploads/44fc13d97a74465ebe1ca39c257d2174.jpg\"]', 1, 1.9355, 1, '127.0.0.1', '2026-10-26 07:34:27', NULL, '2026-10-26 07:34:27', NULL, 1);

-- ----------------------------
-- Table structure for blog_like
-- ----------------------------
DROP TABLE IF EXISTS `blog_like`;
CREATE TABLE `blog_like`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标：1-文章，2-评论，3-话题，4-话题回复',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `user_id` bigint(20) NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_like`(`target_type` ASC, `target_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_like
-- ----------------------------
INSERT INTO `blog_like` VALUES (2114858897842036737, 1, 2114430765892816898, 1, '2026-10-27 07:17:08', NULL, '2026-10-27 07:17:08', NULL, 1);
INSERT INTO `blog_like` VALUES (2114858905798631425, 2, 2114500866432233473, 1, '2026-10-27 07:17:10', NULL, '2026-10-27 07:17:10', NULL, 1);
INSERT INTO `blog_like` VALUES (2115064797492076545, 1, 2114430621076082689, 1992208936665325569, '2026-10-27 20:55:18', NULL, '2026-10-27 20:55:18', NULL, 1);
INSERT INTO `blog_like` VALUES (2115064832992665602, 1, 2114430765892816898, 1992208936665325569, '2026-10-27 20:55:27', NULL, '2026-10-27 20:55:27', NULL, 1);
INSERT INTO `blog_like` VALUES (2115064865838260226, 1, 2100000000000000021, 1992208936665325569, '2026-10-27 20:55:35', NULL, '2026-10-27 20:55:35', NULL, 1);
INSERT INTO `blog_like` VALUES (2115069263486312449, 2, 2114487892107808769, 1, '2026-10-27 21:13:03', NULL, '2026-10-27 21:13:03', NULL, 1);
INSERT INTO `blog_like` VALUES (2115069317504753666, 1, 2114430621076082689, 1, '2026-10-27 21:13:16', NULL, '2026-10-27 21:13:16', NULL, 1);
INSERT INTO `blog_like` VALUES (2115105254163046402, 2, 2114482974055649281, 1, '2026-10-27 23:36:04', NULL, '2026-10-27 23:36:04', NULL, 1);
INSERT INTO `blog_like` VALUES (2115105257430409218, 2, 2100000000000000041, 1, '2026-10-27 23:36:05', NULL, '2026-10-27 23:36:05', NULL, 1);
INSERT INTO `blog_like` VALUES (2115105263029805057, 2, 2114500444338450433, 1, '2026-10-27 23:36:06', NULL, '2026-10-27 23:36:06', NULL, 1);
INSERT INTO `blog_like` VALUES (2115105288938020866, 1, 2100000000000000021, 1, '2026-10-27 23:36:12', NULL, '2026-10-27 23:36:12', NULL, 1);

-- ----------------------------
-- Table structure for blog_site_setting
-- ----------------------------
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

-- ----------------------------
-- Records of blog_site_setting
-- ----------------------------
INSERT INTO `blog_site_setting` VALUES (2100000000000000051, 'siteTitle', '云记', '站点标题', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000052, 'siteDescription', '', '站点副标题/描述', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000053, 'favicon', '', '站点图标 URL（favicon/icon）', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000054, 'enableSearch', '1', '是否开启搜索：0-关闭，1-开启', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000055, 'layout', 'default', '网站布局：default / compact / magazine', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2100000000000000056, 'footerText', '', '页脚文案', '2026-10-25 00:00:00', 'system', '2026-10-25 00:00:00', 'system', 1);
INSERT INTO `blog_site_setting` VALUES (2114094059121422338, 'nav', '[{\"key\":\"home\",\"label\":\"首页\",\"path\":\"/\",\"visible\":true,\"order\":1},{\"key\":\"forum\",\"label\":\"微论坛\",\"path\":\"/forum\",\"visible\":true,\"order\":2},{\"key\":\"archive\",\"label\":\"归档\",\"path\":\"/archive\",\"visible\":true,\"order\":3},{\"key\":\"about\",\"label\":\"关于\",\"path\":\"/about\",\"visible\":true,\"order\":4},{\"key\":\"write\",\"label\":\"写文章\",\"path\":\"/write\",\"visible\":true,\"order\":5}]', '导航菜单 JSON 数组（key/label/path/visible/order）', '2026-10-25 04:37:56', NULL, '2026-10-25 04:37:56', NULL, 1);
INSERT INTO `blog_site_setting` VALUES (2114094059121422339, 'pages', '{\"about\":{\"title\":\"关于云记\",\"content\":\"云记博客是基于 cloudwaer-blog 的多作者技术写作与轻论坛平台，记录工程实践、踩坑与思考。\"}}', '独立页面文案 JSON（如 about.title/about.content）', '2026-10-25 04:37:56', NULL, '2026-10-25 04:37:56', NULL, 1);
INSERT INTO `blog_site_setting` VALUES (2114094059121422340, 'articleLayout', '{\"tocPosition\":\"right\",\"modules\":[{\"key\":\"author\",\"zone\":\"left\",\"order\":1,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"readingInfo\",\"zone\":\"left\",\"order\":2,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"actions\",\"zone\":\"left\",\"order\":4,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"related\",\"zone\":\"left\",\"order\":3,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"toc\",\"zone\":\"right\",\"order\":1,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"tags\",\"zone\":\"right\",\"order\":2,\"visible\":true,\"title\":null,\"extra\":null}]}', '文章详情布局 JSON（tocPosition: left/right/off）', '2026-10-25 04:37:56', NULL, '2026-10-25 04:37:56', NULL, 1);
INSERT INTO `blog_site_setting` VALUES (2114094059121422341, 'homeLayout', '{\"showFeatured\":true,\"showTagCloud\":true,\"showHot\":true,\"modules\":[{\"key\":\"siteProfile\",\"zone\":\"left\",\"order\":1,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"nav\",\"zone\":\"left\",\"order\":2,\"visible\":false,\"title\":null,\"extra\":null},{\"key\":\"quickLinks\",\"zone\":\"left\",\"order\":3,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"featured\",\"zone\":\"main\",\"order\":1,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"articleList\",\"zone\":\"main\",\"order\":2,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"categoryList\",\"zone\":\"right\",\"order\":1,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"tagCloud\",\"zone\":\"right\",\"order\":3,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"hotArticles\",\"zone\":\"right\",\"order\":2,\"visible\":true,\"title\":null,\"extra\":null},{\"key\":\"about\",\"zone\":\"right\",\"order\":4,\"visible\":true,\"title\":null,\"extra\":null}]}', '首页模块开关 JSON（showFeatured/showTagCloud/showHot）', '2026-10-25 04:37:56', NULL, '2026-10-25 04:37:56', NULL, 1);
INSERT INTO `blog_site_setting` VALUES (2114754407268810753, 'requireArticleAudit', '0', '非管理员发布文章是否需审核：0-免审，1-需审核', '2026-10-27 00:21:56', NULL, '2026-10-27 00:21:56', NULL, 1);

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
INSERT INTO `blog_tag` VALUES (2114417635842232321, '微服务', NULL, '2026-10-26 02:03:43', NULL, '2026-10-26 02:03:43', NULL, 1);
INSERT INTO `blog_tag` VALUES (2114746528314884098, '测试', NULL, '2026-10-26 23:50:37', NULL, '2026-10-26 23:50:37', NULL, 1);

-- ----------------------------
-- Table structure for blog_topic
-- ----------------------------
DROP TABLE IF EXISTS `blog_topic`;
CREATE TABLE `blog_topic`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正文',
  `user_id` bigint(20) NOT NULL COMMENT '发帖用户ID',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `view_count` int(11) NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` int(11) NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_top` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_essence` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否加精：0-否，1-是',
  `last_reply_time` datetime NULL DEFAULT NULL COMMENT '最后回复时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_top_last`(`is_top` ASC, `last_reply_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '微论坛话题' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_topic
-- ----------------------------

-- ----------------------------
-- Table structure for blog_topic_reply
-- ----------------------------
DROP TABLE IF EXISTS `blog_topic_reply`;
CREATE TABLE `blog_topic_reply`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `topic_id` bigint(20) NOT NULL COMMENT '话题ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父回复ID，0-顶级',
  `root_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '根回复ID，0-顶级',
  `user_id` bigint(20) NOT NULL COMMENT '回复用户ID',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回复内容',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-删除，1-有效，2-无效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_topic`(`topic_id` ASC) USING BTREE,
  INDEX `idx_root`(`root_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '微论坛话题回复' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_topic_reply
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
