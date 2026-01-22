package com.cloudwaer.gateway.controller;

import com.cloudwaer.gateway.config.DynamicRouteDefinitionLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 网关路由刷新控制器
 *
 * @author cloudwaer
 */
@Slf4j
@RestController
@RequestMapping("/actuator/gateway")
public class GatewayRouteRefreshController implements ApplicationEventPublisherAware {

	@Autowired
	private RouteDefinitionWriter routeDefinitionWriter;

	@Autowired
	private DynamicRouteDefinitionLocator routeDefinitionLocator;

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	/**
	 * 刷新路由
	 */
	@PostMapping("/refresh")
	public Mono<ResponseEntity<String>> refresh() {
		// 直接发布刷新事件，让Spring Cloud Gateway重新加载所有RouteDefinitionLocator的路由
		this.publisher.publishEvent(new RefreshRoutesEvent(this));
		log.info("路由刷新事件已发布");
		return Mono.just(ResponseEntity.ok("路由刷新成功"));
	}

}
