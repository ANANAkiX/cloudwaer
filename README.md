# Cloudwaer 微服务项目

## 项目简介

Cloudwaer 是一个基于 Spring Cloud 的微服务架构项目，采用前后端分离设计，内置统一认证、权限、网关、动态路由、字典、集成等能力。

## 技术栈

### 后端
- Spring Boot 3.2.x
- Spring Cloud 2023.x
- Spring Cloud Alibaba 2022.x
- Spring Authorization Server（JWT）
- Spring Cloud Gateway
- MyBatis-Plus
- Nacos（注册/配置）
- MySQL、Redis

### 前端
- Vue 3 + Vite + TypeScript
- Element Plus、Pinia、Vue Router、Axios

## 模块结构

```
cloudwaer/
├─ cloudwaer-common/                 # 公共模块（核心工具、JWT、TokenService 等）
├─ cloudwaer-authentication/         # 认证服务（登录/登出、Token 校验）
├─ cloudwaer-gateway/                # API 网关（统一转发、路由、过滤器）
├─ cloudwaer-admin/                  # 后台管理
│  ├─ cloudwaer-admin-api/           # 对外 API 接口定义（Feign、DTO）
│  └─ cloudwaer-admin-serve/         # 业务服务实现
├─ cloudwaer-integration-serve/      # 集成服务
└─ cloudwaer-ui/                     # 前端工程
```

## 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2+

## 端口约定（默认）
- Nacos: 8848
- Gateway: 4100
- Authentication: 4101
- Admin-Serve: 4102

实际端口请以各模块 `application.yml` 或 Nacos 配置为准。

## 准备与启动

- 数据库与Redis
  - 创建数据库，导入初始化 SQL（用户/角色/权限/字典 等）。
  - 启动 Redis。
- 启动 Nacos
  - 从官方下载并解压 Nacos（https://nacos.io/）。
  - 执行数据库脚本 `cloudwaer_config.sql` 打开nacos的application.properties 初始化 Nacos 配置Nacos存储数据的MySql地址用户名密码等。
  - 启动 Nacos 服务。
  - 备选方案：不导入SQL文件 直接启动nacos 在nacos终端中 导入nacos-config下面的配置文件以便本地快速启动。
- 构建与运行
  - 根目录执行：`mvn -U -T 1C -DskipTests clean package`
  - 按顺序启动服务：Authentication -> Admin-Serve -> Gateway
  - 注意 `Gateway`服务必须是等待 `Admin` 服务启动之后才能启动 (防止动态网关无法加载导致请求404)
  - 前端见 `cloudwaer-ui/README.md`

## 关键接口
- 认证
  - POST `/auth/login`            登录，返回 `{ token: string }`
  - POST `/auth/logout`           登出，服务端删除 Token
  - GET  `/auth/token/valid`      检查当前请求 Token 是否有效（前端定时轮询）

## 网关与动态路由
- 推荐通过后端维护路由，前端网关页面可新增/编辑/刷新。
- 常见模板：外链代理（Nacos/Swagger）、基础转发。

## 权限与RBAC
- 基于角色的访问控制：用户-角色-权限 多对多。
- 菜单/路由与权限码一体化，前端按权限动态生成路由。

## 常见问题
- 登录后偶发不跳转：已在前端通过 `toLogin()` 增强（replace + 顶层跳转兜底）。
- Token 过期未感知：前端已接入每分钟定时校验 `/auth/token/valid`，并在 401 时强制跳转登录。

## 开发建议
- 统一从 Nacos 管理配置，区分 `dev/test/prod` 环境。
- API 需遵循统一返回结构 `Result{ code, message, data }`，分页字段 `total/current/size/pages` 返回数字类型（后端已提供转换工具）。

