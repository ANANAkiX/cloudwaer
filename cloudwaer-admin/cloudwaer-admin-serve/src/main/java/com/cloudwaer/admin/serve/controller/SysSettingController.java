package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.serve.dto.SettingItemDTO;
import com.cloudwaer.admin.serve.dto.SettingSaveItem;
import com.cloudwaer.admin.serve.service.SysSettingService;
import com.cloudwaer.common.core.annotation.PermissionCode;
import com.cloudwaer.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 全局动态设置（服务器设置）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/setting")
@Tag(name = "服务器设置", description = "全局动态设置：保存为草稿，发布后各实例实时生效")
public class SysSettingController {

	@Autowired
	private SysSettingService sysSettingService;

	@GetMapping("/list")
	@PermissionCode("admin:setting:list")
	@Operation(summary = "查询全局设置", description = "含生效值、来源（DB/YML/DEFAULT）与待发布标记")
	public Result<List<SettingItemDTO>> list() {
		return Result.success(sysSettingService.listItems());
	}

	@PutMapping("/save")
	@PermissionCode("admin:setting:save")
	@Operation(summary = "保存全局设置", description = "只写库不生效，需发布后各实例切换")
	public Result<Boolean> save(@RequestBody List<SettingSaveItem> items) {
		return Result.success(sysSettingService.save(items));
	}

	@PutMapping("/reset")
	@PermissionCode("admin:setting:reset")
	@Operation(summary = "恢复默认", description = "清空已保存值，回到 yml/代码兜底；需发布后生效")
	public Result<Boolean> reset(@RequestBody List<String> keys) {
		return Result.success(sysSettingService.reset(keys));
	}

	@PostMapping("/publish")
	@PermissionCode("admin:setting:publish")
	@Operation(summary = "发布设置", description = "打包写入 Redis 快照并广播，各实例立即刷新（无需重启）")
	public Result<Map<String, Object>> publish() {
		return Result.success(sysSettingService.publish());
	}

	@PostMapping("/password-transport/generate-keys")
	@PermissionCode("admin:setting:save")
	@Operation(summary = "生成密码加密密钥对", description = "生成 RSA-2048 密钥对写入设置（草稿，发布后生效）")
	public Result<Map<String, Object>> generatePasswordTransportKeys() {
		return Result.success(sysSettingService.generatePasswordTransportKeys());
	}

}
