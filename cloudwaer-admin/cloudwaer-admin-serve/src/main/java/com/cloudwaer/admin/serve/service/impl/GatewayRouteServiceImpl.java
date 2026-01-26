package com.cloudwaer.admin.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.admin.serve.config.GatewayRefreshProperties;
import com.cloudwaer.admin.serve.entity.GatewayRoute;
import com.cloudwaer.admin.serve.mapper.GatewayRouteMapper;
import com.cloudwaer.admin.serve.service.GatewayRouteCacheService;
import com.cloudwaer.admin.serve.service.GatewayRouteService;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网关路由服务实现类
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class GatewayRouteServiceImpl extends ServiceImpl<GatewayRouteMapper, GatewayRoute>
		implements GatewayRouteService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired(required = false)
	private DiscoveryClient discoveryClient;

	@Autowired(required = false)
	private RestTemplate restTemplate;

	@Autowired(required = false)
	private GatewayRouteCacheService gatewayRouteCacheService;

	@Autowired
	private GatewayRefreshProperties gatewayRefreshProperties;

	@Override
	public List<GatewayRouteDTO> getAllRoutes() {
		// 优先从Redis加载
		if (gatewayRouteCacheService != null) {
			List<GatewayRouteDTO> cachedRoutes = gatewayRouteCacheService.getRoutesFromCache();
			if (cachedRoutes != null && !cachedRoutes.isEmpty()) {
				log.debug("从Redis加载网关路由: {} 个路由", cachedRoutes.size());
				return cachedRoutes;
			}
		}

		// Redis没有数据，从数据库查询
		log.debug("Redis中没有路由数据，从数据库查询");
		LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(GatewayRoute::getStatus, 1).orderByAsc(GatewayRoute::getOrder);
		List<GatewayRoute> routes = this.list(wrapper);
		List<GatewayRouteDTO> routeDTOs = routes.stream().map(this::convertToDTO).collect(Collectors.toList());

		// 将查询结果缓存到Redis
		if (gatewayRouteCacheService != null && !routeDTOs.isEmpty()) {
			gatewayRouteCacheService.cacheRoutes(routeDTOs);
		}

		return routeDTOs;
	}

	@Override
	public PageResult<GatewayRouteDTO> getRoutesByPage(PageDTO pageDTO) {
		// 构建分页对象
		Page<GatewayRoute> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());

		// 构建查询条件
		LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(GatewayRoute::getStatus, 1);

		// 如果有搜索关键词，进行模糊查询
		if (StringUtils.hasText(pageDTO.getKeyword())) {
			wrapper.and(w -> w.like(GatewayRoute::getRouteId, pageDTO.getKeyword())
				.or()
				.like(GatewayRoute::getUri, pageDTO.getKeyword())
				.or()
				.like(GatewayRoute::getDescription, pageDTO.getKeyword()));
		}

		// 按顺序排序
		wrapper.orderByAsc(GatewayRoute::getOrder);

		// 执行分页查询
		IPage<GatewayRoute> pageResult = this.page(page, wrapper);

		// 转换为DTO列表
		List<GatewayRouteDTO> routeDTOs = pageResult.getRecords()
			.stream()
			.map(this::convertToDTO)
			.collect(Collectors.toList());

		// 构建分页结果
		return new PageResult<>(routeDTOs, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
	}

	@Override
	public GatewayRouteDTO getRouteById(Long id) {
		GatewayRoute route = this.getById(id);
		if (route == null) {
			throw new BusinessException("路由不存在");
		}
		return convertToDTO(route);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveRoute(GatewayRouteDTO routeDTO) {
		// 检查路由ID是否已存在
		LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(GatewayRoute::getRouteId, routeDTO.getRouteId());
		long count = this.count(wrapper);
		if (count > 0) {
			throw new BusinessException("路由ID已存在");
		}

		GatewayRoute route = convertToEntity(routeDTO);
		boolean result = this.save(route);

		// 保存成功后，更新Redis缓存
		if (result && gatewayRouteCacheService != null) {
			updateCacheFromDatabase();
		}

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateRoute(GatewayRouteDTO routeDTO) {
		if (routeDTO.getId() == null) {
			throw new BusinessException("路由ID不能为空");
		}

		GatewayRoute route = this.getById(routeDTO.getId());
		if (route == null) {
			throw new BusinessException("路由不存在");
		}

		// 检查路由ID是否已被其他路由使用
		LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(GatewayRoute::getRouteId, routeDTO.getRouteId()).ne(GatewayRoute::getId, routeDTO.getId());
		long count = this.count(wrapper);
		if (count > 0) {
			throw new BusinessException("路由ID已被使用");
		}

		GatewayRoute updatedRoute = convertToEntity(routeDTO);
		updatedRoute.setId(routeDTO.getId());
		boolean result = this.updateById(updatedRoute);

		// 更新成功后，更新Redis缓存
		if (result && gatewayRouteCacheService != null) {
			updateCacheFromDatabase();
		}

		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteRoute(Long id) {
		boolean result = this.removeById(id);

		// 删除成功后，更新Redis缓存
		if (result && gatewayRouteCacheService != null) {
			updateCacheFromDatabase();
		}

		return result;
	}

	@Override
	public Boolean refreshRoutes() {
		try {
			// 1. 从数据库查询最新的路由配置并更新到Redis
			log.info("开始刷新网关路由：从数据库查询最新配置并更新到Redis");
			updateCacheFromDatabase();

			// 2. 查找网关服务实例并通知刷新
			if (discoveryClient == null) {
				log.warn("DiscoveryClient未配置，无法通知网关刷新路由");
				return false;
			}

			List<ServiceInstance> instances = discoveryClient.getInstances(gatewayRefreshProperties.getServiceId());
			if (instances == null || instances.isEmpty()) {
				log.warn("未找到网关服务实例，无法刷新路由");
				//TODO 未找到网关可能是单体应用  单体应用也不需要配置网关路由
				return false;
			}

			// 向所有网关实例发送刷新请求
			boolean allSuccess = true;
			for (ServiceInstance instance : instances) {
				String url = String.format("%s://%s:%s%s", gatewayRefreshProperties.getScheme(), instance.getHost(),
						instance.getPort(), normalizeRefreshPath(gatewayRefreshProperties.getPath()));

				try {
					if (restTemplate == null) {
						restTemplate = new RestTemplate();
					}

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<String> entity = new HttpEntity<>("", headers);

					restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
					log.info("成功刷新网关路由: {}", url);
				}
				catch (Exception e) {
					log.error("刷新网关路由失败: {}, 错误: {}", url, e.getMessage(), e);
					allSuccess = false;
				}
			}

			return allSuccess;
		}
		catch (Exception e) {
			log.error("刷新网关路由失败", e);
			return false;
		}
	}

	private String normalizeRefreshPath(String path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		return path.startsWith("/") ? path : "/" + path;
	}

	/**
	 * 从数据库查询最新路由配置并更新到Redis
	 */
	private void updateCacheFromDatabase() {
		if (gatewayRouteCacheService == null) {
			return;
		}

		try {
			LambdaQueryWrapper<GatewayRoute> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(GatewayRoute::getStatus, 1).orderByAsc(GatewayRoute::getOrder);
			List<GatewayRoute> routes = this.list(wrapper);
			List<GatewayRouteDTO> routeDTOs = routes.stream().map(this::convertToDTO).collect(Collectors.toList());

			// 更新Redis缓存
			gatewayRouteCacheService.cacheRoutes(routeDTOs);
			log.info("已从数据库更新路由配置到Redis: {} 个路由", routeDTOs.size());
		}
		catch (Exception e) {
			log.error("更新Redis路由缓存失败", e);
		}
	}

	/**
	 * 将实体转换为DTO
	 */
	private GatewayRouteDTO convertToDTO(GatewayRoute route) {
		GatewayRouteDTO dto = new GatewayRouteDTO();
		BeanUtils.copyProperties(route, dto);

		// 解析predicates JSON
		if (route.getPredicates() != null && !route.getPredicates().isEmpty()) {
			try {
				List<GatewayRouteDTO.PredicateConfig> predicates = objectMapper.readValue(route.getPredicates(),
						new TypeReference<List<GatewayRouteDTO.PredicateConfig>>() {
						});
				dto.setPredicates(predicates);
			}
			catch (Exception e) {
				log.error("解析predicates失败", e);
				dto.setPredicates(new ArrayList<>());
			}
		}

		// 解析filters JSON
		if (route.getFilters() != null && !route.getFilters().isEmpty()) {
			try {
				List<GatewayRouteDTO.FilterConfig> filters = objectMapper.readValue(route.getFilters(),
						new TypeReference<List<GatewayRouteDTO.FilterConfig>>() {
						});
				dto.setFilters(filters);
			}
			catch (Exception e) {
				log.error("解析filters失败", e);
				dto.setFilters(new ArrayList<>());
			}
		}

		return dto;
	}

	/**
	 * 将DTO转换为实体
	 */
	private GatewayRoute convertToEntity(GatewayRouteDTO dto) {
		GatewayRoute route = new GatewayRoute();
		BeanUtils.copyProperties(dto, route);

		// 将predicates转换为JSON
		if (dto.getPredicates() != null && !dto.getPredicates().isEmpty()) {
			try {
				String predicatesJson = objectMapper.writeValueAsString(dto.getPredicates());
				route.setPredicates(predicatesJson);
			}
			catch (Exception e) {
				log.error("序列化predicates失败", e);
				throw new BusinessException("路由断言配置格式错误");
			}
		}

		// 将filters转换为JSON
		if (dto.getFilters() != null && !dto.getFilters().isEmpty()) {
			try {
				String filtersJson = objectMapper.writeValueAsString(dto.getFilters());
				route.setFilters(filtersJson);
			}
			catch (Exception e) {
				log.error("序列化filters失败", e);
				throw new BusinessException("路由过滤器配置格式错误");
			}
		}

		return route;
	}

}
