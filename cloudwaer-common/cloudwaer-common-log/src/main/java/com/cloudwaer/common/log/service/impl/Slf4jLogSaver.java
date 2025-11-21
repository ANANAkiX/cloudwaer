package com.cloudwaer.common.log.service.impl;

import com.cloudwaer.common.log.model.LogRecord;
import com.cloudwaer.common.log.service.LogSaver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Slf4jLogSaver implements LogSaver {
    @Override
    public void save(LogRecord r) {
        // 简单JSON格式输出，便于集中日志采集
        // 可在业务中自定义替换为DB/ES/MQ等落地方式
        log.info("REQ_LOG | time={} | service={} | ip={} | method={} | uri={} | class={} | func={} | success={} |  | err={}",
                r.getTimestamp(), r.getService(), r.getIp(), r.getHttpMethod(), r.getUri(), r.getClassName(), r.getMethodName(), r.isSuccess(), safe(r.getError()));
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("\r|\n", " ");
    }
}
