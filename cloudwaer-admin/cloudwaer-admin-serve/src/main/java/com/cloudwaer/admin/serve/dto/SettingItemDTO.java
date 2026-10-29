package com.cloudwaer.admin.serve.dto;

import lombok.Data;

/**
 * 全局设置列表项（含生效值与来源）
 *
 * @author cloudwaer
 */
@Data
public class SettingItemDTO {

	/**
	 * 配置键
	 */
	private String settingKey;

	/**
	 * 分组：captcha|auth|jwt|security|oauth|file|system
	 */
	private String groupKey;

	/**
	 * 值类型：STRING|BOOL|INT|LONG|JSON|SECRET
	 */
	private String valueType;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 已保存到库的值；null=未覆盖（走 yml/代码兜底）
	 */
	private String dbValue;

	/**
	 * 当前生效值（已发布快照值，否则兜底值）
	 */
	private String effectiveValue;

	/**
	 * 生效来源：DB-已发布 / YML-配置文件 / DEFAULT-代码默认
	 */
	private String source;

	/**
	 * 是否存在「已保存未发布」的改动
	 */
	private Boolean pendingPublish;

	/**
	 * 是否已实现可生效；false=暂未完成（前端禁用开关）
	 */
	private Boolean implemented;

}
