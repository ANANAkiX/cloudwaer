package com.cloudwaer.common.core.config;

import com.cloudwaer.common.core.service.PermissionCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限初始化配置
 * 
 * 注意：此配置类已废弃，请使用admin服务中的PermissionInitService
 * admin服务启动时直接从数据库加载权限并缓存到Redis
 *
 * @author cloudwaer
 * @deprecated 请使用admin服务中的PermissionInitService
 */
@Deprecated
@Slf4j
// @Component  // 已禁用，使用admin服务中的PermissionInitService
public class PermissionInitConfig implements CommandLineRunner {

    @Autowired(required = false)
    private PermissionCacheService permissionCacheService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        if (permissionCacheService == null) {
            log.warn("PermissionCacheService未配置，跳过权限缓存初始化");
            return;
        }

        try {
            log.info("开始初始化权限缓存...");
            
            // 从数据库加载权限信息（通过Feign调用admin服务）
            Map<String, String> permissionMap = loadPermissionsFromDatabase();
            
            if (permissionMap != null && !permissionMap.isEmpty()) {
                permissionCacheService.cachePermissions(permissionMap);
                log.info("权限缓存初始化完成，共缓存 {} 个权限映射", permissionMap.size());
            } else {
                log.warn("未加载到权限信息，权限缓存为空");
            }
        } catch (Exception e) {
            log.error("权限缓存初始化失败", e);
        }
    }

    /**
     * 从数据库加载权限信息
     * 通过Feign调用admin服务获取权限信息
     * 注意：需要在auth服务中注入AdminFeignClient，这里使用反射方式获取
     */
    private Map<String, String> loadPermissionsFromDatabase() {
        try {
            // 等待一段时间，确保Feign客户端已初始化
            Thread.sleep(3000);
            
            // 尝试获取AdminFeignClient Bean（通过类型查找）
            Map<String, Object> feignClients = applicationContext.getBeansOfType(Object.class);
            Object feignClient = null;
            
            for (Object bean : feignClients.values()) {
                // 检查是否是AdminFeignClient（通过类名判断）
                if (bean.getClass().getName().contains("AdminFeignClient")) {
                    feignClient = bean;
                    break;
                }
            }
            
            if (feignClient == null) {
                log.warn("AdminFeignClient未找到，跳过权限缓存初始化（可能admin服务未启动）");
                return new HashMap<>();
            }
            
            // 调用Feign客户端获取权限映射
            try {
                java.lang.reflect.Method method = feignClient.getClass().getMethod("getPermissionApiMapping");
                Object result = method.invoke(feignClient);
                
                if (result != null) {
                    // 检查是否是Result类型
                    if (result.getClass().getName().contains("Result")) {
                        // 使用反射获取code和data字段
                        java.lang.reflect.Field codeField = result.getClass().getDeclaredField("code");
                        codeField.setAccessible(true);
                        Integer code = (Integer) codeField.get(result);
                        
                        if (code != null && code == 200) {
                            java.lang.reflect.Field dataField = result.getClass().getDeclaredField("data");
                            dataField.setAccessible(true);
                            Object data = dataField.get(result);
                            
                            if (data instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, String> permissionMap = (Map<String, String>) data;
                                return permissionMap;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("调用AdminFeignClient.getPermissionApiMapping失败", e);
            }
        } catch (Exception e) {
            log.error("加载权限信息失败", e);
        }
        
        return new HashMap<>();
    }
}

