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
	 * 文章评论树分页（前台）：顶级评论分页，每条附带前 N 条楼中楼与回复总数
	 * @param articleId 文章ID
	 * @param current 页码（从1起）
	 * @param size 顶级页大小（默认5）
	 * @param childSize 每条附带子评论数（默认3）
	 * @return 分页结果
	 */
	PageResult<CommentDTO> listPage(Long articleId, Long current, Long size, Long childSize);

	/**
	 * 楼中楼回复按偏移分页（前台「展开更多」）
	 * @param rootId 根评论ID
	 * @param offset 偏移（从0起，即已展示条数）
	 * @param size 本次加载条数（默认10）
	 * @return 分页结果
	 */
	PageResult<CommentDTO> listReplies(Long rootId, Long offset, Long size);

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

	/**
	 * 重算单条评论的质量分并落库（供审核/删除/点赞链路调用；目标不存在时静默返回）
	 *
	 * <p>
	 * 算分只在事件触发时执行，避免排序键随时间漂移导致 offset 翻页重漏。
	 * </p>
	 * @param commentId 评论ID
	 */
	void refreshQualityScore(Long commentId);

}
