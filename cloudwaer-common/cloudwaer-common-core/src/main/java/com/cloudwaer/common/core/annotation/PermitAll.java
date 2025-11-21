package com.cloudwaer.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许所有访问的注解
 * 标注了该注解的类或方法，将跳过权限验证，直接放行
 *
 * @author cloudwaer
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermitAll {
}

