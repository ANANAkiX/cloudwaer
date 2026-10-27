package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞实体（逻辑删除后唯一索引可能冲突，取消时物理删除）
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "blog_like", keepGlobalPrefix = false)
public class BlogLike extends BaseEntity {

	/**
	 * 目标：1-文章，2-评论，3-话题，4-话题回复
	 */
	private Integer targetType;

	/**
	 * 目标ID
	 */
	private Long targetId;

	/**
	 * 点赞用户ID
	 */
	private Long userId;

}
