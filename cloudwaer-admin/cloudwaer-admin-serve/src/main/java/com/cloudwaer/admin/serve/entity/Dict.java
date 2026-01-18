package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;

/**
 * Common dictionary (header).
 */
@Data
@TableName("sys_dict")
public class Dict extends BaseEntity {

    /** Dictionary type, e.g. status, delete_flag. */
    private String type;

    /** Dictionary name. */
    private String name;

    /** Sort order. */
    private Integer sort;

    /** Description. */
    private String description;
}
