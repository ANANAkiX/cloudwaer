package com.cloudwaer.common.scanner.service;

import com.cloudwaer.common.scanner.config.ApiScannerProperties;
import com.cloudwaer.common.scanner.dto.ApiInfo;
import com.cloudwaer.common.scanner.util.ApiScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API扫描服务
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class ApiScannerService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private ApiScannerProperties properties;

    @Autowired(required = false)
    private ApiRegistryService apiRegistryService;

    private List<ApiInfo> cachedApiList = new ArrayList<>();
    private ApiScanner apiScanner;

    @PostConstruct
    public void init() {
        if (properties == null) {
            log.warn("ApiScannerProperties未配置，API扫描功能可能无法正常工作");
            return;
        }

        if (!StringUtils.hasText(properties.getServiceId())) {
            log.error("服务ID未配置，请在配置文件中设置 cloudwaer.api-scanner.service-id");
            return;
        }

        apiScanner = new ApiScanner(properties);
        scanApis();
        
        // 注册服务API到Redis
        if (apiRegistryService != null) {
            apiRegistryService.registerServiceApis(properties.getServiceId(), cachedApiList);
            log.info("服务API注册完成: serviceId={}, apiCount={}", properties.getServiceId(), cachedApiList.size());
        } else {
            log.warn("ApiRegistryService未配置，服务API未注册到Redis");
        }
    }

    @PreDestroy
    public void destroy() {
        // 服务关闭时，注销服务API
        if (apiRegistryService != null && properties != null && StringUtils.hasText(properties.getServiceId())) {
            apiRegistryService.unregisterServiceApis(properties.getServiceId());
        }
    }

    /**
     * 扫描所有API接口
     */
    public void scanApis() {
        Set<Class<?>> controllerClasses = findControllerClasses();
        cachedApiList = apiScanner.scanControllers(controllerClasses);
    }

    /**
     * 获取所有API接口列表
     *
     * @return API信息列表
     */
    public List<ApiInfo> getAllApis() {
        if (cachedApiList.isEmpty()) {
            scanApis();
        }
        return new ArrayList<>(cachedApiList);
    }

    /**
     * 根据请求方法过滤API
     *
     * @param method 请求方法（GET, POST, PUT, DELETE）
     * @return API信息列表
     */
    public List<ApiInfo> getApisByMethod(String method) {
        return getAllApis().stream()
                .filter(api -> api.getMethod().equalsIgnoreCase(method))
                .collect(Collectors.toList());
    }

    /**
     * 查找所有Controller类
     */
    private Set<Class<?>> findControllerClasses() {
        Set<Class<?>> controllerClasses = new HashSet<>();
        
        // 获取所有Bean的名称
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        
        for (String beanName : beanNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                Class<?> beanClass = bean.getClass();
                
                // 获取实际的类（可能是代理类，需要获取原始类）
                Class<?> targetClass = getTargetClass(beanClass);
                
                // 检查是否是Controller（有@RestController或@Controller注解）
                if (targetClass.isAnnotationPresent(RestController.class) ||
                    targetClass.isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
                    
                    // 检查是否在扫描范围内
                    if (shouldScanClass(targetClass)) {
                        controllerClasses.add(targetClass);
                    }
                }
            } catch (Exception e) {
                // 忽略无法获取的Bean
            }
        }
        
        return controllerClasses;
    }

    /**
     * 获取目标类（处理代理类）
     */
    private Class<?> getTargetClass(Class<?> clazz) {
        // 如果是CGLIB代理类，获取父类
        if (clazz.getName().contains("$$")) {
            return clazz.getSuperclass();
        }
        return clazz;
    }

    /**
     * 判断是否应该扫描该类
     */
    private boolean shouldScanClass(Class<?> clazz) {
        if (properties == null || properties.getBasePackages() == null || properties.getBasePackages().isEmpty()) {
            return true;
        }
        
        String packageName = clazz.getPackage().getName();
        for (String basePackage : properties.getBasePackages()) {
            if (packageName.startsWith(basePackage)) {
                return true;
            }
        }
        
        return false;
    }
}

