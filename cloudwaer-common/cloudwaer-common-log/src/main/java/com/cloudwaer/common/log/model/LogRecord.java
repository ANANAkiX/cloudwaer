package com.cloudwaer.common.log.model;

import lombok.Data;

@Data
public class LogRecord {
    private long timestamp;
    private String service;         // 服务名 spring.application.name
    private String ip;              // 客户端IP
    private String httpMethod;      // GET/POST/...
    private String uri;             // 请求路径
    private String className;       // 控制器类名
    private String methodName;      // 控制器方法名
    private boolean success;        // 是否成功
    private String error;           // 异常摘要（可选）
}
