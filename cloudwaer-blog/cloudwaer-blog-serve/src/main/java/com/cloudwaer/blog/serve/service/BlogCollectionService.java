package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.CollectionArticleSortDTO;
import com.cloudwaer.blog.api.dto.CollectionDTO;
import com.cloudwaer.blog.api.dto.CollectionSaveDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 博客合集服务接口
 *
 * @author cloudwaer
 */
public interface BlogCollectionService {

	/**
	 * 分页查询合集
	 * <p>
	 * 非 ADMIN 强制仅本人；ADMIN 可按 authorId 筛选
	 */
	PageResult<CollectionDTO> page(Long current, Long size, String keyword, Long authorId);

	/**
	 * 创作中心：仅当前用户合集
	 */
	PageResult<CollectionDTO> myPage(Long current, Long size, String keyword);

	/**
	 * 合集详情
	 */
	CollectionDTO detail(Long id);

	/**
	 * 保存合集（id 空新增，否则更新）；仅本人或 ADMIN
	 */
	Boolean save(CollectionSaveDTO dto);

	/**
	 * 删除合集：解除旗下文章 collection_id 后逻辑删除
	 */
	Boolean delete(Long id);

	/**
	 * 当前登录用户可用合集下拉列表（写文章选择用）
	 */
	List<CollectionDTO> myOptions();

	/**
	 * 合集内文章手动排序（顺序写入 collection_sort）
	 */
	Boolean sortArticles(CollectionArticleSortDTO dto);

	/**
	 * 合集内已发布文章（公开详情侧栏；排除当前文章）
	 * @param collectionId 合集ID
	 * @param excludeArticleId 排除的文章ID
	 */
	List<ArticleDTO> publicArticles(Long collectionId, Long excludeArticleId);

	/**
	 * 创作中心：合集内本人文章（含草稿）
	 */
	PageResult<ArticleDTO> myArticles(Long collectionId, Long current, Long size);

	/**
	 * 创作中心：将本人文章加入/移出合集（collectionId 空则移出）
	 */
	Boolean bindArticle(Long articleId, Long collectionId);

}
