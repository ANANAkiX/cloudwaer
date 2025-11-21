package com.cloudwaer.gateway.config;

import com.cloudwaer.admin.api.dto.GatewayRouteDTO;
import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.common.core.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 动态路由定义定位器
 * 从admin服务获取路由配置并转换为RouteDefinition
 *
 * @author cloudwaer
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Lazy
public class DynamicRouteDefinitionLocator implements RouteDefinitionLocator, ApplicationListener<ApplicationReadyEvent> {

    @Autowired(required = false)
    private WebClient.Builder webClientBuilder;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    private static final String ADMIN_SERVICE_NAME = "cloudwaer-admin-serve";
    private static final String GATEWAY_ROUTE_LIST_URL = "/admin/gateway-route/list";
    private static final String REDIS_ROUTES_KEY = "gateway:routes:v1";
    private static final long REDIS_CACHE_SECONDS = 300L;
    
    // 缓存路由定义，避免重复调用
    private final AtomicReference<List<RouteDefinition>> cachedRoutes = new AtomicReference<>(Collections.emptyList());
    private volatile long lastLoadTime = 0;
    private static final long CACHE_DURATION_MS = 30000; // 缓存30秒
    
    // 应用是否已启动
    private final AtomicBoolean applicationReady = new AtomicBoolean(false);

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // 如果应用未启动或WebClient未初始化，直接返回缓存的路由（避免启动时错误）
        if (!applicationReady.get() || webClientBuilder == null) {
            return Flux.fromIterable(cachedRoutes.get());
        }

