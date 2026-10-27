package com.cloudwaer.wiki.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wiki_doc")
public class WikiDoc extends BaseEntity {

	private Long spaceId;

	private Long parentId;

	private String title;

	private String content;

	private String summary;

	private Integer sort;

	private Integer publicRead;

	private Long authorId;

	private String authorName;

}
