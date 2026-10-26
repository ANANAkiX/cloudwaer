package com.cloudwaer.blog.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客评论实体
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_comment")
public class BlogComment extends BaseEntity {

	/**
	 * 文章ID
	 */
	private Long articleId;

	/**
	 * 父评论ID，0-顶级评论
	 */
	private Long parentId;

	/**
	 * 根评论ID（楼中楼归属），0-顶级评论
	 */
	private Long rootId;

	/**
	 * 评论用户ID
	 */
	private Long userId;

	/**
	 * 评论者昵称
	 */
	private String nickname;

	/**
	 * 头像URL
	 */
	private String avatar;

	/**
	 * 评论内容
	 */
	private String content;

	/**
	 * 附图URL列表JSON（如 ["\/blog\/uploads\/xx.png"]）
	 */
	private String images;

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * 质量分（离线落库，前台排序用；事件触发重算）
	 */
	private Double qualityScore;

	/**
	 * 审核状态：0-待审核，1-已通过，2-已拒绝
	 */
	private Integer auditStatus;

	/**
	 * 评论IP
	 */
	private String ip;

}
