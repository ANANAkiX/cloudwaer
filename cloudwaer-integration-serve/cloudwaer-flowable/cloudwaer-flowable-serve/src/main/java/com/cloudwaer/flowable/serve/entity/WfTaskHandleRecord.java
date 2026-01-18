package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_task_handle_record")
public class WfTaskHandleRecord extends BaseEntity {

    private String processInstanceId;
    private String taskId;
    private String taskDefinitionKey;
    private String taskName;
    private String assignee;
    private String result;
    private String comment;
    private Long durationMs;
}
