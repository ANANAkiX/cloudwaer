package com.cloudwaer.admin.serve.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时设置快照自愈：Redis 重启丢快照后，用库中 published_value 重建（草稿不生效）。
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class SysSettingSnapshotHealRunner implements ApplicationRunner {

	private final SysSettingService sysSettingService;

	public SysSettingSnapshotHealRunner(SysSettingService sysSettingService) {
		this.sysSettingService = sysSettingService;
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			if (sysSettingService.healSnapshotIfMissing()) {
				log.info("启动自愈：设置快照已从数据库重建");
			}
		}
		catch (Exception e) {
			log.warn("设置快照启动自愈失败（可手动「发布设置」恢复）: {}", e.getMessage());
		}
	}

}
