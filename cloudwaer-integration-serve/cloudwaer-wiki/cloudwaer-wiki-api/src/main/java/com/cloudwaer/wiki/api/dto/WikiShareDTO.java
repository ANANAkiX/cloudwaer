package com.cloudwaer.wiki.api.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class WikiShareDTO {

	private Long id;

	private String targetType;

	private Long targetId;

	private String token;

	private Integer accessLevel;

	private LocalDateTime expireTime;

	private Boolean hasPassword;

	private String remark;

	private String shareUrl;

	private LocalDateTime createTime;

	private Boolean expired;

	private Boolean revoked;

}
