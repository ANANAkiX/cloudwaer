package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章详情页布局配置（存于 blog_site_setting.articleLayout 的 JSON 对象）
 *
 * @author cloudwaer
 */
@Data
public class ArticleLayoutSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 目录位置：left-左侧，right-右侧，off-不显示（已固定为 right，字段保留兼容）
	 */
	private String tocPosition;

	/**
	 * 侧栏模块编排（推荐使用；空则前端回落默认）
	 */
	private List<LayoutModuleItem> modules;

}
