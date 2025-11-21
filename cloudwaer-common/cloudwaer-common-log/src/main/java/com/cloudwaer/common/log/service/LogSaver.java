package com.cloudwaer.common.log.service;

import com.cloudwaer.common.log.model.LogRecord;

public interface LogSaver {
    void save(LogRecord record);
}
