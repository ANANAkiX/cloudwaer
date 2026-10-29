package com.cloudwaer.admin.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.admin.serve.entity.SysSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 全局动态设置 Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface SysSettingMapper extends BaseMapper<SysSetting> {

}
