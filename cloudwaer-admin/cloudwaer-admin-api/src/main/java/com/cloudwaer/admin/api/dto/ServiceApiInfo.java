package com.cloudwaer.admin.api.dto;

import com.cloudwaer.common.scanner.dto.ApiInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务API信息DTO（用于级联选择器）
 *
 * @author cloudwaer
 */
@Data
public class ServiceApiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务显示名称
     */
    private String serviceLabel;

    /**
     * API列表（使用统一的ApiInfo）
     */
    private List<ApiInfo> apis;

    /**
     * 子服务（用于级联选择器）
     */
    private List<ServiceApiInfo> children;
}

