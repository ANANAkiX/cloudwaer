package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 博客文章导入结果DTO（解析后的标题与 Markdown 正文）
 *
 * @author cloudwaer
 */
@Data
public class ArticleImportDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标题（默认文件名，不含扩展名）
	 */
	private String title;

	/**
	 * 正文（Markdown）
	 */
	private String content;

	/**
	 * 原始文件名
	 */
	private String filename;

	/**
	 * 解析到的正文纯文本长度（便于前端提示）
	 */
	private Integer textLength;

}
