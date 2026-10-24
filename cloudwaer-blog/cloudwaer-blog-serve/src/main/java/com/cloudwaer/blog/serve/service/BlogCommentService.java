package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.CommentDTO;
import com.cloudwaer.blog.api.dto.CommentSaveDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 博客评论服务
 *
 * @author cloudwaer
 */
public interface BlogCommentService {

	/**
	 * 发表评论（登录用户，审核通过前不对前台可见）
	 * @param dto 评论DTO
	 * @param clientIp 客户端IP
	 * @return 评论ID
	 */
	Long addComment(CommentSaveDTO dto, String clientIp);

	/**
	 * 文章公开评论树（仅已通过，顶级 + 楼中楼）
	 * @param articleId 文章ID
	 * @return 顶级评论列表（含 replies）
	 */
	List<CommentDTO> listByArticle(Long articleId);

	/**
	 * 管理端评论分页
	 * @param current 页码
	 * @param size 页大小
	 * @param articleId 文章ID筛选
	 * @param auditStatus 审核状态筛选
	 * @param keyword 关键字
	 * @return 分页结果
	 */
	PageResult<CommentDTO> page(Long current, Long size, Long articleId, Integer auditStatus, String keyword);

	/**
	 * 审核评论
	 * @param id 评论ID
	 * @param auditStatus 1-通过，2-拒绝
	 * @return 是否成功
	 */
	Boolean audit(Long id, Integer auditStatus);

	/**
	 * 逻辑删除评论（已通过评论同步扣减文章评论数）
	 * @param id 评论ID
	 * @return 是否成功
	 */
	Boolean deleteComment(Long id);

}
