package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.CategoryDTO;
import com.cloudwaer.blog.api.dto.TagDTO;
import com.cloudwaer.blog.serve.service.BlogArticleService;
import com.cloudwaer.blog.serve.service.BlogTaxonomyService;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客公开读接口（匿名访问，方法上不加权限注解即按匿名放行）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/open")
@Tag(name = "博客公开接口", description = "面向博客前台的匿名只读接口")
public class BlogOpenController {

	@Autowired
	private BlogArticleService blogArticleService;

	@Autowired
	private BlogTaxonomyService blogTaxonomyService;

	/**
	 * 已发布文章分页列表
	 */
	@GetMapping("/article/list")
	@Operation(summary = "文章列表", description = "仅已发布文章，支持关键字/分类/标签筛选")
	public Result<PageResult<ArticleDTO>> list(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "tagId", required = false) Long tagId) {
		return Result.success(blogArticleService.publicPage(current, size, keyword, categoryId, tagId));
	}

	/**
	 * 已发布文章详情（浏览量+1）
	 */
	@GetMapping("/article/detail")
	@Operation(summary = "文章详情", description = "仅已发布文章，每次访问浏览量+1")
	public Result<ArticleDTO> detail(@RequestParam("id") Long id) {
		return Result.success(blogArticleService.publicDetail(id));
	}

	/**
	 * 归档列表
	 */
	@GetMapping("/article/archive")
	@Operation(summary = "归档列表", description = "已发布文章按发布时间倒序")
	public Result<List<ArticleDTO>> archive() {
		return Result.success(blogArticleService.archive());
	}

	/**
	 * 热门文章
	 */
	@GetMapping("/article/hot")
	@Operation(summary = "热门文章", description = "按热度分排序，侧栏推荐")
	public Result<List<ArticleDTO>> hot(@RequestParam(value = "limit", defaultValue = "5") Integer limit) {
		return Result.success(blogArticleService.hot(limit == null ? 5 : limit));
	}

	/**
	 * 分类列表
	 */
	@GetMapping("/category/list")
	@Operation(summary = "分类列表", description = "全部启用分类")
	public Result<List<CategoryDTO>> categories() {
		return Result.success(blogTaxonomyService.listCategories());
	}

	/**
	 * 标签列表
	 */
	@GetMapping("/tag/list")
	@Operation(summary = "标签列表", description = "全部启用标签")
	public Result<List<TagDTO>> tags() {
		return Result.success(blogTaxonomyService.listTags());
	}

}
