/*
 Navicat Premium Dump SQL

 Source Server         : 本地my_sql
 Source Server Type    : MySQL
 Source Server Version : 80018 (8.0.18)
 Source Host           : localhost:3306
 Source Schema         : cloudwaer_config

 Target Server Type    : MySQL
 Target Server Version : 80018 (8.0.18)
 File Encoding         : 65001

 Date: 08/01/2026 23:29:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'configuration description',
  `c_use` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'configuration usage',
  `effect` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置生效的描述',
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '配置的类型',
  `c_schema` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '配置的模式',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (1, 'application-common.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/cloudwaer?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai\n    username: root\n    password: root\n  redis:\n    host: localhost\n    port: 6379\n    database: 0\n\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  type-aliases-package: com.cloudwaer.**.entity\n  configuration:\n    map-underscore-to-camel-case: true', '15ede9d96351caea5ca46bbce9396bfe', '2025-11-20 21:38:12', '2025-11-20 21:38:12', 'nacos', '0:0:0:0:0:0:0:1', '', 'public', '所有服务都会默认加载这个配置文件', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (2, 'application-cloudwaer-admin-serve-dev.yml', 'DEFAULT_GROUP', '# Admin管理服务配置 (dev环境)\n# Data ID: application-cloudwaer-admin-serve-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4102\nspringdoc:\n  api-docs:\n    path: /v3/api-docs\n  swagger-ui:\n    path: /swagger-ui.html\n    enabled: true\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: Admin管理服务API\n    version: 1.0\n    description: 用户、路由、权限管理服务的API文档\n  # API扫描器配置\n  api-scanner:\n    enabled: true\n    # 服务ID（必填，唯一标识，通常是服务名称）\n    service-id: cloudwaer-admin-serve\n    # 排除的方法名称（不扫描这些方法）\n    exclude-methods:\n      - error\n      - health\n    # 排除的路径模式（支持Ant路径匹配）\n    exclude-paths:\n      - /actuator/**\n      - /error\n      - /swagger-ui/**\n      - /v3/api-docs/**\n    # 扫描的基础包路径（如果不配置，则扫描所有包）\n    base-packages:\n      - com.cloudwaer.admin', '89c100afec11c88baf208d34a48755cf', '2025-11-20 21:38:12', '2025-11-22 04:23:40', 'nacos', '0:0:0:0:0:0:0:1', '', 'public', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (3, 'application-cloudwaer-authentication-dev.yml', 'DEFAULT_GROUP', '# 认证授权服务配置 (dev环境)\n# Data ID: application-cloudwaer-authentication-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4101\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"\n\nspringdoc:\n  api-docs:\n    path: /v3/api-docs\n  swagger-ui:\n    path: /swagger-ui.html\n    enabled: true\n\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: 认证授权服务API\n    version: 1.0\n    description: 认证授权服务的API文档\n\n', '9a04b5aae9e02f5938c54deee0dfc6e7', '2025-11-20 21:38:12', '2026-01-07 07:37:37', 'nacos', '0:0:0:0:0:0:0:1', '', 'public', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (4, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: false      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', '7fe65983ff2a36204b4358b625f02626', '2025-11-20 21:38:12', '2026-01-06 07:13:56', 'nacos', '0:0:0:0:0:0:0:1', '', 'public', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (5, 'application-cloudwaer-codegen-serve-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/cloudwaer_codegen_config?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai\n    username: root\n    password: root\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  type-aliases-package: com.cloudwaer.**.entity\n  configuration:\n    map-underscore-to-camel-case: true\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: 代码生成管理服务API\n    version: 1.0\n    description: 代码生成服务的API文档\n  # API扫描器配置\n  api-scanner:\n    enabled: true\n    # 服务ID（必填，唯一标识，通常是服务名称）\n    service-id: cloudwaer-codegen-serve\n    exclude-methods:\n      - error\n      - health\n    # 排除的路径模式（支持Ant路径匹配）\n    exclude-paths:\n      - /actuator/**\n      - /error\n      - /swagger-ui/**\n      - /v3/api-docs/**\n    # 扫描的基础包路径（如果不配置，则扫描所有包）\n    base-packages:\n      - com.cloudwaer.codegen\n#代码生成服务运行端口\nserver:\n  port: 4333', '020dfe2d1610cada4f44df9ef1aa3937', '2025-11-21 00:17:50', '2025-11-21 00:54:19', 'nacos', '0:0:0:0:0:0:0:1', '', 'public', '所有服务都会默认加载这个配置文件', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (6, 'application-cloudwaer-codegen-serve-dev.yml', 'DEFAULT_GROUP', 'spring:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/cloudwaer_codegen_config?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai\n    username: root\n    password: root\nmybatis-plus:\n  mapper-locations: classpath*:mapper/**/*.xml\n  type-aliases-package: com.cloudwaer.**.entity\n  configuration:\n    map-underscore-to-camel-case: true\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: 代码生成管理服务API\n    version: 1.0\n    description: 代码生成服务的API文档\n  # API扫描器配置\n  api-scanner:\n    enabled: true\n    # 服务ID（必填，唯一标识，通常是服务名称）\n    service-id: cloudwaer-codegen-serve\n    exclude-methods:\n      - error\n      - health\n    # 排除的路径模式（支持Ant路径匹配）\n    exclude-paths:\n      - /actuator/**\n      - /error\n      - /swagger-ui/**\n      - /v3/api-docs/**\n    # 扫描的基础包路径（如果不配置，则扫描所有包）\n    base-packages:\n      - com.cloudwaer.codegen\n#代码生成服务运行端口\nserver:\n  port: 4333', '020dfe2d1610cada4f44df9ef1aa3937', '2025-11-21 00:18:14', '2025-11-21 00:54:19', 'nacos_namespace_migrate', '0:0:0:0:0:0:0:1', '', '', '所有服务都会默认加载这个配置文件', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (7, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: false      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', '7fe65983ff2a36204b4358b625f02626', '2025-11-21 21:36:31', '2026-01-06 07:13:56', 'nacos_namespace_migrate', '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (8, 'application-cloudwaer-admin-serve-dev.yml', 'DEFAULT_GROUP', '# Admin管理服务配置 (dev环境)\n# Data ID: application-cloudwaer-admin-serve-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4102\nspringdoc:\n  api-docs:\n    path: /v3/api-docs\n  swagger-ui:\n    path: /swagger-ui.html\n    enabled: true\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: Admin管理服务API\n    version: 1.0\n    description: 用户、路由、权限管理服务的API文档\n  # API扫描器配置\n  api-scanner:\n    enabled: true\n    # 服务ID（必填，唯一标识，通常是服务名称）\n    service-id: cloudwaer-admin-serve\n    # 排除的方法名称（不扫描这些方法）\n    exclude-methods:\n      - error\n      - health\n    # 排除的路径模式（支持Ant路径匹配）\n    exclude-paths:\n      - /actuator/**\n      - /error\n      - /swagger-ui/**\n      - /v3/api-docs/**\n    # 扫描的基础包路径（如果不配置，则扫描所有包）\n    base-packages:\n      - com.cloudwaer.admin', '89c100afec11c88baf208d34a48755cf', '2025-11-22 04:17:06', '2025-11-22 04:23:40', 'nacos_namespace_migrate', '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (9, 'application-cloudwaer-authentication-dev.yml', 'DEFAULT_GROUP', '# 认证授权服务配置 (dev环境)\n# Data ID: application-cloudwaer-authentication-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4101\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"\n\nspringdoc:\n  api-docs:\n    path: /v3/api-docs\n  swagger-ui:\n    path: /swagger-ui.html\n    enabled: true\n\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: 认证授权服务API\n    version: 1.0\n    description: 认证授权服务的API文档\n\n', '9a04b5aae9e02f5938c54deee0dfc6e7', '2026-01-07 07:37:37', '2026-01-07 07:37:37', 'nacos_namespace_migrate', '0:0:0:0:0:0:0:1', '', '', '', NULL, NULL, 'yaml', NULL, '');

