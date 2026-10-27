package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 独立页面文案配置（存于 blog_site_setting.pages 的 JSON 对象值）
 *
 * @author cloudwaer
 */
@Data
public class PageSettingItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 页面标题
	 */
	private String title;

	/**
	 * 页面正文（Markdown / 纯文本）
	 */
	private String content;

}
