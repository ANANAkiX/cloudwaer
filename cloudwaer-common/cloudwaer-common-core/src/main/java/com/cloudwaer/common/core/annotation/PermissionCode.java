package com.cloudwaer.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限代码注解 标注在Controller方法上，用于标识该接口需要的权限代码 权限代码对应 sys_permission 表中的 permission_code 列
 *
 * @author cloudwaer
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionCode {

	/**
	 * 权限代码 对应 sys_permission.permission_code
	 * @return 权限代码
	 */
	String value() default "";

	/**
	 * 是否启用权限验证 true: 需要权限验证 false: 跳过权限验证（相当于原来的 @PermitAll）
	 * @return 是否启用权限验证
	 */
	boolean enable() default true;

}
