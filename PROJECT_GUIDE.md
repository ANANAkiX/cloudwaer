# Cloudwaer 项目启动指南

## 项目结构

```
cloudwaer/
├── cloudwaer-common/              # 公共模块
├── cloudwaer-authentication/      # 认证授权服务
├── cloudwaer-gateway/             # API网关
├── cloudwaer-admin/               # 用户权限管理
│   ├── cloudwaer-admin-api/       # API接口
│   └── cloudwaer-admin-serve/     # 业务逻辑层
├── cloudwaer-integration-serve/   # 集成服务
├── cloudwaer-ui/                  # 前端项目
└── sql/                           # 数据库脚本
```

## 环境准备

1. **JDK 17+**
2. **Maven 3.6+**
3. **Node.js 16+**
4. **MySQL 8.0+**
5. **Redis 6.0+**
6. **Nacos 2.0+** (本地启动)

## 启动步骤

### 1. 启动Nacos

```bash
# Windows
startup.cmd -m standalone

# Linux/Mac
sh startup.sh -m standalone
```

访问 http://localhost:8848/nacos，默认用户名/密码：nacos/nacos

### 2. 配置Nacos

在Nacos配置中心创建以下配置：

- **命名空间**: dev
- **配置Data ID**: application-common.yml
- **Group**: DEFAULT_GROUP
- **配置格式**: YAML

将 `nacos-config-example/application-common.yml` 的内容上传到Nacos。

### 3. 初始化数据库

执行 `sql/init.sql` 脚本创建数据库和表结构。

### 4. 启动后端服务

按以下顺序启动服务：

1. **cloudwaer-authentication** (端口: 8083)
2. **cloudwaer-admin-serve** (端口: 8081)
3. **cloudwaer-gateway** (端口: 8080)
4. **cloudwaer-integration-serve** (端口: 8082)

### 5. 启动前端

```bash
cd cloudwaer-ui
npm install
npm run dev
```

访问 http://localhost:3000

## 注意事项

### POM文件修复

所有POM文件中的 `<n>` 标签需要手动修复为 `<name>`：

```xml
<!-- 错误 -->
<n>module-name</n>

<!-- 正确 -->
<name>module-name</name>
```

### 数据库配置

修改各服务的 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cloudwaer?...
    username: root
    password: your_password
```

### Redis配置

确保Redis服务已启动，默认端口6379。

### 密码加密

默认用户密码在数据库中是明文，实际使用时需要使用BCrypt加密：

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encodedPassword = encoder.encode("admin123");
```

## 功能说明

### 认证授权

- 登录接口: POST /auth/login
- 登出接口: POST /auth/logout
- Token验证: 网关自动验证JWT Token

### 用户权限管理

- 用户管理: /admin/user/**
- 角色管理: /admin/role/**
- 权限管理: /admin/permission/**
- 路由管理: /admin/route/**

### 前端路由

前端路由由后端动态返回，根据用户权限自动生成菜单和路由。

## 开发建议

1. **配置管理**: 所有配置统一使用Nacos管理
2. **服务调用**: 服务间调用使用Feign
3. **异常处理**: 使用统一的异常处理机制
4. **日志管理**: 建议集成分布式日志追踪
5. **代码规范**: 保持代码低耦合，高内聚

## 待完善功能

1. 完善权限管理的CRUD接口
2. 实现动态路由的完整逻辑
3. 添加权限验证拦截器
4. 完善前端路由动态加载
5. 添加单元测试
6. 集成分布式日志追踪（Sleuth/Zipkin）




