package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 抄送记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_cc_record")
public class WfCcRecord extends BaseEntity {

	private String instanceId;

	private String defKey;

	private String nodeId;

	private String ccType;

	private String ccTarget;

	private String ccBy;

	private Integer readStatus;

	private LocalDateTime readTime;

}
