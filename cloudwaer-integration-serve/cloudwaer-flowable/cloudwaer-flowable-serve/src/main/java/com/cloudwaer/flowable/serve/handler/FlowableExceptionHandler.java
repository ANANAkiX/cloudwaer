package com.cloudwaer.flowable.serve.handler;

import com.cloudwaer.common.core.exception.GlobalExceptionHandler;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
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
public class FlowableExceptionHandler extends GlobalExceptionHandler {

	/**
	 * 处理流程错误异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(FlowableException.class)
	public Result<?> handleNoResourceFoundException(FlowableException e) {
		log.error("流程出错 - {}", e.getMessage());
		return Result.fail(ResultCode.FLOWABLE_ERROR.getCode(), ResultCode.FLOWABLE_ERROR.getMessage());
	}

}
