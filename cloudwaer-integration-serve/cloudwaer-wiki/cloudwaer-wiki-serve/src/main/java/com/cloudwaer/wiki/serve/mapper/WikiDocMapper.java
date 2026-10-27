package com.cloudwaer.wiki.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.wiki.serve.entity.WikiDoc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WikiDocMapper extends BaseMapper<WikiDoc> {

}
