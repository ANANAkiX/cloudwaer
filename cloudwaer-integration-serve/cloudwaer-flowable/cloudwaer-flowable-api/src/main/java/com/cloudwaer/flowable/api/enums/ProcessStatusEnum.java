package com.cloudwaer.flowable.api.enums;

import lombok.Getter;

/**
 * 流程状态枚举
 */
@Getter
public enum ProcessStatusEnum {

    RUNNING(0, "running", "运行中"),
    COMPLETED(1, "completed", "已完成"),
    SUSPENDED(2, "suspended", "已挂起"),
    REJECTED(3, "rejected", "被拒绝"),
    TERMINATED(4, "terminated", "已终止"),
    CANCELED(5, "canceled", "已取消");


    private final Integer code;
    private final String value;
    private final String description;

    ProcessStatusEnum(Integer code, String value, String description) {
        this.code = code;
        this.value = value;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ProcessStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProcessStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据value获取枚举
     */
    public static ProcessStatusEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (ProcessStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
