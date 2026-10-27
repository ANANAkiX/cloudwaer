package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.CategoryDTO;
import com.cloudwaer.blog.api.dto.CategorySaveDTO;
import com.cloudwaer.blog.api.dto.TagDTO;
import com.cloudwaer.blog.api.dto.TagSaveDTO;

import java.util.List;

/**
 * 博客分类/标签服务
 *
 * @author cloudwaer
 */
public interface BlogTaxonomyService {

	/**
	 * 分类列表（公开读，按排序升序，含已发布文章数）
	 * @return 分类列表
	 */
	List<CategoryDTO> listCategories();

	/**
	 * 标签列表（公开读，按名称升序，含已发布引用数——标签云）
	 * @return 标签列表
	 */
	List<TagDTO> listTags();

	/**
	 * 管理端分类列表（含全部状态语义由调用方约定，此处返回逻辑有效数据）
	 * @return 分类列表
	 */
	List<CategoryDTO> adminListCategories();

	/**
	 * 管理端标签列表
	 * @return 标签列表
	 */
	List<TagDTO> adminListTags();

	/**
	 * 新增/更新分类
	 * @param dto 保存DTO
	 * @return 是否成功
	 */
	Boolean saveCategory(CategorySaveDTO dto);

	/**
	 * 删除分类（若仍被文章引用则拒绝）
	 * @param id 分类ID
	 * @return 是否成功
	 */
	Boolean deleteCategory(Long id);

	/**
	 * 新增/更新标签
	 * @param dto 保存DTO
	 * @return 是否成功
	 */
	/**
	 * 保存标签（新增/更新），返回标签 ID
	 */
	Long saveTag(TagSaveDTO dto);

	/**
	 * 删除标签（若仍被文章关联则拒绝）
	 * @param id 标签ID
	 * @return 是否成功
	 */
	Boolean deleteTag(Long id);

}
