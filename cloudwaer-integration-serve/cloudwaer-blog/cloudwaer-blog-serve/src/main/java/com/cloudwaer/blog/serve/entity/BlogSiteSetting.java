package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客站点设置实体（key-value）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_site_setting")
public class BlogSiteSetting extends BaseEntity {

	/**
	 * 配置键
	 */
	private String settingKey;

	/**
	 * 配置值
	 */
	private String settingValue;

	/**
	 * 备注
	 */
	private String remark;

}