        // 检查缓存是否有效
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLoadTime < CACHE_DURATION_MS && !cachedRoutes.get().isEmpty()) {
            return Flux.fromIterable(cachedRoutes.get());
        }

        // 刷新时优先从 admin 数据库获取，失败时才回退 Redis
        return loadRoutesAsync()
                .switchIfEmpty(Flux.defer(() -> {
                    // admin 成功返回空列表时，表示无路由，直接返回空或使用缓存
                    List<RouteDefinition> cache = cachedRoutes.get();
                    return cache.isEmpty() ? Flux.empty() : Flux.fromIterable(cache);
                }))
                .onErrorResume(error -> {
                    log.debug("从 admin 加载路由失败，回退 Redis 或缓存: {}", error.getMessage());
                    List<RouteDefinition> redisRoutes = loadRoutesFromRedisAsDefinitions();
                    if (!redisRoutes.isEmpty()) {
                        cachedRoutes.set(redisRoutes);
                        lastLoadTime = System.currentTimeMillis();
                        return Flux.fromIterable(redisRoutes);
                    }
                    return Flux.fromIterable(cachedRoutes.get());
                });
    }

    /**
     * 异步加载路由
     */
    private Flux<RouteDefinition> loadRoutesAsync() {
        if (webClientBuilder == null) {
            return Flux.fromIterable(cachedRoutes.get());
        }

        try {
            WebClient webClient = webClientBuilder.build();
            
            // 异步调用，使用subscribeOn避免阻塞
            return webClient.get()
                    .uri("http://" + ADMIN_SERVICE_NAME + GATEWAY_ROUTE_LIST_URL)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Result<List<GatewayRouteDTO>>>() {})
                    .subscribeOn(Schedulers.boundedElastic())  // 使用有界弹性调度器
                    .flatMapMany(result -> {
                        if (result == null || result.getCode() == null || !result.getCode().equals(ResultCode.SUCCESS.getCode()) || result.getData() == null) {
                            return Flux.error(new IllegalStateException("admin路由接口返回无效结果"));
                        }

                        List<GatewayRouteDTO> routeDTOs = result.getData();
                        if (routeDTOs.isEmpty()) {
                            log.debug("admin 返回空路由列表");
                            cachedRoutes.set(Collections.emptyList());
                            lastLoadTime = System.currentTimeMillis();
                            // 空列表也视为成功，不触发回退
                            saveRoutesToRedis(routeDTOs);
                            return Flux.empty();
                        }

                        // 转换为RouteDefinition
                        List<RouteDefinition> routeDefinitions = convertFromDTOList(routeDTOs);
                        log.info("成功加载 {} 个动态路由配置", routeDefinitions.size());
                        // 更新缓存
                        cachedRoutes.set(routeDefinitions);
                        lastLoadTime = System.currentTimeMillis();
                        // 落地到Redis缓存
                        saveRoutesToRedis(routeDTOs);
                        return Flux.fromIterable(routeDefinitions);
                    })
                    .doOnError(error -> log.debug("加载动态路由配置失败: {}", error.getMessage()));
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    /**
     * 应用启动完成后，异步加载路由
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        applicationReady.set(true);
        log.info("应用启动完成，开始加载动态路由配置");
        // 优先尝试从Redis加载
        List<RouteDefinition> redisRoutes = loadRoutesFromRedisAsDefinitions();
        if (!redisRoutes.isEmpty()) {
            cachedRoutes.set(redisRoutes);
            lastLoadTime = System.currentTimeMillis();
            log.info("从Redis加载到 {} 条网关路由", redisRoutes.size());
            // 后台刷新一次
            loadRoutesAsync().subscribe();
        } else {
            // 异步加载路由，不阻塞
            loadRoutesAsync().subscribe(
                    rd -> {},
                    error -> log.warn("启动时加载动态路由配置失败，将在后续请求中重试: {}", error.getMessage())
            );
        }
    }

    /**
     * 将GatewayRouteDTO转换为RouteDefinition
     */
    private RouteDefinition convertToRouteDefinition(GatewayRouteDTO routeDTO) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(routeDTO.getRouteId());
        
        // 安全地创建URI
        try {
            URI uri = URI.create(routeDTO.getUri());
            definition.setUri(uri);
        } catch (Exception e) {
            log.error("创建URI失败: uri={}, routeId={}", routeDTO.getUri(), routeDTO.getRouteId(), e);
            throw new IllegalArgumentException("无效的URI: " + routeDTO.getUri(), e);
        }
        
        definition.setOrder(routeDTO.getOrder() != null ? routeDTO.getOrder() : 0);

        // 转换断言
        if (routeDTO.getPredicates() != null && !routeDTO.getPredicates().isEmpty()) {
            List<PredicateDefinition> predicates = new ArrayList<>();
            for (GatewayRouteDTO.PredicateConfig predicateConfig : routeDTO.getPredicates()) {
                PredicateDefinition predicate = new PredicateDefinition();
                predicate.setName(predicateConfig.getName());
                
                // 设置参数（Spring Cloud Gateway的参数格式：key=value，对于Path断言，key通常是_genkey_0）
                if (predicateConfig.getArgs() != null && !predicateConfig.getArgs().isEmpty()) {
                    Map<String, String> args = new HashMap<>();
                    String predicateName = predicateConfig.getName();
                    for (Map.Entry<String, String> entry : predicateConfig.getArgs().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        
                        // 对于Path断言，如果参数名是pattern，转换为_genkey_0
                        if ("Path".equals(predicateName) && "pattern".equals(key)) {
                            args.put("_genkey_0", value);
                        } else {
                            // 其他断言和参数直接使用
                            args.put(key, value);
                        }
                    }
                    predicate.setArgs(args);
                }
                
                predicates.add(predicate);
            }
            definition.setPredicates(predicates);
        }

        // 转换过滤器
        if (routeDTO.getFilters() != null && !routeDTO.getFilters().isEmpty()) {
            List<FilterDefinition> filters = new ArrayList<>();
            for (GatewayRouteDTO.FilterConfig filterConfig : routeDTO.getFilters()) {
                FilterDefinition filter = new FilterDefinition();
                filter.setName(filterConfig.getName());
                
                // 设置参数
                if (filterConfig.getArgs() != null && !filterConfig.getArgs().isEmpty()) {
                    Map<String, String> args = new HashMap<>(filterConfig.getArgs());
                    filter.setArgs(args);
                }
                
                filters.add(filter);
            }
            definition.setFilters(filters);
        }

        return definition;
    }

    /**
     * 批量将 DTO 列表转换为 RouteDefinition 列表
     */
    private List<RouteDefinition> convertFromDTOList(List<GatewayRouteDTO> routeDTOs) {
        if (routeDTOs == null || routeDTOs.isEmpty()) {
            return Collections.emptyList();
        }
        List<RouteDefinition> list = new ArrayList<>();
        for (GatewayRouteDTO dto : routeDTOs) {
            try {
                list.add(convertToRouteDefinition(dto));
            } catch (Exception e) {
                log.error("转换路由定义失败: routeId={}", dto.getRouteId(), e);
            }
        }
        return list;
    }

    /**
     * 从 Redis 读取并转换为 RouteDefinition 列表
     */
    private List<RouteDefinition> loadRoutesFromRedisAsDefinitions() {
        List<GatewayRouteDTO> dtos = loadRoutesFromRedisDTOs();
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return convertFromDTOList(dtos);
    }

    /**
     * 从 Redis 读取 DTO 列表
     */
    private List<GatewayRouteDTO> loadRoutesFromRedisDTOs() {
        if (stringRedisTemplate == null || objectMapper == null) return Collections.emptyList();
        try {
            String json = stringRedisTemplate.opsForValue().get(REDIS_ROUTES_KEY);
            if (json == null || json.isEmpty()) return Collections.emptyList();
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, GatewayRouteDTO.class)
            );
        } catch (Exception e) {
            log.debug("读取Redis路由缓存失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 将最新路由写入 Redis 做缓存
     */
    private void saveRoutesToRedis(List<GatewayRouteDTO> routeDTOs) {
        if (stringRedisTemplate == null || objectMapper == null || routeDTOs == null) return;
        try {
            String json = objectMapper.writeValueAsString(routeDTOs);
            stringRedisTemplate.opsForValue().set(REDIS_ROUTES_KEY, json, REDIS_CACHE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.debug("写入Redis路由缓存失败: {}", e.getMessage());
        }
    }
}

