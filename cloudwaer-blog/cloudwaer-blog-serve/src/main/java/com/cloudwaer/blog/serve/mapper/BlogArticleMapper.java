package com.cloudwaer.blog.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudwaer.blog.serve.entity.BlogArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 博客文章Mapper
 *
 * @author cloudwaer
 */
@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {

	/**
	 * 浏览量 +1（原子自增）
	 * @param id 文章ID
	 * @return 影响行数
	 */
	@Update("UPDATE blog_article SET view_count = view_count + 1 WHERE id = #{id}")
	int incrementView(@Param("id") Long id);

	/**
	 * 评论数 +1（原子自增，审核通过时调用）
	 * @param id 文章ID
	 * @return 影响行数
	 */
	@Update("UPDATE blog_article SET comment_count = comment_count + 1 WHERE id = #{id}")
	int incrementComment(@Param("id") Long id);

	/**
	 * 评论数 -1（原子自减，删除已通过评论时调用）
	 * @param id 文章ID
	 * @return 影响行数
	 */
	@Update("UPDATE blog_article SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{id}")
	int decrementComment(@Param("id") Long id);

	/**
	 * 点赞数 +1
	 */
	@Update("UPDATE blog_article SET like_count = like_count + 1 WHERE id = #{id}")
	int incrementLike(@Param("id") Long id);

	/**
	 * 点赞数 -1
	 */
	@Update("UPDATE blog_article SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
	int decrementLike(@Param("id") Long id);

	/**
	 * 刷新热度分（对数加权 + 时间衰减） score = (ln(1+view)*1 + ln(1+like)*2 + ln(1+comment)*3) *
	 * exp(-age_days/30)
	 */
	@Update("UPDATE blog_article SET heat_score = (LN(1 + view_count) * 1.0 "
			+ "+ LN(1 + like_count) * 2.0 + LN(1 + comment_count) * 3.0) "
			+ "* EXP(-DATEDIFF(NOW(), COALESCE(publish_time, create_time)) / 30.0) WHERE id = #{id}")
	int refreshHeat(@Param("id") Long id);

}
