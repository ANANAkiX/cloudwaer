package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_deployment")
public class WfDeployment extends BaseEntity {

    private Long modelId;
    private String modelKey;
    private Integer modelVersion;

    private String deploymentId;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;

    @TableField("form_json")
    private String formJson;

    @TableField("deploy_status")
    private Integer deployStatus;
}
