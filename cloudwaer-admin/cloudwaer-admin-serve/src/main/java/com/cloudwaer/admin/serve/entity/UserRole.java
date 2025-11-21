package com.cloudwaer.admin.serve.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloudwaer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关系实体
 * 注意：中间表使用物理删除，不使用逻辑删除，避免唯一索引冲突
 *
 * @author cloudwaer
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRole extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}

