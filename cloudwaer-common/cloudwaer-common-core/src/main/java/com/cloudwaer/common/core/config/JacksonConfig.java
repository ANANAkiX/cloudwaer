package com.cloudwaer.common.core.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson配置类
 * 统一处理Long类型序列化为字符串，避免前端精度丢失
 *
 * @author cloudwaer
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置Jackson，将Long类型序列化为字符串
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 创建自定义模块
            SimpleModule simpleModule = new SimpleModule();
            // 将Long类型序列化为字符串
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.modules(simpleModule);
        };
    }
}

