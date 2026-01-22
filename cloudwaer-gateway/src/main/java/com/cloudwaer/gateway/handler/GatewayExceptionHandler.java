package com.cloudwaer.gateway.handler;

import com.cloudwaer.common.core.exception.GlobalExceptionHandler;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 网关异常处理器 优先级高于GlobalExceptionHandler
 *
 * @author cloudwaer
 */
@RestControllerAdvice
@Order(-1)
@Slf4j
public class GatewayExceptionHandler extends GlobalExceptionHandler {

	/**
	 * 处理找不到服务异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(NotFoundException.class)
	public Result<?> handleNoResourceFoundException(NotFoundException e) {
		// 记录为 WARN 级别，因为这不是真正的错误，可能是浏览器或工具尝试访问不存在的资源
		log.warn("服务未找到: {} - {}", e.getMessage());
		return Result.fail(ResultCode.NOT_SERVE_FOUND.getCode(), ResultCode.NOT_SERVE_FOUND.getMessage());
	}

	/**
	 * 处理其他异常
	 */
	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e) {
		// 最常见额外处理路径404
		Class<? extends Exception> aClass = e.getClass();
		log.warn("路径未找到: {}", e.getMessage());
		if (aClass.equals(org.springframework.web.reactive.resource.NoResourceFoundException.class)) {
			return Result.fail(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMessage());
		}
		log.error("系统异常", e);
		return super.handleException(e);
	}

}
