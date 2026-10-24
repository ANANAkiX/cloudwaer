package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.CategoryDTO;
import com.cloudwaer.blog.api.dto.CategorySaveDTO;
import com.cloudwaer.blog.api.dto.TagDTO;
import com.cloudwaer.blog.api.dto.TagSaveDTO;
import com.cloudwaer.blog.serve.service.BlogTaxonomyService;
import com.cloudwaer.common.core.annotation.PermissionCode;
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
 * 博客分类/标签管理控制器（管理端）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog")
@Tag(name = "博客分类标签管理", description = "分类与标签的管理端 CRUD")
public class BlogTaxonomyController {

	@Autowired
	private BlogTaxonomyService blogTaxonomyService;

	// ===== 分类 =====

	/**
	 * 分类列表
	 */
	@GetMapping("/category/list")
	@PermissionCode("blog:category:view")
	@Operation(summary = "分类列表", description = "管理端分类列表")
	public Result<List<CategoryDTO>> listCategories() {
		return Result.success(blogTaxonomyService.adminListCategories());
	}

	/**
	 * 保存分类（新增/更新）
	 */
	@PostMapping("/category/save")
	@PermissionCode("blog:category:add")
	@Operation(summary = "保存分类", description = "id 为空新增，否则更新")
	public Result<Boolean> saveCategory(@RequestBody @Validated CategorySaveDTO dto) {
		return Result.success(blogTaxonomyService.saveCategory(dto));
	}

	/**
	 * 更新分类
	 */
	@PutMapping("/category/update")
	@PermissionCode("blog:category:edit")
	@Operation(summary = "更新分类", description = "更新已有分类")
	public Result<Boolean> updateCategory(@RequestBody @Validated CategorySaveDTO dto) {
		return Result.success(blogTaxonomyService.saveCategory(dto));
	}

	/**
	 * 删除分类
	 */
	@DeleteMapping("/category/delete")
	@PermissionCode("blog:category:delete")
	@Operation(summary = "删除分类", description = "被文章引用时拒绝删除")
	public Result<Boolean> deleteCategory(@RequestParam("id") Long id) {
		return Result.success(blogTaxonomyService.deleteCategory(id));
	}

	// ===== 标签 =====

	/**
	 * 标签列表
	 */
	@GetMapping("/tag/list")
	@PermissionCode("blog:tag:view")
	@Operation(summary = "标签列表", description = "管理端标签列表")
	public Result<List<TagDTO>> listTags() {
		return Result.success(blogTaxonomyService.adminListTags());
	}

	/**
	 * 保存标签（新增/更新）
	 */
	@PostMapping("/tag/save")
	@PermissionCode("blog:tag:add")
	@Operation(summary = "保存标签", description = "id 为空新增，否则更新")
	public Result<Boolean> saveTag(@RequestBody @Validated TagSaveDTO dto) {
		return Result.success(blogTaxonomyService.saveTag(dto));
	}

	/**
	 * 更新标签
	 */
	@PutMapping("/tag/update")
	@PermissionCode("blog:tag:edit")
	@Operation(summary = "更新标签", description = "更新已有标签")
	public Result<Boolean> updateTag(@RequestBody @Validated TagSaveDTO dto) {
		return Result.success(blogTaxonomyService.saveTag(dto));
	}

	/**
	 * 删除标签
	 */
	@DeleteMapping("/tag/delete")
	@PermissionCode("blog:tag:delete")
	@Operation(summary = "删除标签", description = "被文章关联时拒绝删除")
	public Result<Boolean> deleteTag(@RequestParam("id") Long id) {
		return Result.success(blogTaxonomyService.deleteTag(id));
	}

}
