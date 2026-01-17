-- 修改流程状态字段为整数类型
ALTER TABLE `wf_process_ext` MODIFY COLUMN `status` int(11) DEFAULT 0 COMMENT '状态：0-运行中，1-已完成，2-已挂起，3-已终止，4-已取消';

-- 更新现有数据
UPDATE `wf_process_ext` SET `status` = 0 WHERE `status` = 'running' OR `status` IS NULL;
UPDATE `wf_process_ext` SET `status` = 1 WHERE `status` = 'completed';
UPDATE `wf_process_ext` SET `status` = 2 WHERE `status` = 'suspended';
UPDATE `wf_process_ext` SET `status` = 3 WHERE `status` = 'terminated';
UPDATE `wf_process_ext` SET `status` = 4 WHERE `status` = 'canceled';
