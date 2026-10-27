package com.cloudwaer.wiki.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.wiki.api.dto.WikiImportResultDTO;
import com.cloudwaer.wiki.serve.service.WikiImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/wiki/doc")
@RequiredArgsConstructor
@Tag(name = "知识库导入", description = "zip / 文件夹导入为文档树")
public class WikiImportController {

	private final WikiImportService wikiImportService;

	/**
	 * 导入 zip 或单个 md/markdown/txt。
	 */
	@PostMapping("/import")
	@PermissionCode("wiki:doc:add")
	@Operation(summary = "导入 zip/文档", description = "zip 内目录同步建成文档节点；仅 md/markdown/txt")
	public Result<WikiImportResultDTO> importArchive(@RequestParam("spaceId") Long spaceId,
			@RequestParam(value = "parentId", required = false) Long parentId,
			@RequestParam("file") MultipartFile file) {
		return Result.success(wikiImportService.importArchive(spaceId, parentId, file));
	}

	/**
	 * 文件夹导入：浏览器多文件 + 相对路径（paths 与 files 按下标对齐，逗号分隔）。
	 */
	@PostMapping("/import-folder")
	@PermissionCode("wiki:doc:add")
	@Operation(summary = "导入文件夹", description = "paths 为相对路径列表（/ 分隔），中间目录会建成节点")
	public Result<WikiImportResultDTO> importFolder(@RequestParam("spaceId") Long spaceId,
			@RequestParam(value = "parentId", required = false) Long parentId,
			@RequestParam(value = "paths", required = false) List<String> paths,
			@RequestParam("files") List<MultipartFile> files) {
		List<String> pathList = paths == null ? new ArrayList<>() : paths;
		return Result.success(wikiImportService.importFiles(spaceId, parentId, pathList, files));
	}

}
