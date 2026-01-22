package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.scanner.dto.ApiInfo;
import com.cloudwaer.common.scanner.service.ApiScannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API扫描控制器（Admin服务）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/api-scanner")
@Tag(name = "API扫描", description = "API接口扫描接口（Admin服务）")
public class ApiScannerController {

	@Autowired(required = false)
	private ApiScannerService apiScannerService;

	/**
	 * 获取当前服务的所有API接口列表
	 * @return API信息列表
	 */
	@GetMapping("/apis")
	@Operation(summary = "获取当前服务的所有API接口", description = "扫描并返回当前服务（Admin）的所有可用API接口列表")
	public Result<List<ApiInfo>> getAllApis() {
		if (apiScannerService == null) {
			return Result.fail(500, "API扫描服务未启用");
		}
		List<ApiInfo> apis = apiScannerService.getAllApis();
		return Result.success(apis);
	}

	/**
	 * 根据请求方法获取API接口列表
	 * @param method 请求方法（GET, POST, PUT, DELETE）
	 * @return API信息列表
	 */
	@GetMapping("/apis/method")
	@Operation(summary = "根据请求方法获取API接口", description = "根据请求方法过滤当前服务的API接口列表")
	public Result<List<ApiInfo>> getApisByMethod(@RequestParam String method) {
		if (apiScannerService == null) {
			return Result.fail(500, "API扫描服务未启用");
		}
		List<ApiInfo> apis = apiScannerService.getApisByMethod(method);
		return Result.success(apis);
	}

}
