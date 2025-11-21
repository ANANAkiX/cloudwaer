package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;

/**
 * 通用字典表
 */
@Data
@TableName("sys_dict")
public class Dict extends BaseEntity {

    /** 字典类型，如: status, delete_flag */
    private String type;

    /** 键/编码 */
    @TableField("code")
    private String code;

    /** 值 */
    private String value;

    /** 展示标签 */
    private String label;

    /** 排序 */
    private Integer sort;

    /** 备注 */
    private String description;
}
