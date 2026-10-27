package com.cloudwaer.wiki.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.wiki.api.dto.WikiDocDTO;
import com.cloudwaer.wiki.api.dto.WikiDocSaveDTO;
import com.cloudwaer.wiki.serve.service.WikiDocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wiki/doc")
@RequiredArgsConstructor
@Tag(name = "知识库文档", description = "文档树 CRUD、移动")
public class WikiDocController {

	private final WikiDocService wikiDocService;

	@GetMapping("/tree")
	@PermissionCode("wiki:doc:view")
	@Operation(summary = "文档树")
	public Result<List<WikiDocDTO>> tree(@RequestParam("spaceId") Long spaceId) {
		return Result.success(wikiDocService.tree(spaceId));
	}

	@GetMapping("/detail")
	@PermissionCode("wiki:doc:view")
	@Operation(summary = "文档详情")
	public Result<WikiDocDTO> detail(@RequestParam("id") Long id) {
		return Result.success(wikiDocService.detail(id));
	}

	@PostMapping("/save")
	@PermissionCode("wiki:doc:add")
	@Operation(summary = "新增文档")
	public Result<Boolean> save(@RequestBody @Valid WikiDocSaveDTO dto) {
		return Result.success(wikiDocService.save(dto));
	}

	@PutMapping("/update")
	@PermissionCode("wiki:doc:edit")
	@Operation(summary = "更新文档")
	public Result<Boolean> update(@RequestBody @Valid WikiDocSaveDTO dto) {
		return Result.success(wikiDocService.update(dto));
	}

	@PutMapping("/move")
	@PermissionCode("wiki:doc:edit")
	@Operation(summary = "移动/排序")
	public Result<Boolean> move(@RequestParam("id") Long id,
			@RequestParam(value = "parentId", required = false) Long parentId,
			@RequestParam(value = "sort", required = false) Integer sort) {
		return Result.success(wikiDocService.move(id, parentId, sort));
	}

	@DeleteMapping("/delete")
	@PermissionCode("wiki:doc:delete")
	@Operation(summary = "删除文档")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(wikiDocService.delete(id));
	}

}
