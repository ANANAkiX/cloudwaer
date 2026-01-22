package com.cloudwaer.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户ID查询请求DTO
 *
 * @author cloudwaer
 */
@Data
@Schema(description = "用户ID查询请求")
public class UserIdQueryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@NotNull(message = "用户ID不能为空")
	@Schema(description = "用户ID", required = true, example = "1")
	private Long userId;

}
