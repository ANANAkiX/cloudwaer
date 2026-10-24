package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogTopicReply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 微论坛回复Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogTopicReplyMapper extends BaseMapper<BlogTopicReply> {

	/**
	 * 物理删除话题下全部回复
	 */
	@Delete("DELETE FROM blog_topic_reply WHERE topic_id = #{topicId}")
	int physicalDeleteByTopicId(@Param("topicId") Long topicId);

}
