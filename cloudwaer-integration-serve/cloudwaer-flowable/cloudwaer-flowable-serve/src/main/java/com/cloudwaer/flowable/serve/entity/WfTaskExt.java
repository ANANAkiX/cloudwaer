package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_task_ext")
public class WfTaskExt extends BaseEntity {

    private String taskId;
    private String processInstanceId;
    private String assignee;
    private String formKey;

    @TableField("biz_status")
    private String bizStatus;

    private String comment;
}
