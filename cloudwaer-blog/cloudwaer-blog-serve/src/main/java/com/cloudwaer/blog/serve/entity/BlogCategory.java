package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客分类实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_category")
public class BlogCategory extends BaseEntity {

	/**
	 * 分类名称
	 */
	private String name;

	/**
	 * 别名
	 */
	private String slug;

	/**
	 * 分类描述
	 */
	private String description;

	/**
	 * 排序
	 */
	private Integer sort;

}
