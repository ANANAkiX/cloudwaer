package com.cloudwaer.common.scanner.util;

import com.cloudwaer.common.core.constant.HttpMethodConstants;
import com.cloudwaer.common.scanner.config.ApiScannerProperties;
import com.cloudwaer.common.scanner.dto.ApiInfo;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * API接口扫描器
 *
 * @author cloudwaer
 */
public class ApiScanner {

	private final ApiScannerProperties properties;

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	public ApiScanner(ApiScannerProperties properties) {
		this.properties = properties;
	}

	/**
	 * 扫描指定包下的所有Controller接口
	 * @param controllerClasses Controller类集合
	 * @return API信息列表
	 */
	public List<ApiInfo> scanControllers(Set<Class<?>> controllerClasses) {
		List<ApiInfo> apiInfoList = new ArrayList<>();

		for (Class<?> controllerClass : controllerClasses) {
			// 获取类上的@RequestMapping注解
			RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
			String classPath = "";
			if (classMapping != null && classMapping.value().length > 0) {
				classPath = normalizePath(classMapping.value()[0]);
			}

			// 扫描类中的所有方法（只扫描声明的方法，不包括继承的方法）
			Method[] methods = controllerClass.getDeclaredMethods();
			for (Method method : methods) {
				// 跳过私有方法
				if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) {
					continue;
				}
				ApiInfo apiInfo = extractApiInfo(controllerClass, method, classPath);
				if (apiInfo != null && !shouldExclude(apiInfo)) {
					apiInfoList.add(apiInfo);
				}
			}
		}

		return apiInfoList;
	}

	/**
	 * 提取方法的API信息
	 */
	private ApiInfo extractApiInfo(Class<?> controllerClass, Method method, String classPath) {
		// 检查方法是否有HTTP映射注解
		String httpMethod = null;
		String methodPath = "";

		if (method.isAnnotationPresent(GetMapping.class)) {
			httpMethod = HttpMethodConstants.GET;
			GetMapping mapping = method.getAnnotation(GetMapping.class);
			if (mapping.value().length > 0) {
				methodPath = normalizePath(mapping.value()[0]);
			}
		}
		else if (method.isAnnotationPresent(PostMapping.class)) {
			httpMethod = HttpMethodConstants.POST;
			PostMapping mapping = method.getAnnotation(PostMapping.class);
			if (mapping.value().length > 0) {
				methodPath = normalizePath(mapping.value()[0]);
			}
		}
		else if (method.isAnnotationPresent(PutMapping.class)) {
			httpMethod = HttpMethodConstants.PUT;
			PutMapping mapping = method.getAnnotation(PutMapping.class);
			if (mapping.value().length > 0) {
				methodPath = normalizePath(mapping.value()[0]);
			}
		}
		else if (method.isAnnotationPresent(DeleteMapping.class)) {
			httpMethod = HttpMethodConstants.DELETE;
			DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
			if (mapping.value().length > 0) {
				methodPath = normalizePath(mapping.value()[0]);
			}
		}
		else if (method.isAnnotationPresent(RequestMapping.class)) {
			RequestMapping mapping = method.getAnnotation(RequestMapping.class);
			if (mapping.method().length > 0) {
				httpMethod = mapping.method()[0].name();
			}
			else {
				// 如果没有指定方法，默认支持所有方法，这里取第一个
				httpMethod = HttpMethodConstants.GET;
			}
			if (mapping.value().length > 0) {
				methodPath = normalizePath(mapping.value()[0]);
			}
		}
		else {
			// 没有HTTP映射注解，跳过
			return null;
		}

		// 构建完整路径
		String fullPath = combinePath(classPath, methodPath);

		ApiInfo apiInfo = new ApiInfo();
		apiInfo.setMethod(httpMethod);
		apiInfo.setPath(methodPath);
		apiInfo.setFullPath(fullPath);
		apiInfo.setMethodName(method.getName());
		apiInfo.setClassName(controllerClass.getSimpleName());

		// 尝试获取描述（从@Operation注解）
		try {
			if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.Operation.class)) {
				io.swagger.v3.oas.annotations.Operation operation = method
					.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
				if (StringUtils.hasText(operation.summary())) {
					apiInfo.setDescription(operation.summary());
				}
			}
		}
		catch (Exception e) {
			// Swagger可能未引入，忽略
		}

		return apiInfo;
	}

	/**
	 * 规范化路径（确保以/开头，不以/结尾）
	 */
	private String normalizePath(String path) {
		if (!StringUtils.hasText(path)) {
			return "";
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * 合并类路径和方法路径
	 */
	private String combinePath(String classPath, String methodPath) {
		if (!StringUtils.hasText(classPath)) {
			return normalizePath(methodPath);
		}
		if (!StringUtils.hasText(methodPath)) {
			return normalizePath(classPath);
		}
		String combined = normalizePath(classPath) + normalizePath(methodPath);
		// 处理重复的斜杠
		return combined.replaceAll("/+", "/");
	}

	/**
	 * 判断是否应该排除该API
	 */
	private boolean shouldExclude(ApiInfo apiInfo) {
		// 检查排除的方法名称
		if (properties.getExcludeMethods() != null) {
			for (String excludeMethod : properties.getExcludeMethods()) {
				if (apiInfo.getMethodName().equals(excludeMethod) || apiInfo.getMethodName().contains(excludeMethod)) {
					return true;
				}
			}
		}

		// 检查排除的路径模式
		if (properties.getExcludePaths() != null) {
			for (String excludePath : properties.getExcludePaths()) {
				if (pathMatcher.match(excludePath, apiInfo.getFullPath())) {
					return true;
				}
			}
		}

		return false;
	}

}
