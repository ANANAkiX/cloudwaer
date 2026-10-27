package com.cloudwaer.wiki.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wiki_share")
public class WikiShare extends BaseEntity {

	private String targetType;

	private Long targetId;

	private String token;

	private Integer accessLevel;

	private LocalDateTime expireTime;

	private String password;

	private String remark;

}
