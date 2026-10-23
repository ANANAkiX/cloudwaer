# Cloudwaer 项目启动指南

> 详细约定见仓库根目录及工作区 `AGENTS.md`。端口与路径以本节「快速路径」为准。

## 项目结构

```
cloudwaer/
├── cloudwaer-common/              # 公共模块
├── cloudwaer-authentication/      # 认证授权服务
├── cloudwaer-gateway/             # API 网关（微服务模式）
├── cloudwaer-admin/               # 用户权限管理
├── cloudwaer-integration-serve/   # codegen + flowable
├── cloudwaer-blog/                # 博客 api + serve
├── cloudwaer-boot/                # 单体启动（本地默认）
├── cloudwaer-ui/                  # 后台前端（工作区同级目录）
├── cloudwaer-blog-ui/             # 博客前台（工作区同级目录）
└── db/                            # 全量 SQL；增量见 db/changes/
```

## 环境准备

1. **JDK 17+**
2. **Maven 3.6+**
3. **Node.js 18+**
4. **MySQL 8.0+**
5. **Redis 6.0+**
6. **Nacos 2.2+**（仅微服务模式需要）

## 快速路径（推荐：单体 boot）

适合本地开发，**不需要 Nacos**。

### 1. 初始化数据库

```bash
# 依次导入（按需）
mysql -uroot -p < db/cloudwaer.sql
mysql -uroot -p < db/cloudwaer_flowable.sql
mysql -uroot -p < db/cloudwaer_codegen_config.sql
mysql -uroot -p < db/cloudwaer_blog.sql
# Nacos 用：mysql -uroot -p < db/cloudwaer_config.sql
```

不要使用不存在的 `sql/init.sql`。

### 2. 配置

编辑 `cloudwaer-boot/src/main/resources/application.yml` 中的 MySQL/Redis 连接。

### 3. 启动后端

启动类：`CloudwaerBootApplication`，默认 **http://localhost:9999**

### 4. 启动前端

```bash
cd ../cloudwaer-ui
npm install
npm run dev    # http://localhost:4000
```

博客前台（可选）：

```bash
cd ../cloudwaer-blog-ui
npm install && npm run dev   # http://localhost:4100
```

## 微服务模式（可选）

1. 启动 Nacos：`startup.cmd -m standalone` → http://localhost:8848/nacos  
2. 用 `db/cloudwaer_config.sql` 初始化 Nacos 存储，或在控制台导入公共配置。  
3. 启动顺序：**Authentication → Admin-Serve → Gateway（必须最后）**  
4. 动态路由：`/admin/gateway-route/list`（网关匿名拉取）

## 校验

```bash
mvn -DskipTests clean package
bash scripts/check-rbac.sh
```

## 注意事项

- RBAC 三方一致：注解码 ↔ `db/cloudwaer.sql` ↔ `cloudwaer-ui/src/config/permission.ts`
- 增量 SQL 放 `db/changes/`，命名 `作用-时间.sql`
- 前端 `npm run typecheck`（vue-tsc 2）；Flowable 设计器大页暂 exclude，属已知遗留

## 功能入口

- 认证：`/auth/login`、`/auth/logout`、`/auth/token/valid`
- 管理：`/admin/user|role|permission|dict|gateway-route/**`
- 流程：`/flowable/**`
- 博客管理：`/blog/**`（文章/分类/标签/评论/申请/论坛）
- 博客公开：`/blog/open/**`
