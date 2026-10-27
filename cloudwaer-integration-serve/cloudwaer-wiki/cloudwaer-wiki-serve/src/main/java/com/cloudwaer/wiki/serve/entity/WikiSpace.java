package com.cloudwaer.wiki.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wiki_space")
public class WikiSpace extends BaseEntity {

	private String name;

	private String slug;

	private String description;

	private Integer publicRead;

	private Integer sort;

}
