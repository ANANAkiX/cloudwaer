package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全局动态设置
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_setting")
public class SysSetting extends BaseEntity {

	private String settingKey;

	private String settingValue;

	private String valueType;

	private String groupKey;

	private String remark;

}
