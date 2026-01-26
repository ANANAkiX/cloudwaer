package com.cloudwaer.common.scanner.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * API信息DTO
 *
 * @author cloudwaer
 */
@Data
public class ApiInfo implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 请求方法（GET, POST, PUT, DELETE等）
	 */
	private String method;

	/**
	 * API路径
	 */
	private String path;

	/**
	 * 完整路径（包含类上的@RequestMapping）
	 */
	private String fullPath;

	/**
	 * 方法名称
	 */
	private String methodName;

	/**
	 * 类名称
	 */
	private String className;

	/**
	 * 建议的权限自动生成代码 (或许能用上)
	 */
	private String permissionCode;

	private String serviceId;

	private String apiId;

	/**
	 * 描述（从@Operation或注释中获取）
	 */
	private String description;

}
