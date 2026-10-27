package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.LikeToggleDTO;

/**
 * 点赞服务
 *
 * @author cloudwaer
 */
public interface BlogLikeService {

	/**
	 * 目标：文章
	 */
	int TARGET_ARTICLE = 1;

	/**
	 * 目标：评论
	 */
	int TARGET_COMMENT = 2;

	/**
	 * 目标：话题
	 */
	int TARGET_TOPIC = 3;

	/**
	 * 目标：话题回复
	 */
	int TARGET_TOPIC_REPLY = 4;

	/**
	 * 切换点赞（再点取消）
	 * @param targetType 目标类型
	 * @param targetId 目标ID
	 * @return 最新点赞状态与计数
	 */
	LikeToggleDTO toggle(Integer targetType, Long targetId);

	/**
	 * 是否已点赞
	 * @param targetType 类型
	 * @param targetId ID
	 * @return 是否
	 */
	Boolean liked(Integer targetType, Long targetId);

}
