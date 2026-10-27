package com.cloudwaer.wiki.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WikiShareSaveDTO {

	@NotBlank(message = "目标类型不能为空")
	private String targetType;

	@NotNull(message = "目标ID不能为空")
	private Long targetId;

	/** 1-只读，2-可写 */
	private Integer accessLevel;

	/** ISO 或 yyyy-MM-dd'T'HH:mm；空=长期 */
	private String expireTime;

	private String password;

	private String remark;

}
