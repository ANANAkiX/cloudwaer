package com.cloudwaer.flowable.serve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.cloudwaer")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cloudwaer")
public class FlowableServeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowableServeApplication.class, args);
    }
}