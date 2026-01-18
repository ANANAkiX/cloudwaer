/*
 Flowable extension tables
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wf_model
-- ----------------------------
DROP TABLE IF EXISTS `wf_model`;
CREATE TABLE `wf_model`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `model_key` varchar(128) NOT NULL COMMENT 'model key',
  `model_name` varchar(128) NOT NULL COMMENT 'model name',
  `category` varchar(64) NULL DEFAULT NULL COMMENT 'category',
  `version` int(11) NOT NULL DEFAULT 1 COMMENT 'model version',
  `model_status` int(11) NOT NULL DEFAULT 0 COMMENT 'model status: 0-draft,1-released,2-archived',
  `bpmn_xml` longtext NULL COMMENT 'bpmn xml',
  `form_json` longtext NULL COMMENT '动态表单JSON',
  `node_actions_json` longtext NULL COMMENT 'node actions json',
  `remark` varchar(500) NULL DEFAULT NULL COMMENT 'remark',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_model_key_version`(`model_key` ASC, `version` ASC) USING BTREE,
  INDEX `idx_model_key`(`model_key` ASC) USING BTREE,
  INDEX `idx_model_status`(`model_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'flowable model' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wf_node_action
-- ----------------------------
DROP TABLE IF EXISTS `wf_node_action`;
CREATE TABLE `wf_node_action`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `model_id` bigint(20) NOT NULL COMMENT 'model id',
  `model_key` varchar(128) NOT NULL COMMENT 'model key',
  `model_version` int(11) NOT NULL COMMENT 'model version',
  `node_id` varchar(128) NULL DEFAULT NULL COMMENT 'node id',
  `node_name` varchar(128) NULL DEFAULT NULL COMMENT 'node name',
  `event_type` varchar(64) NOT NULL COMMENT 'event type',
  `action_type` varchar(64) NOT NULL COMMENT 'action type',
  `action_config` longtext NULL COMMENT 'action config json',
  `enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'enabled',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_model_id`(`model_id` ASC) USING BTREE,
  INDEX `idx_model_key_version`(`model_key` ASC, `model_version` ASC) USING BTREE,
  INDEX `idx_node_event`(`node_id` ASC, `event_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'flowable node actions' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wf_deployment
-- ----------------------------
DROP TABLE IF EXISTS `wf_deployment`;
CREATE TABLE `wf_deployment`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `model_id` bigint(20) NOT NULL COMMENT 'model id',
  `model_key` varchar(128) NOT NULL COMMENT 'model key',
  `model_version` int(11) NOT NULL COMMENT 'model version',
  `deployment_id` varchar(64) NOT NULL COMMENT 'flowable deployment id',
  `process_definition_id` varchar(64) NOT NULL COMMENT 'process definition id',
  `process_definition_key` varchar(128) NOT NULL COMMENT 'process definition key',
  `process_definition_name` varchar(128) NULL DEFAULT NULL COMMENT 'process definition name',
  `process_definition_version` int(11) NOT NULL COMMENT 'process definition version',
  `form_json` longtext NULL COMMENT '动态表单JSON',
  `deploy_status` int(11) NOT NULL DEFAULT 1 COMMENT 'deploy status: 1-active,0-archived',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_model_key`(`model_key` ASC) USING BTREE,
  INDEX `idx_process_definition_id`(`process_definition_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'flowable deployment' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wf_process_ext
-- ----------------------------
DROP TABLE IF EXISTS `wf_process_ext`;
CREATE TABLE `wf_process_ext`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `process_instance_id` varchar(64) NOT NULL COMMENT 'flowable process instance id',
  `process_definition_key` varchar(128) NOT NULL COMMENT 'process definition key',
  `business_key` varchar(128) NULL DEFAULT NULL COMMENT 'business key',
  `starter_user_id` varchar(64) NULL DEFAULT NULL COMMENT 'starter user id',
  `biz_status` varchar(32) NULL DEFAULT NULL COMMENT 'business status',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_process_instance_id`(`process_instance_id` ASC) USING BTREE,
  INDEX `idx_process_definition_key`(`process_definition_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'flowable process extension table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wf_task_ext
-- ----------------------------
DROP TABLE IF EXISTS `wf_task_ext`;
CREATE TABLE `wf_task_ext`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `task_id` varchar(64) NOT NULL COMMENT 'flowable task id',
  `process_instance_id` varchar(64) NOT NULL COMMENT 'process instance id',
  `assignee` varchar(64) NULL DEFAULT NULL COMMENT 'assignee user id',
  `form_key` varchar(128) NULL DEFAULT NULL COMMENT 'form key',
  `biz_status` varchar(32) NULL DEFAULT NULL COMMENT 'business status',
  `comment` varchar(500) NULL DEFAULT NULL COMMENT 'comment',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_process_instance_id`(`process_instance_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'flowable task extension table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wf_task_handle_record
-- ----------------------------
DROP TABLE IF EXISTS `wf_task_handle_record`;
CREATE TABLE `wf_task_handle_record`  (
  `id` bigint(20) NOT NULL COMMENT 'primary key',
  `process_instance_id` varchar(64) NOT NULL COMMENT 'process instance id',
  `task_id` varchar(64) NOT NULL COMMENT 'task id',
  `task_definition_key` varchar(64) NULL DEFAULT NULL COMMENT 'task definition key',
  `task_name` varchar(255) NULL DEFAULT NULL COMMENT 'task name',
  `assignee` varchar(64) NULL DEFAULT NULL COMMENT 'assignee',
  `result` varchar(64) NULL DEFAULT NULL COMMENT 'result',
  `comment` varchar(500) NULL DEFAULT NULL COMMENT 'comment',
  `duration_ms` bigint(20) NULL DEFAULT NULL COMMENT 'duration ms',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `create_user` varchar(100) NULL DEFAULT NULL COMMENT 'create user',
  `update_time` datetime NOT NULL COMMENT 'update time',
  `update_user` varchar(100) NULL DEFAULT NULL COMMENT 'update user',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT 'record status',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_process_instance_id`(`process_instance_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'task handle record' ROW_FORMAT = DYNAMIC;
SET FOREIGN_KEY_CHECKS = 1;


