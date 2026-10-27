package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 微论坛话题Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogTopicMapper extends BaseMapper<BlogTopic> {

	/**
	 * 浏览量+1
	 */
	@Update("UPDATE blog_topic SET view_count = view_count + 1 WHERE id = #{id}")
	int incrementView(@Param("id") Long id);

	/**
	 * 点赞数+1
	 */
	@Update("UPDATE blog_topic SET like_count = like_count + 1 WHERE id = #{id}")
	int incrementLike(@Param("id") Long id);

	/**
	 * 点赞数-1
	 */
	@Update("UPDATE blog_topic SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
	int decrementLike(@Param("id") Long id);

	/**
	 * 回复数+1
	 */
	@Update("UPDATE blog_topic SET reply_count = reply_count + 1, last_reply_time = NOW() WHERE id = #{id}")
	int incrementReply(@Param("id") Long id);

}
