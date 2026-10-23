package com.cloudwaer.flowable.serve.controller;

import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.flowable.serve.listener.EventListenerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 监听器元数据：供设计器下拉选择实现名。
 */
@RestController
@RequestMapping("/flowable/listener")
@Tag(name = "Flowable Listener Catalog")
public class WfListenerCatalogController {

	@Autowired(required = false)
	private EventListenerRegistry eventListenerRegistry;

	@GetMapping("/catalog")
	@PermissionCode(value = "flowable:model:list")
	@Operation(summary = "List available listener implementations")
	public Result<Map<String, List<String>>> catalog() {
		Map<String, List<String>> data = new LinkedHashMap<>();
		if (eventListenerRegistry == null) {
			return Result.success(data);
		}
		data.put("taskListeners", eventListenerRegistry.listTaskListenerNames());
		data.put("executionListeners", eventListenerRegistry.listExecutionListenerNames());
		return Result.success(data);
	}

}
