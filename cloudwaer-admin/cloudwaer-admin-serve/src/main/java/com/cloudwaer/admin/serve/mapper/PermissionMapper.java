package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
