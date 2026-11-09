package com.cloudwaer.workflow.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.workflow.serve.engine.listener.ListenerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 监听器目录控制器
 */
@RestController
@RequestMapping("/workflow/listener")
@Tag(name = "监听器目录")
public class ListenerCatalogController {

	@Autowired
	private ListenerRegistry listenerRegistry;

	@GetMapping("/catalog")
	@PermissionCode("workflow:def:detail")
	@Operation(summary = "获取可用监听器列表（供设计器下拉）")
	public Result<Map<String, String>> catalog() {
		return Result.success(listenerRegistry.getAvailableListeners());
	}

}
