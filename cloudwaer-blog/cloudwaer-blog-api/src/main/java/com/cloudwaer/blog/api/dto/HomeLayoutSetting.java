package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页模块开关配置（存于 blog_site_setting.homeLayout 的 JSON 对象）
 *
 * @author cloudwaer
 */
@Data
public class HomeLayoutSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否显示精选大卡
	 */
	private Boolean showFeatured;

	/**
	 * 是否显示标签云侧栏
	 */
	private Boolean showTagCloud;

	/**
	 * 是否显示热门文章侧栏
	 */
	private Boolean showHot;

}
