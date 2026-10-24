package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.ArticleSaveDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 博客文章服务接口
 *
 * @author cloudwaer
 */
public interface BlogArticleService {

	/**
	 * 分页查询文章（管理端，含草稿）
	 * <p>
	 * 非 ADMIN 仅可见本人文章（author_id 隔离）
	 */
	PageResult<ArticleDTO> page(Long current, Long size, String keyword, Integer publishStatus, Long authorId,
			Long categoryId, Long tagId);

	/**
	 * 文章详情（含正文与标签）
	 * @param id 文章ID
	 * @return 文章DTO
	 */
	ArticleDTO detail(Long id);

	/**
	 * 新增文章（作者为当前登录用户）
	 * @param saveDTO 保存DTO
	 * @return 是否成功
	 */
	Boolean saveArticle(ArticleSaveDTO saveDTO);

	/**
	 * 更新文章
	 * @param saveDTO 更新DTO（id必填）
	 * @return 是否成功
	 */
	Boolean updateArticle(ArticleSaveDTO saveDTO);

	/**
	 * 删除文章（逻辑删除，级联清理标签关联）
	 * @param id 文章ID
	 * @return 是否成功
	 */
	Boolean deleteArticle(Long id);

	/**
	 * 批量填充标签
	 * @param articles 文章列表
	 */
	void fillTags(List<ArticleDTO> articles);

	/**
	 * 公开分页查询（仅已发布，置顶/热度排序，正文不返回）
	 * @param current 页码
	 * @param size 每页条数
	 * @param keyword 关键字（标题模糊）
	 * @param categoryId 分类筛选（可空）
	 * @param tagId 标签筛选（可空）
	 * @return 分页结果
	 */
	PageResult<ArticleDTO> publicPage(Long current, Long size, String keyword, Long categoryId, Long tagId);

	/**
	 * 公开文章详情（仅已发布，浏览量+1）
	 * @param id 文章ID
	 * @return 文章DTO
	 */
	ArticleDTO publicDetail(Long id);

	/**
	 * 归档列表（仅已发布，按发布时间倒序，正文不返回）
	 * @return 文章简列表
	 */
	List<ArticleDTO> archive();

	/**
	 * 热门文章（按热度分）
	 * @param limit 条数
	 * @return 文章简列表
	 */
	List<ArticleDTO> hot(int limit);

}
