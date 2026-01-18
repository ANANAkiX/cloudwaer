package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;

/**
 * Dictionary item.
 */
@Data
@TableName("sys_dict_item")
public class DictItem extends BaseEntity {

    /** Dictionary header ID. */
    private Long dictId;

    /** Item code. */
    private String code;

    /** Item value. */
    private String value;

    /** Item label. */
    private String label;

    /** Sort order. */
    private Integer sort;

    /** Description. */
    private String description;
}
