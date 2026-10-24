package com.cloudwaer.blog.serve.controller;

import com.cloudwaer.blog.api.dto.SiteSettingDTO;
import com.cloudwaer.blog.serve.service.BlogSettingService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客站点设置控制器（管理端）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/blog/setting")
@Tag(name = "博客站点设置", description = "站点标题/图标/搜索/布局等配置管理")
public class BlogSettingController {

	@Autowired
	private BlogSettingService blogSettingService;

	/**
	 * 读取站点设置
	 */
	@GetMapping
	@PermissionCode("blog:setting:view")
	@Operation(summary = "读取设置", description = "返回扁平化站点配置")
	public Result<SiteSettingDTO> getSetting() {
		return Result.success(blogSettingService.getSettings());
	}

	/**
	 * 保存站点设置
	 */
	@PostMapping("/save")
	@PermissionCode("blog:setting:edit")
	@Operation(summary = "保存设置", description = "按 key upsert 站点配置")
	public Result<Boolean> saveSetting(@RequestBody @Validated SiteSettingDTO dto) {
		return Result.success(blogSettingService.saveSettings(dto));
	}

}
