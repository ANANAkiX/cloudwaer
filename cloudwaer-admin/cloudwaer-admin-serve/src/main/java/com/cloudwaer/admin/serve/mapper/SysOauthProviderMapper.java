package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.SysOauthProvider;
import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方登录供应商 Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface SysOauthProviderMapper extends BaseMapper<SysOauthProvider> {

}
