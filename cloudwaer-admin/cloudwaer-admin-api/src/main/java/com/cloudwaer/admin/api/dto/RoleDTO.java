package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色DTO
 *
 * @author cloudwaer
 */
@Data
public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID（序列化为字符串，避免前端精度丢失）
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 角色编码
	 */
	private String roleCode;

	/**
	 * 角色描述
	 */
	private String description;

	/**
	 * 权限ID列表
	 */
	private List<Long> permissionIds;

}
