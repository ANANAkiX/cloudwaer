package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.TopicDTO;
import com.cloudwaer.blog.api.dto.TopicReplyDTO;
import com.cloudwaer.blog.api.dto.TopicReplySaveDTO;
import com.cloudwaer.blog.api.dto.TopicSaveDTO;
import com.cloudwaer.common.core.dto.PageResult;

import java.util.List;

/**
 * 微论坛服务
 *
 * @author cloudwaer
 */
public interface BlogTopicService {

	/**
	 * 公开分页（置顶优先，再按最后回复/创建时间）
	 */
	PageResult<TopicDTO> publicPage(Long current, Long size, String keyword, Boolean essenceOnly);

	/**
	 * 公开详情（浏览+1）
	 */
	TopicDTO publicDetail(Long id);

	/**
	 * 回复树（全部可见回复）
	 */
	List<TopicReplyDTO> listReplies(Long topicId);

	/**
	 * 发帖（登录用户）
	 */
	Long saveTopic(TopicSaveDTO dto);

	/**
	 * 回复（登录用户，两级）
	 */
	Long reply(TopicReplySaveDTO dto);

	/**
	 * 管理端分页
	 */
	PageResult<TopicDTO> adminPage(Long current, Long size, String keyword, Integer isTop, Integer isEssence);

	/**
	 * 管理端更新标记（置顶/加精）
	 */
	Boolean adminUpdateFlags(Long id, Integer isTop, Integer isEssence);

	/**
	 * 管理端删除
	 */
	Boolean adminDelete(Long id);

	/**
	 * 批量刷新文章热度（管理端兜底）
	 * @return 刷新条数
	 */
	int refreshArticleHeat();

}
