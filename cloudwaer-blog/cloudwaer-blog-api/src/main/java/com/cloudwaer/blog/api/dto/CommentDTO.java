package com.cloudwaer.blog.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 博客评论DTO
 *
 * @author cloudwaer
 */
@Data
public class CommentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 评论ID
	 */
	private Long id;

	/**
	 * 文章ID
	 */
	private Long articleId;

	/**
	 * 文章标题（管理端联查冗余）
	 */
	private String articleTitle;

	/**
	 * 父评论ID，0-顶级评论
	 */
	private Long parentId;

	/**
	 * 根评论ID，0-顶级评论
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
	 * 附图URL列表（评论图片/表情包）
	 */
	private List<String> images = new ArrayList<>();

	/**
	 * 点赞数
	 */
	private Integer likeCount;

	/**
	 * 楼中楼回复总数（公开树分页时回填）
	 */
	private Integer replyTotal;

	/**
	 * 审核状态：0-待审核，1-已通过，2-已拒绝
	 */
	private Integer auditStatus;

	/**
	 * 评论IP
	 */
	private String ip;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 楼中楼回复列表（仅公开读嵌套下发）
	 */
	private List<CommentDTO> replies = new ArrayList<>();

}
