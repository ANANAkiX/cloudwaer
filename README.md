# Cloudwaer 微服务项目

## 项目简介

Cloudwaer 是一个基于 Spring Cloud 的微服务架构项目，采用前后端分离设计，内置统一认证、权限、网关、动态路由、字典、集成等能力。

前端仓库：`cloudwaer-ui`（及可选 `cloudwaer-blog-ui` 公开展示端）。

## 技术栈

### 后端
- Java 17 / Spring Boot 3.2.x
- Spring Cloud 2023.x / Spring Cloud Alibaba
- JWT（自研 TokenService + Redis）
- Spring Cloud Gateway
- MyBatis-Plus
- Nacos（注册/配置，微服务模式）
- MySQL、Redis

### 前端
- Vue 3 + Vite + TypeScript
- Element Plus、Pinia、Vue Router、Axios

## 模块结构

```
cloudwaer/
├─ cloudwaer-common/           # 公共（core / redis / security / log / swagger）
├─ cloudwaer-authentication/   # 认证服务
├─ cloudwaer-gateway/          # API 网关
├─ cloudwaer-admin/            # 用户/角色/权限/字典/网关路由
├─ cloudwaer-integration-serve/# codegen + flowable
├─ cloudwaer-blog/             # 博客（api + serve，库 cloudwaer_blog）
├─ cloudwaer-boot/             # 单体启动（本地默认，多数据源）
└─ db/                         # 全量 SQL + db/changes/ 增量脚本
```

## 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2+（仅微服务模式）

## 端口约定（默认）

| 组件 | 端口 |
|------|------|
| **cloudwaer-boot（单体，本地推荐）** | **9999** |
| cloudwaer-ui 前端 | 4000 |
| cloudwaer-blog-ui 前台 | 4100 |
| Nacos | 8848 |

微服务分拆时端口以各模块 `bootstrap.yml` / Nacos 配置为准。  
**不要**把 Gateway 写成 4100（4100 是博客前台开发端口）。

## 数据库

全量脚本在 `db/`：

| 文件 | 库 |
|------|-----|
| `db/cloudwaer.sql` | `cloudwaer`（sys_*） |
| `db/cloudwaer_blog.sql` | `cloudwaer_blog` |
| `db/cloudwaer_flowable.sql` | `cloudwaer_flowable` |
| `db/cloudwaer_codegen_config.sql` | `cloudwaer_codegen_config` |
| `db/cloudwaer_config.sql` | Nacos 配置库 |

增量变更脚本放 `db/changes/`，命名 `作用-时间.sql`（见仓库 `AGENTS.md`）。

**不存在** `sql/init.sql`；**不存在**根目录 `nacos-config-example/`（Nacos 配置请用 `db/cloudwaer_config.sql` 或控制台手工导入）。

## 准备与启动

### 单体（本地默认）
1. 导入上述 5 个 SQL（至少 `cloudwaer` + `cloudwaer_blog` + `cloudwaer_flowable`）。
2. 启动 Redis。
3. 启动 `cloudwaer-boot`（读 `application.yml` 多数据源，无 Nacos）。
4. `cd cloudwaer-ui && npm install && npm run dev` → http://localhost:4000

### 微服务
1. Nacos + `cloudwaer_config.sql`
2. 按序：Authentication → Admin-Serve → **Gateway（最后）**
3. 网关动态路由来自 `/admin/gateway-route/list`（匿名）

## 构建与校验

```bash
# 后端
mvn -DskipTests clean package          # validate 含 spring-javaformat
bash scripts/check-rbac.sh             # RBAC 注解/种子/前端码比对（尽力而为）

# 前端
cd ../cloudwaer-ui && npm run build
npm run typecheck                      # vue-tsc@2；历史 Flowable 大页已 exclude，仍有遗留错误为非阻断
```

## 权限与RBAC
- 用户-角色-权限多对多；`@PermissionCode` ↔ `db/cloudwaer.sql` 种子 ↔ 前端 `permission.ts` 三方一致。
- 码风格：`模块:资源:动作`。
- 角色变更会刷新在线 Token 权限（无需强制重登）。

## 关键接口
- POST `/auth/login` / `/auth/logout`
- GET  `/auth/token/valid`（前端定时轮询）
