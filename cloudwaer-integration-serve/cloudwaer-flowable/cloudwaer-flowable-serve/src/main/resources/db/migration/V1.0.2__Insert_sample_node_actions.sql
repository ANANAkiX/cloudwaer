-- 插入示例节点配置数据
INSERT INTO wf_node_action (model_key, model_version, node_id, node_name, event_type, action_type, action_config, enabled, create_time, update_time) VALUES
('test-process-1', 1, 'userTask1', '用户任务1', 'create', 'assign', '1', 1, NOW(), NOW()),
('test-process-1', 1, 'userTask2', '用户任务2', 'create', 'assign', '2', 1, NOW(), NOW());

-- 插入示例流程模型数据
INSERT INTO wf_model (model_key, model_name, category, version, model_status, bpmn_xml, node_actions_json, remark, create_time, update_time) VALUES
('test-process-1', '测试流程1', 'test', 1, 1, 
'<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:flowable="http://flowable.org/bpmn"
             targetNamespace="http://www.flowable.org/processdef">
  <process id="test-process-1" name="测试流程1" isExecutable="true">
    <startEvent id="startEvent1" name="开始"/>
    <userTask id="userTask1" name="用户任务1">
      <extensionElements>
        <flowable:taskListener event="create" delegateExpression="${flowableTaskListener}"/>
      </extensionElements>
    </userTask>
    <userTask id="userTask2" name="用户任务2">
      <extensionElements>
        <flowable:taskListener event="create" delegateExpression="${flowableTaskListener}"/>
      </extensionElements>
    </userTask>
    <endEvent id="endEvent1" name="结束"/>
    <sequenceFlow id="flow1" sourceRef="startEvent1" targetRef="userTask1"/>
    <sequenceFlow id="flow2" sourceRef="userTask1" targetRef="userTask2"/>
    <sequenceFlow id="flow3" sourceRef="userTask2" targetRef="endEvent1"/>
  </process>
</definitions>',
'[{"nodeId":"userTask1","actionType":"assign","actionConfig":"1"},{"nodeId":"userTask2","actionType":"assign","actionConfig":"2"}]',
'测试流程1示例', NOW(), NOW());
