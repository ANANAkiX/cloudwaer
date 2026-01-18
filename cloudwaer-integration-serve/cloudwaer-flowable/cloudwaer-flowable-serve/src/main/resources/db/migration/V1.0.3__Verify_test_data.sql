-- 验证测试数据是否存在
SELECT '=== wf_model 表数据 ===' as info;
SELECT id, model_key, model_name, category, version, model_status, create_time, update_time FROM wf_model WHERE model_key = 'test-process-1';

SELECT '=== wf_node_action 表数据 ===' as info;
SELECT id, model_key, model_version, node_id, node_name, action_type, action_config, enabled FROM wf_node_action WHERE model_key = 'test-process-1';

SELECT '=== 检查流程是否已部署 ===' as info;
SELECT ID_, KEY_, NAME_, VERSION_, DEPLOYMENT_ID_ FROM ACT_RE_PROCDEF WHERE KEY_ = 'test-process-1';

SELECT '=== 检查是否有流程实例 ===' as info;
SELECT ID_, PROC_INST_ID_, PROC_DEF_ID_, NAME_, ASSIGNEE_, CREATE_TIME_ FROM ACT_RU_TASK WHERE PROC_INST_ID_ IN (SELECT ID_ FROM ACT_RU_EXECUTION WHERE PROC_DEF_ID_ IN (SELECT ID_ FROM ACT_RE_PROCDEF WHERE KEY_ = 'test-process-1'));
