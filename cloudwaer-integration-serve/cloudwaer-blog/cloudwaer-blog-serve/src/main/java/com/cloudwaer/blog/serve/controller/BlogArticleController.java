package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.ArticleDTO;
import com.cloudwaer.blog.api.dto.ArticleAuditDTO;
import com.cloudwaer.blog.api.dto.ArticleImportDTO;
import com.cloudwaer.blog.api.dto.ArticleSaveDTO;
import com.cloudwaer.blog.serve.service.BlogArticleService;
import com.cloudwaer.blog.serve.util.ArticleFileParser;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
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
	@Operation(summary = "新增文章", description = "创建文章；返回落库发布状态（0草稿/1已发布/3待审核）")
	public Result<Integer> save(@RequestBody @Validated ArticleSaveDTO saveDTO) {
		return Result.success(blogArticleService.saveArticle(saveDTO));
	}

	/**
	 * 更新文章
	 */
	@PutMapping("/update")
	@PermissionCode("blog:article:edit")
	@Operation(summary = "更新文章", description = "更新文章内容、标签与发布状态；返回落库发布状态")
	public Result<Integer> update(@RequestBody @Validated ArticleSaveDTO saveDTO) {
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

	/**
	 * 文章审核（审核中心；与评论审核一致：登录 + 服务端 ADMIN）
	 */
	@PostMapping("/audit")
	@RequireLogin
	@Operation(summary = "审核文章", description = "1-通过（上线），2-拒绝（回草稿并记录意见）；仅 ADMIN")
	public Result<Boolean> audit(@RequestBody @Validated ArticleAuditDTO dto) {
		return Result.success(blogArticleService.auditArticle(dto.getId(), dto.getAuditStatus(), dto.getRemark()));
	}

	// ===== 创作中心：仅本人（登录即可） =====

	/**
	 * 我的文章分页（创作中心）
	 */
	@GetMapping("/my/page")
	@RequireLogin
	@Operation(summary = "我的文章分页", description = "创作中心：仅本人")
	public Result<PageResult<ArticleDTO>> myPage(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "publishStatus", required = false) Integer publishStatus) {
		return Result.success(blogArticleService.myPage(current, size, keyword, publishStatus));
	}

	/**
	 * 我的文章详情（创作中心）
	 */
	@GetMapping("/my/detail")
	@RequireLogin
	@Operation(summary = "我的文章详情", description = "创作中心：仅本人")
	public Result<ArticleDTO> myDetail(@RequestParam("id") Long id) {
		return Result.success(blogArticleService.detail(id));
	}

	/**
	 * 我的文章保存（创作中心，作者为当前登录用户）
	 */
	@PostMapping("/my/save")
	@RequireLogin
	@Operation(summary = "我的文章保存", description = "创作中心：仅本人新建；返回落库发布状态（0草稿/1已发布/3待审核）")
	public Result<Integer> mySave(@RequestBody @Validated ArticleSaveDTO saveDTO) {
		return Result.success(blogArticleService.saveArticle(saveDTO));
	}

	/**
	 * 我的文章更新（创作中心）
	 */
	@PutMapping("/my/update")
	@RequireLogin
	@Operation(summary = "我的文章更新", description = "创作中心：仅本人；返回落库发布状态")
	public Result<Integer> myUpdate(@RequestBody @Validated ArticleSaveDTO saveDTO) {
		return Result.success(blogArticleService.updateArticle(saveDTO));
	}

	/**
	 * 我的文章删除（创作中心）
	 */
	@DeleteMapping("/my/delete")
	@RequireLogin
	@Operation(summary = "我的文章删除", description = "创作中心：仅本人")
	public Result<Boolean> myDelete(@RequestParam("id") Long id) {
		return Result.success(blogArticleService.deleteArticle(id));
	}

}
