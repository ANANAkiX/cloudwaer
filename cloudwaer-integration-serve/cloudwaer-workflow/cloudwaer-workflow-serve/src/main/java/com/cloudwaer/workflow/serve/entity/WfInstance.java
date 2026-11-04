package com.cloudwaer.workflow.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 流程实例
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_instance")
public class WfInstance extends BaseEntity {

	private String instanceId;

	private Long defId;

	private String defKey;

	private Integer defVersion;

	private String defName;

	private String businessKey;

	private String title;

	private String formJson;

	private Integer instStatus;

	private String priority;

	private LocalDateTime dueTime;

	private String startUser;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String currentNodes;

	private String varsJson;

}
