package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 页面模块编排项（首页 / 详情侧栏共用）
 *
 * @author cloudwaer
 */
@Data
public class LayoutModuleItem implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 模块 key，如 siteProfile / toc
	 */
	private String key;

	/**
	 * 分区：left / main / right / hidden
	 */
	private String zone;

	/**
	 * 分区内排序
	 */
	private Integer order;

	/**
	 * 是否显示
	 */
	private Boolean visible;

	/**
	 * 自定义标题（可空）
	 */
	private String title;

	/**
	 * 扩展配置
	 */
	private Map<String, Object> extra;

}
