package com.cloudwaer.admin.serve.config;

import com.cloudwaer.admin.serve.service.PermissionService;
import com.cloudwaer.common.core.service.PermissionCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 权限初始化服务（在admin服务中使用） 在应用启动时，从数据库加载权限信息并缓存到Redis
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class PermissionInitService implements CommandLineRunner {

	@Autowired(required = false)
	private PermissionCacheService permissionCacheService;

	@Autowired
	private PermissionService permissionService;

	@Override
	public void run(String... args) {
		if (permissionCacheService == null) {
			log.warn("PermissionCacheService未配置，跳过权限缓存初始化");
			return;
		}

		try {
			log.info("开始初始化权限缓存...");

			// 从数据库加载权限映射
			Map<String, String> permissionMap = permissionService.getPermissionApiMapping();

			if (permissionMap != null && !permissionMap.isEmpty()) {
				permissionCacheService.cachePermissions(permissionMap);
				log.info("权限缓存初始化完成，共缓存 {} 个权限映射", permissionMap.size());
			}
			else {
				log.warn("未加载到权限信息，权限缓存为空");
			}
		}
		catch (Exception e) {
			log.error("权限缓存初始化失败", e);
		}
	}

}
