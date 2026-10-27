package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogSiteSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客站点设置Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogSiteSettingMapper extends BaseMapper<BlogSiteSetting> {

}
