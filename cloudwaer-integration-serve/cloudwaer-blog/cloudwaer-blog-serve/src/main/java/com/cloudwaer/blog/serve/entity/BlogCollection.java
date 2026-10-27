package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客合集实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_collection")
public class BlogCollection extends BaseEntity {

	/**
	 * 合集名称
	 */
	private String name;

	/**
	 * 合集简介
	 */
	private String description;

	/**
	 * 封面图URL（预留）
	 */
	private String coverUrl;

	/**
	 * 作者ID
	 */
	private Long authorId;

	/**
	 * 作者昵称（冗余）
	 */
	private String authorName;

	/**
	 * 文章数（已发布冗余）
	 */
	private Integer articleCount;

	/**
	 * 合集展示排序
	 */
	private Integer sort;

	/**
	 * 是否精选：0-否，1-是
	 */
	private Integer isFeatured;

	/**
	 * 可见性：0-私密，1-公开
	 */
	private Integer visibility;

	/**
	 * 扩展JSON
	 */
	private String extra;

}
