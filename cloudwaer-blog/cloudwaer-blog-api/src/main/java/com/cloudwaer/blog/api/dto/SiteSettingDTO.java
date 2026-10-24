package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 博客站点设置DTO（扁平化 key-value，供管理端表单与公开读）
 *
 * <p>
 * 存量键 siteTitle/siteDescription/favicon/enableSearch/layout/footerText 保持扁平字段； 复杂配置以 JSON
 * 字符串存于独立键（nav/pages/articleLayout/homeLayout），DTO 中展开为类型化对象。
 * </p>
 *
 * @author cloudwaer
 */
@Data
public class SiteSettingDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 站点标题
	 */
	private String siteTitle;

	/**
	 * 站点副标题/描述
	 */
	private String siteDescription;

	/**
	 * 站点图标 URL（favicon/icon）
	 */
	private String favicon;

	/**
	 * 是否开启搜索：0-关闭，1-开启
	 */
	private Integer enableSearch;

	/**
	 * 网站布局：default / compact / magazine
	 */
	private String layout;

	/**
	 * 页脚文案
	 */
	private String footerText;

	/**
	 * 导航菜单（JSON 存键 nav）
	 */
	private List<NavSettingItem> nav;

	/**
	 * 独立页面文案，key 为页面标识（如 about）（JSON 存键 pages）
	 */
	private Map<String, PageSettingItem> pages;

	/**
	 * 文章详情布局（JSON 存键 articleLayout）
	 */
	private ArticleLayoutSetting articleLayout;

	/**
	 * 首页模块开关（JSON 存键 homeLayout）
	 */
	private HomeLayoutSetting homeLayout;

}
