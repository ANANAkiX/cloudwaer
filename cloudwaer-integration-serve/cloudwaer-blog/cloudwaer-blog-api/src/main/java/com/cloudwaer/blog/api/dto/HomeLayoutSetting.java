package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页布局配置（存于 blog_site_setting.homeLayout 的 JSON 对象）
 *
 * @author cloudwaer
 */
@Data
public class HomeLayoutSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否显示精选大卡（兼容旧配置）
	 */
	private Boolean showFeatured;

	/**
	 * 是否显示标签云侧栏（兼容旧配置）
	 */
	private Boolean showTagCloud;

	/**
	 * 是否显示热门文章侧栏（兼容旧配置）
	 */
	private Boolean showHot;

	/**
	 * 首页模块编排（推荐使用；空则前端回落默认）
	 */
	private List<LayoutModuleItem> modules;

}
