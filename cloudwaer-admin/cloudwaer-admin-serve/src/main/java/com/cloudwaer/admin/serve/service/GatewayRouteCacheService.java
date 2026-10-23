package com.cloudwaer.admin.serve.service;

import com.cloudwaer.common.core.util.RedisUtil;
import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网关路由缓存服务 将网关路由信息缓存到Redis
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class GatewayRouteCacheService {

	@Autowired(required = false)
	private RedisUtil redisUtil;

	/**
	 * Redis Key
	 */
	private static final String GATEWAY_ROUTE_CACHE_KEY = "cloudwaer:gateway:route:cache";

	/**
	 * 缓存网关路由列表到Redis
	 * @param routes 网关路由列表
	 */
	public void cacheRoutes(List<GatewayRouteDTO> routes) {
		if (redisUtil == null || !redisUtil.isAvailable()) {
			log.warn("Redis未配置，无法缓存网关路由信息");
			return;
		}

		if (routes == null) {
			routes = new ArrayList<>();
		}

		try {
			// 将路由列表序列化为JSON并存储到Redis
			redisUtil.setObject(GATEWAY_ROUTE_CACHE_KEY, routes, 30, TimeUnit.DAYS);

			log.info("网关路由缓存成功: 缓存了 {} 个路由", routes.size());
		}
		catch (Exception e) {
			log.error("缓存网关路由信息失败", e);
		}
	}

	/**
	 * 从Redis获取网关路由列表
	 * @return 网关路由列表，如果不存在或出错则返回null
	 */
	public List<GatewayRouteDTO> getRoutesFromCache() {
		if (redisUtil == null || !redisUtil.isAvailable()) {
			return null;
		}

		try {
			List<GatewayRouteDTO> routes = redisUtil.getObject(GATEWAY_ROUTE_CACHE_KEY,
					new TypeReference<List<GatewayRouteDTO>>() {
					});

			log.debug("从Redis获取网关路由: {} 个路由", routes != null ? routes.size() : 0);
			return routes;
		}
		catch (Exception e) {
			log.error("从Redis获取网关路由失败", e);
			return null;
		}
	}

	/**
	 * 清除网关路由缓存
	 */
	public void clearCache() {
		if (redisUtil != null && redisUtil.isAvailable()) {
			try {
				redisUtil.delete(GATEWAY_ROUTE_CACHE_KEY);
				log.info("网关路由缓存已清除");
			}
			catch (Exception e) {
				log.error("清除网关路由缓存失败", e);
			}
		}
	}

}
