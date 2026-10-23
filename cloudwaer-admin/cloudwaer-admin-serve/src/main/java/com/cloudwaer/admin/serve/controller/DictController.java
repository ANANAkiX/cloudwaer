package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.DictDTO;
import com.cloudwaer.admin.api.dto.DictItemDTO;
import com.cloudwaer.admin.serve.service.DictService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dict")
@Tag(name = "Dictionary", description = "Dictionary APIs")
public class DictController {

	@Autowired
	private DictService dictService;

	@GetMapping("/list")
	@PermissionCode("admin:dict:view")
	@Operation(summary = "List items by type")
	public Result<List<DictItemDTO>> listByType(@RequestParam String type) {
		return Result.success(dictService.getItemsByType(type));
	}

	@PostMapping("/refresh")
	@PermissionCode("admin:dict:refresh")
	@Operation(summary = "Refresh dict cache")
	public Result<Boolean> refresh() {
		dictService.rebuildCache();
		return Result.success(true);
	}

	// ===== Dict headers =====
	@GetMapping("/page")
	@PermissionCode("admin:dict:page")
	@Operation(summary = "Page dictionaries")
	public Result<PageResult<DictDTO>> page(@RequestParam(value = "current", defaultValue = "1") Long current,
			@RequestParam(value = "size", defaultValue = "10") Long size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "type", required = false) String type) {
		PageDTO pageDTO = new PageDTO();
		pageDTO.setCurrent(current);
		pageDTO.setSize(size);
		pageDTO.setKeyword(keyword);
		PageResult<DictDTO> pageResult = dictService.page(pageDTO, keyword, type);
		return Result.success(pageResult);
	}

	@GetMapping("/detail")
	@PermissionCode("admin:dict:view")
	@Operation(summary = "Dict detail")
	public Result<DictDTO> detail(@RequestParam("id") Long id) {
		return Result.success(dictService.detail(id));
	}

	@PostMapping("/save")
	@PermissionCode("admin:dict:add")
	@Operation(summary = "Create dict")
	public Result<Boolean> save(@RequestBody DictDTO dto) {
		return Result.success(dictService.save(dto));
	}

	@PutMapping("/update")
	@PermissionCode("admin:dict:edit")
	@Operation(summary = "Update dict")
	public Result<Boolean> update(@RequestBody DictDTO dto) {
		return Result.success(dictService.update(dto));
	}

	@DeleteMapping("/delete")
	@PermissionCode("admin:dict:delete")
	@Operation(summary = "Delete dict")
	public Result<Boolean> delete(@RequestParam("id") Long id) {
		return Result.success(dictService.delete(id));
	}

	// ===== Dict items =====
	@GetMapping("/item/list")
	@PermissionCode("admin:dict:view")
	@Operation(summary = "List dict items")
	public Result<List<DictItemDTO>> listItems(@RequestParam("dictId") Long dictId) {
		return Result.success(dictService.listItems(dictId));
	}

	@PostMapping("/item/save")
	@PermissionCode("admin:dict:add")
	@Operation(summary = "Create dict item")
	public Result<Boolean> saveItem(@RequestBody DictItemDTO dto) {
		return Result.success(dictService.saveItem(dto));
	}

	@PutMapping("/item/update")
	@PermissionCode("admin:dict:edit")
	@Operation(summary = "Update dict item")
	public Result<Boolean> updateItem(@RequestBody DictItemDTO dto) {
		return Result.success(dictService.updateItem(dto));
	}

	@DeleteMapping("/item/delete")
	@PermissionCode("admin:dict:delete")
	@Operation(summary = "Delete dict item")
	public Result<Boolean> deleteItem(@RequestParam("id") Long id) {
		return Result.success(dictService.deleteItem(id));
	}

}
