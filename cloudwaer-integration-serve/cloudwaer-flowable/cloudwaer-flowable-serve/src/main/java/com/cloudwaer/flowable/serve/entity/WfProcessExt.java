package com.cloudwaer.flowable.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_process_ext")
public class WfProcessExt extends BaseEntity {

	@TableField("process_instance_id")
	private String processInstanceId;

	@TableField("process_definition_key")
	private String processDefinitionKey;

	@TableField("business_key")
	private String businessKey;

	private Integer status;

}
