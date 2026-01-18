package com.cloudwaer.admin.serve.controller;

import com.cloudwaer.admin.api.dto.ServiceApiInfo;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.scanner.dto.ApiInfo;
import com.cloudwaer.common.scanner.service.ApiRegistryService;
import com.cloudwaer.common.scanner.service.ApiScannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务API控制器（用于级联选择器）
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/admin/service-api")
@Tag(name = "服务API管理", description = "获取各服务的API接口列表（用于级联选择器）")
public class ServiceApiController {

    @Autowired(required = false)
    private ApiRegistryService apiRegistryService;

    @Autowired(required = false)
    private ApiScannerService apiScannerService;

    /**
     * 获取所有服务及其API列表（用于级联选择器）
     * 从Redis获取所有已注册服务的API
     *
     * @return 服务API信息列表
     */
    @GetMapping("/services")
    @Operation(summary = "获取所有服务及其API列表", description = "从Redis获取所有已注册服务的API接口，用于级联选择器")
    public Result<List<ServiceApiInfo>> getAllServiceApis() {
        List<ServiceApiInfo> serviceApiList = new ArrayList<>();

        if (apiRegistryService == null) {
            log.warn("ApiRegistryService未配置，无法获取服务API列表");
            return Result.success(serviceApiList);
        }

        // 从Redis获取所有服务的API
        Map<String, List<ApiInfo>> allServiceApis = apiRegistryService.getAllServiceApis();

        // 转换为ServiceApiInfo列表
        for (Map.Entry<String, List<ApiInfo>> entry : allServiceApis.entrySet()) {
            String serviceId = entry.getKey();
            List<ApiInfo> apis = entry.getValue();

            ServiceApiInfo serviceApiInfo = new ServiceApiInfo();
            serviceApiInfo.setServiceName(serviceId);
            serviceApiInfo.setServiceLabel(getServiceLabel(serviceId));
            serviceApiInfo.setApis(apis); // 直接使用，无需转换

            serviceApiList.add(serviceApiInfo);
        }

        log.debug("获取所有服务API列表成功: serviceCount={}", serviceApiList.size());
        return Result.success(serviceApiList);
    }

    /**
     * 获取指定服务的API列表
     *
     * @param serviceName 服务名称
     * @return API信息列表
     */
    @GetMapping("/apis")
    @Operation(summary = "获取指定服务的API列表", description = "根据服务名称从Redis获取该服务的API接口列表")
    public Result<List<ApiInfo>> getServiceApis(@RequestParam String serviceName) {
        if (apiRegistryService == null) {
            log.warn("ApiRegistryService未配置，无法获取服务API: {}", serviceName);
            return Result.success(new ArrayList<>());
        }

        // 从Redis获取指定服务的API
        List<ApiInfo> apis = apiRegistryService.getServiceApis(serviceName);

        return Result.success(apis);
    }

    /**
     * 获取服务显示名称
     */
    private String getServiceLabel(String serviceName) {
        switch (serviceName) {
            case "cloudwaer-admin-serve":
                return "Admin管理服务";
            case "cloudwaer-authentication":
                return "认证授权服务";
            case "cloudwaer-integration-serve":
                return "集成服务";
            case "cloudwaer-flowable-serve":
                return "流程服务";
            case "cloudwaer-codegen-serve":
                return "代码生成服务";
            default:
                return serviceName;
        }
    }
}

