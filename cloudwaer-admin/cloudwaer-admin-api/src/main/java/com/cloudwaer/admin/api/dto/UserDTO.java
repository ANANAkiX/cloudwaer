package com.cloudwaer.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户DTO
 *
 * @author cloudwaer
 */
@Data
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID（序列化为字符串，避免前端精度丢失）
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 角色ID列表
	 */
	private List<Long> roleIds;

}
