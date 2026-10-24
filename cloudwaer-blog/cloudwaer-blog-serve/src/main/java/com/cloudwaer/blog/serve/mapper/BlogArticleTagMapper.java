package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogArticleTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 文章标签关联Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogArticleTagMapper extends BaseMapper<BlogArticleTag> {

	/**
	 * 物理删除文章的标签关联（中间表不用逻辑删除，否则唯一索引 uk_article_tag 冲突）
	 * @param articleId 文章ID
	 * @return 删除行数
	 */
	@Delete("DELETE FROM blog_article_tag WHERE article_id = #{articleId}")
	int physicalDeleteByArticleId(@Param("articleId") Long articleId);

	/**
	 * 物理删除标签的全部关联
	 * @param tagId 标签ID
	 * @return 删除行数
	 */
	@Delete("DELETE FROM blog_article_tag WHERE tag_id = #{tagId}")
	int physicalDeleteByTagId(@Param("tagId") Long tagId);

	/**
	 * 按标签统计已发布文章引用数（标签云）
	 * @return 每行含 tag_id、use_count
	 */
	@Select("SELECT at.tag_id AS tagId, COUNT(*) AS useCount " + "FROM blog_article_tag at "
			+ "INNER JOIN blog_article a ON a.id = at.article_id AND a.status = 1 AND a.publish_status = 1 "
			+ "WHERE at.status = 1 " + "GROUP BY at.tag_id")
	List<Map<String, Object>> countPublishedUseByTag();

}
