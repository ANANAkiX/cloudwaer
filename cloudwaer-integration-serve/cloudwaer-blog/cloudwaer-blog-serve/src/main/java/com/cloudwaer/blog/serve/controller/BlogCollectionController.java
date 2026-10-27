package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.CollectionArticleSortDTO;
import com.cloudwaer.blog.api.dto.CollectionDTO;
import com.cloudwaer.blog.api.dto.CollectionSaveDTO;
import com.cloudwaer.blog.serve.service.BlogCollectionService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客合集控制器
 * <p>
 * 管理端：blog:collection:*（ADMIN 可管全站）；创作中心：/my/* 仅本人（登录即可）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/collection")
@Tag(name = "博客合集管理", description = "合集 CRUD、合集内排序；/my 为创作中心仅本人接口")
public class BlogCollectionController {

	@Autowired
	private BlogCollectionService blogCollectionService;

	/**
	 * 分页查询（管理端，ADMIN 全站）
	 */
	@GetMapping("/page")
	@PermissionCode("blog:collection:view")
	@Operation(summary = "合集分页", description = "管理端；非 ADMIN 仅本人")
	public Result<PageResult<CollectionDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "authorId", required = false) Long authorId) {
		return Result.success(blogCollectionService.page(current, size, keyword, authorId));
	}

	/**
	 * 合集详情
	 */
	@GetMapping("/detail")
	@PermissionCode("blog:collection:view")
	@Operation(summary = "合集详情", description = "非 ADMIN 仅本人")
	public Result<CollectionDTO> detail(@RequestParam("id") Long id) {
		return Result.success(blogCollectionService.detail(id));
	}

	/**
	 * 新增合集
	 */
	@PostMapping("/save")
	@PermissionCode("blog:collection:add")
	@Operation(summary = "新增合集", description = "作者为当前登录用户")
	public Result<Boolean> save(@RequestBody @Validated CollectionSaveDTO dto) {
		return Result.success(blogCollectionService.save(dto));
	}

	/**
	 * 更新合集
	 */
	@PutMapping("/update")
	@PermissionCode("blog:collection:edit")
	@Operation(summary = "更新合集", description = "非 ADMIN 仅本人")
	public Result<Boolean> update(@RequestBody @Validated CollectionSaveDTO dto) {
		return Result.success(blogCollectionService.save(dto));
	}

	/**
	 * 删除合集（解除旗下文章绑定）
	 */
	@DeleteMapping("/delete")
	@PermissionCode("blog:collection:delete")
	@Operation(summary = "删除合集", description = "文章解除绑定，不删除文章")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(blogCollectionService.delete(id));
	}

	/**
	 * 合集内文章手动排序
	 */
	@PutMapping("/article-sort")
	@PermissionCode("blog:collection:sort")
	@Operation(summary = "合集内文章排序", description = "按 articleIds 顺序写入 collection_sort")
	public Result<Boolean> sortArticles(@RequestBody @Validated CollectionArticleSortDTO dto) {
		return Result.success(blogCollectionService.sortArticles(dto));
	}

	// ===== 创作中心：仅本人（登录即可，不依赖 blog:collection 管理码） =====

	/**
	 * 我的合集下拉（写文章选择用）
	 */
	@GetMapping("/my/options")
	@RequireLogin
	@Operation(summary = "我的合集选项", description = "创作中心：当前用户全部合集")
	public Result<List<CollectionDTO>> myOptions() {
		return Result.success(blogCollectionService.myOptions());
	}

	/**
	 * 我的合集分页（创作中心）
	 */
	@GetMapping("/my/page")
	@RequireLogin
	@Operation(summary = "我的合集分页", description = "创作中心：仅本人")
	public Result<PageResult<CollectionDTO>> myPage(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword) {
		return Result.success(blogCollectionService.myPage(current, size, keyword));
	}

	/**
	 * 我的合集保存（创作中心）
	 */
	@PostMapping("/my/save")
	@RequireLogin
	@Operation(summary = "我的合集保存", description = "创作中心：仅本人")
	public Result<Boolean> mySave(@RequestBody @Validated CollectionSaveDTO dto) {
		return Result.success(blogCollectionService.save(dto));
	}

	/**
	 * 我的合集删除（创作中心）
	 */
	@DeleteMapping("/my/delete")
	@RequireLogin
	@Operation(summary = "我的合集删除", description = "创作中心：仅本人")
	public Result<Boolean> myDelete(@RequestParam("id") Long id) {
		return Result.success(blogCollectionService.delete(id));
	}

	/**
	 * 我的合集内文章排序（创作中心）
	 */
	@PutMapping("/my/article-sort")
	@RequireLogin
	@Operation(summary = "我的合集内排序", description = "创作中心：仅本人")
	public Result<Boolean> mySortArticles(@RequestBody @Validated CollectionArticleSortDTO dto) {
		return Result.success(blogCollectionService.sortArticles(dto));
	}

	/**
	 * 合集内本人文章列表
	 */
	@GetMapping("/my/articles")
	@RequireLogin
	@Operation(summary = "合集内文章", description = "创作中心：仅本人")
	public Result<PageResult<com.cloudwaer.blog.api.dto.ArticleDTO>> myArticles(
			@RequestParam("collectionId") Long collectionId,
			@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "20") Long size) {
		return Result.success(blogCollectionService.myArticles(collectionId, current, size));
	}

	/**
	 * 文章加入/移出合集
	 */
	@PutMapping("/my/bind-article")
	@RequireLogin
	@Operation(summary = "文章绑定合集", description = "collectionId 为空表示移出")
	public Result<Boolean> myBindArticle(@RequestParam("articleId") Long articleId,
			@RequestParam(value = "collectionId", required = false) Long collectionId) {
		return Result.success(blogCollectionService.bindArticle(articleId, collectionId));
	}

}
