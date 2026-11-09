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

	/**
	 * 已发布值（快照重建源；NULL=从未发布）。发布时由 setting_value 同步
	 */
	private String publishedValue;

	private String valueType;

	private String groupKey;

	private String remark;

}
