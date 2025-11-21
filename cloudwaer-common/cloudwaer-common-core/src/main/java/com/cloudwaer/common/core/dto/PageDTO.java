package com.cloudwaer.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询DTO
 *
 * @author cloudwaer
 */
@Data
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private Long current = 1L;

    /**
     * 每页数量
     */
    private Long size = 10L;

    /**
     * 搜索关键词（用于复合搜索）
     */
    private String keyword;

    /**
     * 获取偏移量（用于数据库查询）
     */
    public Long getOffset() {
        return (current - 1) * size;
    }
}
