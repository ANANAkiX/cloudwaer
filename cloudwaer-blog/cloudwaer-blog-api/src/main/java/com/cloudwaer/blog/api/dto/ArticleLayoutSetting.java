package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章详情页布局配置（存于 blog_site_setting.articleLayout 的 JSON 对象）
 *
 * @author cloudwaer
 */
@Data
public class ArticleLayoutSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 目录位置：left-左侧，right-右侧，off-不显示
	 */
	private String tocPosition;

}
