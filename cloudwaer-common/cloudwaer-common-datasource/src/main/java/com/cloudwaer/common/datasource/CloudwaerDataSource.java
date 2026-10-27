package com.cloudwaer.common.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 显式指定动态数据源路由键。
 *
 * <p>
 * 默认按约定解析（{@code com.cloudwaer.<key>.serve} / 请求路径 {@code /<key>/}）；命名不符合约定时用本注解覆盖。
 * 优先级：方法注解 &gt; 类注解 &gt; 包名/路径约定。
 * </p>
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudwaerDataSource {

	/** 数据源路由键，对应 yml 中 dynamic datasource 名称 */
	String value();

}
