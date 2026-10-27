package com.cloudwaer.wiki.api.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 导入结果：新建目录/文档计数与失败明细。
 */
@Data
public class WikiImportResultDTO {

	private int dirCount;

	private int docCount;

	private List<String> skipped = new ArrayList<>();

	private List<String> errors = new ArrayList<>();

	public void addSkipped(String path, String reason) {
		skipped.add(path + " — " + reason);
	}

	public void addError(String path, String reason) {
		errors.add(path + " — " + reason);
	}

}