-- ----------------------------
-- Table structure for config_info_gray
-- ----------------------------
DROP TABLE IF EXISTS `config_info_gray`;
CREATE TABLE `config_info_gray`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'group_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'md5',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'src_user',
  `src_ip` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'src_ip',
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
  `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'app_name',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'tenant_id',
  `gray_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'gray_name',
  `gray_rule` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'gray_rule',
  `encrypted_data_key` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfogray_datagrouptenantgray`(`data_id` ASC, `group_id` ASC, `tenant_id` ASC, `gray_name` ASC) USING BTREE,
  INDEX `idx_dataid_gmt_modified`(`data_id` ASC, `gmt_modified` ASC) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'config_info_gray' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_info_gray
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id` ASC, `tag_name` ASC, `tag_type` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_tag_relation' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT 'id',
  `nid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `op_type` char(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'operation type',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密钥',
  `publish_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT 'formal' COMMENT 'publish type gray or formal',
  `gray_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'gray name',
  `ext_info` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'ext info',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create` ASC) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified` ASC) USING BTREE,
  INDEX `idx_did`(`data_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '多租户改造' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (4, 22, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4100\n\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: true      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', 'd229da629d19c5b613471a2f470e0767', '2026-01-06 07:07:08', '2026-01-05 23:07:09', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 23, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\n\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: true      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', '2b2dcbc54de6f61088627c848b3c59b6', '2026-01-06 07:07:59', '2026-01-05 23:08:00', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 24, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: true      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', 'c276e5f236b70e2bf00592f714852519', '2026-01-06 07:12:34', '2026-01-05 23:12:35', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 25, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: false      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', '7fe65983ff2a36204b4358b625f02626', '2026-01-06 07:12:52', '2026-01-05 23:12:52', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 26, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: true      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', 'c276e5f236b70e2bf00592f714852519', '2026-01-06 07:13:05', '2026-01-05 23:13:06', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 27, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: false      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', '7fe65983ff2a36204b4358b625f02626', '2026-01-06 07:13:38', '2026-01-05 23:13:39', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (4, 28, 'application-cloudwaer-gateway-dev.yml', 'DEFAULT_GROUP', '', '# 网关服务配置 (dev环境)\n# Data ID: application-cloudwaer-gateway-dev.yml\n# Group: DEFAULT_GROUP\nserver:\n  port: 4100\nspring:\n  main:\n    web-application-type: reactive\n  autoconfigure:\n    exclude:\n      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration\ncloudwaer:\n  captcha:\n    enabled: true      # 验证码开关\n    length: 4          # 位数\n    width: 120\n    height: 40\n    expire-seconds: 120\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: true\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"', 'c276e5f236b70e2bf00592f714852519', '2026-01-06 07:13:56', '2026-01-05 23:13:56', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');
INSERT INTO `his_config_info` VALUES (3, 29, 'application-cloudwaer-authentication-dev.yml', 'DEFAULT_GROUP', '', '# 认证授权服务配置 (dev环境)\n# Data ID: application-cloudwaer-authentication-dev.yml\n# Group: DEFAULT_GROUP\n\nserver:\n  port: 4101\njwt:\n  secret: cloudwaer-secret-key-for-jwt-token-generation-minimum-256-bits\n  expiration: 86400000\n  allow-multiple-login: false\n  redis-key-prefix: \"cloudwaer:jwt:token:\"\n  user-token-list-key-prefix: \"cloudwaer:jwt:user:tokens:\"\n\nspringdoc:\n  api-docs:\n    path: /v3/api-docs\n  swagger-ui:\n    path: /swagger-ui.html\n    enabled: true\n\n# Swagger配置\ncloudwaer:\n  swagger:\n    title: 认证授权服务API\n    version: 1.0\n    description: 认证授权服务的API文档\n\n', 'd359e6461d37037e1385f5de1b6c7c6e', '2026-01-07 07:37:36', '2026-01-06 23:37:37', 'nacos', '0:0:0:0:0:0:0:1', 'U', 'public', '', 'formal', '', '{\"type\":\"yaml\",\"src_user\":\"nacos\"}');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'role',
  `resource` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'resource',
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'action',
  UNIQUE INDEX `uk_role_permission`(`role` ASC, `resource` ASC, `action` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'username',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'role',
  UNIQUE INDEX `idx_user_role`(`username` ASC, `role` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '租户容量信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'tenant_info' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (1, '1', 'f91fb05b-8bf8-499b-8698-5cb45c6d4fde', 'cloudwaer', 'cloudwaer新版本的命名空间', 'nacos', 1767884463440, 1767884463440);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'username',
  `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'password',
  `enabled` tinyint(1) NOT NULL COMMENT 'enabled',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$W74BAP0.FMDcLygXBXUvc.6MPyvs4Oy59KVFBKRv5KN.eDe9hME5i', 1);

SET FOREIGN_KEY_CHECKS = 1;
