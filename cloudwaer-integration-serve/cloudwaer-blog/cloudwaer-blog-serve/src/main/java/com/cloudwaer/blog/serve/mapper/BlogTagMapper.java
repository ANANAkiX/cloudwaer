package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 博客标签Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {

	/**
	 * 标签是否被文章关联引用
	 * @param tagId 标签ID
	 * @return 引用数
	 */
	@Select("SELECT COUNT(*) FROM blog_article_tag WHERE tag_id = #{tagId} AND status = 1")
	int countRelations(@Param("tagId") Long tagId);

}
