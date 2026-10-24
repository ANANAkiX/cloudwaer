package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {

	/**
	 * 物理删除点赞（取消点赞时，避免唯一索引与逻辑删除冲突）
	 */
	@Delete("DELETE FROM blog_like WHERE target_type = #{targetType} AND target_id = #{targetId} AND user_id = #{userId}")
	int physicalDelete(@Param("targetType") Integer targetType, @Param("targetId") Long targetId,
			@Param("userId") Long userId);

}
