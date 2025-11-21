package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.admin.serve.entity.GatewayRoute;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 网关路由服务接口
 *
 * @author cloudwaer
 */
public interface GatewayRouteService extends IService<GatewayRoute> {

    /**
     * 获取所有有效的网关路由（用于网关服务加载路由，优先从Redis加载）
     *
     * @return 网关路由列表
     */
    List<GatewayRouteDTO> getAllRoutes();

    /**
     * 分页查询网关路由列表
     *
     * @param pageDTO 分页参数
     * @return 分页结果
     */
    PageResult<GatewayRouteDTO> getRoutesByPage(PageDTO pageDTO);

    /**
     * 根据ID获取网关路由
     *
     * @param id 路由ID
     * @return 网关路由DTO
     */
    GatewayRouteDTO getRouteById(Long id);

    /**
     * 保存网关路由
     *
     * @param routeDTO 网关路由DTO
     * @return 是否成功
     */
    Boolean saveRoute(GatewayRouteDTO routeDTO);

    /**
     * 更新网关路由
     *
     * @param routeDTO 网关路由DTO
     * @return 是否成功
     */
    Boolean updateRoute(GatewayRouteDTO routeDTO);

    /**
     * 删除网关路由
     *
     * @param id 路由ID
     * @return 是否成功
     */
    Boolean deleteRoute(Long id);

    /**
     * 刷新网关路由（从数据库查询最新数据并更新到Redis，然后通知网关重新加载路由）
     *
     * @return 是否成功
     */
    Boolean refreshRoutes();
}

