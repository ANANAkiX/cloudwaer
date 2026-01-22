package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
