package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 导航菜单项配置（存于 blog_site_setting.nav 的 JSON 数组元素）
 *
 * @author cloudwaer
 */
@Data
public class NavSettingItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 唯一键：home / forum / archive / about / write / 自定义
	 */
	private String key;

	/**
	 * 显示文案
	 */
	private String label;

	/**
	 * 跳转路径（站内路由）
	 */
	private String path;

	/**
	 * 是否显示：true-显示，false-隐藏
	 */
	private Boolean visible;

	/**
	 * 排序值（小在前）
	 */
	private Integer order;

}
