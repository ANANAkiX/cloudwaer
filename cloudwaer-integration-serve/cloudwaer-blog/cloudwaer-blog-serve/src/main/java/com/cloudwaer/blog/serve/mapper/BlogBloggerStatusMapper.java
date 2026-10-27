package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogBloggerStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客主暂停状态 Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogBloggerStatusMapper extends BaseMapper<BlogBloggerStatus> {

}
