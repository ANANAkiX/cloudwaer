# Cloudwaer Codegen 代码生成模块

## 模块简介
- 基于数据库表结构快速生成后端实体、Mapper、Service、Controller 以及前端 API、页面的脚手架代码。
- 典型用途：快速搭建 CRUD 能力，保持与 Cloudwaer 项目约定的一致性（包结构、返回体、分页模型等）。

## 子模块
- cloudwaer-codegen-api：对外 DTO、Feign 接口（如有），供其他服务调用。
- cloudwaer-codegen-serve：代码生成服务实现与控制台/接口。

## 环境准备
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 数据源配置
- 在 `cloudwaer-codegen-serve` 的 `application.yml` 或 Nacos 配置里，设置用于扫描元数据的数据库连接：
```
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/your_db?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```
- 建议使用与目标业务库一致的数据库作为元数据源（包含要生成的表）。

## 使用方法
1) 启动服务
- 在 `cloudwaer-codegen-serve` 目录执行：
```
mvn spring-boot:run
```
- 或从根目录构建后以 jar 方式运行。

2) 访问控制台/接口
- 控制台（如提供 Web 页面）：`http://localhost:PORT/codegen`
- 或通过接口提交生成任务（具体接口以实际实现为准，例如）：
```
POST /codegen/generate
{
  "tables": ["t_user", "t_role"],
  "packageBase": "com.cloudwaer.demo",
  "output": {
    "backend": "D:/work/generated/backend",
    "frontend": "D:/work/generated/frontend"
  },
  "options": {
    "lombok": true,
    "restStyle": true,
    "logicDeleteField": "deleted",
    "swagger": true
  }
}
```

3) 检查生成结果
- 后端：entity/mapper/service/controller 等按约定包路径输出。
- 前端：接口 `src/api/*.ts` 与页面 `src/views/**` 脚手架。

## 约定与输出规范
- 后端返回结构使用项目统一的 `Result{ code, message, data }`。
- 分页模型字段：`total/current/size/pages`（前端已做数字类型处理）。
- Controller 命名：`XxxController`，路径遵循小写短横线或驼峰转短横线。
- 逻辑删除字段（如开启）：`deleted` 或以配置为准。

## 注意事项
- 请先确认目标表是否具备主键、非空约束、备注注释，以便生成更完整的实体与注释。
- 生成前端页面后，需在后端路由/权限中补充菜单与权限码；前端动态路由才能显示。
- 生成代码为脚手架，需结合业务手动完善校验、DTO、权限控制等。
- 避免直接覆盖已有业务代码：建议将输出目录指向独立文件夹，再人工拷贝合并。
- 如使用 Nacos 统一配置，请在 codegen 服务的配置中加入数据源与输出目录、模板选项等可调参数。

## 常见问题
- 无法连接数据库：检查 `spring.datasource` 配置与网络权限。
- 生成包结构不符合预期：检查 `packageBase` 与模块命名约定。
- 前端类型不匹配：确保后端字段与前端类型推断一致，或在模板中自定义类型映射。

## 后续计划（可选）
- 接入模板引擎自定义（如 Freemarker/Velocity/Beetl）
- 一键注册菜单/权限到 Admin 服务
- 多数据库方言支持（PostgreSQL、Oracle 等）
