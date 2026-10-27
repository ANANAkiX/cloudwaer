package com.cloudwaer.wiki.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.annotation.RequireLogin;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.wiki.api.dto.WikiSpaceDTO;
import com.cloudwaer.wiki.api.dto.WikiSpaceSaveDTO;
import com.cloudwaer.wiki.serve.service.WikiSpaceService;
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
@RequestMapping("/wiki/space")
@RequiredArgsConstructor
@Tag(name = "知识库空间", description = "空间 CRUD")
public class WikiSpaceController {

	private final WikiSpaceService wikiSpaceService;

	@GetMapping("/page")
	@PermissionCode("wiki:space:view")
	@Operation(summary = "空间分页")
	public Result<PageResult<WikiSpaceDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword) {
		return Result.success(wikiSpaceService.page(current, size, keyword));
	}

	@GetMapping("/list")
	@RequireLogin
	@Operation(summary = "空间列表", description = "下拉选项用；登录即可")
	public Result<List<WikiSpaceDTO>> list() {
		return Result.success(wikiSpaceService.list());
	}

	@GetMapping("/detail")
	@PermissionCode("wiki:space:view")
	@Operation(summary = "空间详情")
	public Result<WikiSpaceDTO> detail(@RequestParam("id") Long id) {
		return Result.success(wikiSpaceService.detail(id));
	}

	@PostMapping("/save")
	@PermissionCode("wiki:space:add")
	@Operation(summary = "新增空间")
	public Result<Boolean> save(@RequestBody @Valid WikiSpaceSaveDTO dto) {
		return Result.success(wikiSpaceService.save(dto));
	}

	@PutMapping("/update")
	@PermissionCode("wiki:space:edit")
	@Operation(summary = "更新空间")
	public Result<Boolean> update(@RequestBody @Valid WikiSpaceSaveDTO dto) {
		return Result.success(wikiSpaceService.update(dto));
	}

	@DeleteMapping("/delete")
	@PermissionCode("wiki:space:delete")
	@Operation(summary = "删除空间")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(wikiSpaceService.delete(id));
	}

}
