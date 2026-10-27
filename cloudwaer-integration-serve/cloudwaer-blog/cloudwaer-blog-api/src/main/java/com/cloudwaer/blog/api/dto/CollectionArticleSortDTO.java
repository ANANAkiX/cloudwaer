package com.cloudwaer.blog.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 合集内文章手动排序DTO
 *
 * @author cloudwaer
 */
@Data
public class CollectionArticleSortDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 合集ID
	 */
	@NotNull(message = "合集ID不能为空")
	private Long collectionId;

	/**
	 * 按顺序排列的文章ID列表
	 */
	@NotNull(message = "文章ID列表不能为空")
	private List<Long> articleIds;

}
