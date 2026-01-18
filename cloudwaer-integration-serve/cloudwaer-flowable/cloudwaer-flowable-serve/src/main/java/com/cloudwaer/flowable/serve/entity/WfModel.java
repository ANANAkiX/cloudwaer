package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_model")
public class WfModel extends BaseEntity {

    private String modelKey;
    private String modelName;
    private String category;
    private Integer version;
    private Integer modelStatus;

    @TableField("bpmn_xml")
    private String bpmnXml;

    @TableField("form_json")
    private String formJson;

    @TableField("node_actions_json")
    private String nodeActionsJson;

    private String remark;
}
