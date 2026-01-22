package com.cloudwaer.common.core.exception;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author cloudwaer
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 处理参数校验异常
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
	public Result<?> handleValidationException(Exception e) {
		String message = "参数校验失败";
		if (e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
			message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		}
		else if (e instanceof BindException) {
			BindException ex = (BindException) e;
			message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		}
		log.error("参数校验异常: {}", message);
		return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
	}

	/**
	 * 处理业务异常
	 */
	@ExceptionHandler(BusinessException.class)
	public Result<?> handleBusinessException(BusinessException e) {
		log.error("业务异常: {}", e.getMessage());
		return Result.fail(e.getCode(), e.getMessage());
	}

	/**
	 * 处理资源未找到异常（如静态资源、不存在的接口路径等）
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
		// 记录为 WARN 级别，因为这不是真正的错误，可能是浏览器或工具尝试访问不存在的资源
		log.warn("资源未找到: {} - {}", e.getResourcePath(), e.getMessage());
		return Result.fail(ResultCode.NOT_FOUND.getCode(), "请求的资源不存在");
	}

	/**
	 * 处理其他异常
	 */
	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e) {
		log.error("系统异常", e);
		return Result.fail(ResultCode.FAIL);
	}

}
