package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 博客合集 Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogCollectionMapper extends BaseMapper<BlogCollection> {

	/**
	 * 刷新合集已发布文章数
	 * @param collectionId 合集ID
	 */
	@Update("UPDATE blog_collection SET article_count = (SELECT COUNT(*) FROM blog_article WHERE collection_id = #{collectionId} AND publish_status = 1 AND status = 1) WHERE id = #{collectionId}")
	void refreshArticleCount(@Param("collectionId") Long collectionId);

}
