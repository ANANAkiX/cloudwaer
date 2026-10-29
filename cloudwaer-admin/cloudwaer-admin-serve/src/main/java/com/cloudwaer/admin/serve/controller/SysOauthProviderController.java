package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.serve.entity.SysOauthProvider;
import com.cloudwaer.admin.serve.service.SysOauthProviderService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 第三方登录供应商管理（可配置多组）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/oauth-provider")
@Tag(name = "第三方登录", description = "第三方登录供应商管理（多组；登录流程本体暂未开放）")
public class SysOauthProviderController {

	@Autowired
	private SysOauthProviderService sysOauthProviderService;

	@GetMapping("/list")
	@PermissionCode("admin:oauth:list")
	@Operation(summary = "查询第三方登录供应商", description = "Client Secret 脱敏返回")
	public Result<List<SysOauthProvider>> list() {
		return Result.success(sysOauthProviderService.list());
	}

	@PostMapping("/save")
	@PermissionCode("admin:oauth:save")
	@Operation(summary = "保存第三方登录供应商", description = "新增或更新；Secret 留空保持原值")
	public Result<Boolean> save(@RequestBody SysOauthProvider provider) {
		return Result.success(sysOauthProviderService.save(provider));
	}

	@DeleteMapping("/{id}")
	@PermissionCode("admin:oauth:delete")
	@Operation(summary = "删除第三方登录供应商")
	public Result<Boolean> delete(@PathVariable("id") Long id) {
		return Result.success(sysOauthProviderService.delete(id));
	}

}
