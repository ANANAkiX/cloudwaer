package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签关联实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_article_tag")
public class BlogArticleTag extends BaseEntity {

	/**
	 * 文章ID
	 */
	private Long articleId;

	/**
	 * 标签ID
	 */
	private Long tagId;

}
