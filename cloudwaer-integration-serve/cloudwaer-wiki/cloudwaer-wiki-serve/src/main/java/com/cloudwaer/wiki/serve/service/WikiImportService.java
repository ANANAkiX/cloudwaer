package com.cloudwaer.wiki.serve.service;

import com.cloudwaer.wiki.api.dto.WikiImportResultDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface WikiImportService {

	/**
	 * 导入 zip 包或单个文档文件。zip 内目录会同步建成文档节点（空正文），文件成为叶子文档。
	 */
	WikiImportResultDTO importArchive(Long spaceId, Long parentId, MultipartFile file);

	/**
	 * 导入文件夹（浏览器多文件 + 相对路径）。path 形如 {@code guide/intro.md}，中间段建成目录节点。
	 * @param paths 与 files 一一对应的相对路径（用 / 分隔）
	 */
	WikiImportResultDTO importFiles(Long spaceId, Long parentId, List<String> paths, List<MultipartFile> files);

}
