package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.ArticleImportDTO;
import com.cloudwaer.blog.api.dto.ArticleSaveDTO;
import com.cloudwaer.blog.serve.service.BlogArticleService;
import com.cloudwaer.blog.serve.util.ArticleFileParser;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 博客文章控制器（管理端）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/article")
@Tag(name = "博客文章管理", description = "文章的增删改查与状态管理")
public class BlogArticleController {

	@Autowired
	private BlogArticleService blogArticleService;

	/**
	 * 分页查询文章
	 */
	@GetMapping("/page")
	@PermissionCode("blog:article:view")
	@Operation(summary = "分页查询文章", description = "管理端文章列表；非 ADMIN 仅本人；支持关键字/发布状态/作者筛选")
	public Result<PageResult<ArticleDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "publishStatus", required = false) Integer publishStatus,
			@RequestParam(value = "authorId", required = false) Long authorId,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "tagId", required = false) Long tagId) {
		return Result
			.success(blogArticleService.page(current, size, keyword, publishStatus, authorId, categoryId, tagId));
	}

	/**
	 * 文章详情
	 */
	@GetMapping("/detail")
	@PermissionCode("blog:article:view")
	@Operation(summary = "文章详情", description = "根据ID获取文章详情（含正文与标签）")
	public Result<ArticleDTO> detail(@RequestParam("id") Long id) {
		return Result.success(blogArticleService.detail(id));
	}

	/**
	 * 新增文章
	 */
	@PostMapping("/save")
	@PermissionCode("blog:article:add")
	@Operation(summary = "新增文章", description = "创建文章，作者为当前登录用户")
	public Result<Boolean> save(@RequestBody @Validated ArticleSaveDTO saveDTO) {
		return Result.success(blogArticleService.saveArticle(saveDTO));
	}

	/**
	 * 更新文章
	 */
	@PutMapping("/update")
	@PermissionCode("blog:article:edit")
	@Operation(summary = "更新文章", description = "更新文章内容、标签与发布状态")
	public Result<Boolean> update(@RequestBody @Validated ArticleSaveDTO saveDTO) {
		return Result.success(blogArticleService.updateArticle(saveDTO));
	}

	/**
	 * 导入文章文件（MD / TXT / PDF / DOCX），解析为 Markdown 后由前端填入表单
	 */
	@PostMapping("/import")
	@PermissionCode("blog:article:add")
	@Operation(summary = "导入文章文件", description = "上传 md/txt/pdf/docx；标题默认文件名（去扩展名），正文解析为 Markdown；不落库，由前端新建/保存")
	public Result<ArticleImportDTO> importFile(@RequestParam("file") MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException("请选择要导入的文件");
		}
		String original = file.getOriginalFilename() == null ? "未命名" : file.getOriginalFilename();
		try {
			ArticleFileParser.ParsedArticle parsed = ArticleFileParser.parse(original, file.getBytes());
			ArticleImportDTO dto = new ArticleImportDTO();
			dto.setTitle(parsed.title());
			dto.setContent(parsed.content());
			dto.setFilename(original);
			dto.setTextLength(parsed.content().length());
			return Result.success(dto);
		}
		catch (BusinessException e) {
			throw e;
		}
		catch (Exception e) {
			log.warn("文章导入失败: {}", original, e);
			throw new BusinessException("文章导入失败：" + e.getMessage());
		}
	}

	/**
	 * 删除文章
	 */
	@DeleteMapping("/delete")
	@PermissionCode("blog:article:delete")
	@Operation(summary = "删除文章", description = "逻辑删除文章并清理标签关联")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(blogArticleService.deleteArticle(id));
	}

}
