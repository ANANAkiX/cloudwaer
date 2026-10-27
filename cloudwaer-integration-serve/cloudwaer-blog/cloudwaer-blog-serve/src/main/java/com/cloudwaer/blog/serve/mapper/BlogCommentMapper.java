package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 博客评论Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogCommentMapper extends BaseMapper<BlogComment> {

	/**
	 * 点赞数+1
	 */
	@Update("UPDATE blog_comment SET like_count = like_count + 1 WHERE id = #{id}")
	int incrementLike(@Param("id") Long id);

	/**
	 * 点赞数-1
	 */
	@Update("UPDATE blog_comment SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
	int decrementLike(@Param("id") Long id);

}
