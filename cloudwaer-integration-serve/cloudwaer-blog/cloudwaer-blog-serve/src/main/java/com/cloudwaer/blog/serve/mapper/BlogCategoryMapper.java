package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 博客分类Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {

	/**
	 * 按分类统计已发布文章数
	 * @return 每行含 category_id、article_count
	 */
	@Select("SELECT category_id AS categoryId, COUNT(*) AS articleCount " + "FROM blog_article "
			+ "WHERE status = 1 AND publish_status = 1 AND category_id IS NOT NULL " + "GROUP BY category_id")
	List<Map<String, Object>> countPublishedByCategory();

	/**
	 * 分类是否被有效文章引用
	 * @param categoryId 分类ID
	 * @return 引用数
	 */
	@Select("SELECT COUNT(*) FROM blog_article WHERE category_id = #{categoryId} AND status = 1")
	int countArticles(@Param("categoryId") Long categoryId);

}
