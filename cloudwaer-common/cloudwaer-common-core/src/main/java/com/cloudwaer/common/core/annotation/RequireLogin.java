package com.cloudwaer.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录校验注解 标注在Controller方法上：仅要求用户已登录（Token有效并能解析出用户ID），不校验具体权限码。
 * 用于"当前用户"类自服务接口（查询自己的信息、路由、权限），任何登录用户均可访问。
 *
 * @author cloudwaer
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireLogin {

}
