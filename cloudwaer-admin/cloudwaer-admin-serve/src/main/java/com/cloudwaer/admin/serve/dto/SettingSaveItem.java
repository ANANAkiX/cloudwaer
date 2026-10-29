package com.cloudwaer.admin.serve.dto;

import lombok.Data;

/**
 * 全局设置保存项
 *
 * @author cloudwaer
 */
@Data
public class SettingSaveItem {

	/**
	 * 配置键
	 */
	private String settingKey;

	/**
	 * 配置值；null 或空串表示清空（回到 yml/代码兜底）
	 */
	private String settingValue;

}
