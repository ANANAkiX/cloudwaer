package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.BloggerSuspendDTO;
import com.cloudwaer.blog.api.dto.BloggerStatusDTO;

import java.util.List;

/**
 * 博客主管理服务（暂停/解封/状态查询）
 *
 * @author cloudwaer
 */
public interface BloggerManageService {

	/**
	 * 暂停博客主发布权限至指定时间（upsert；可续期）
	 * @param dto 暂停请求（suspendUntil 为空表示永久）
	 * @return 是否成功
	 */
	Boolean suspend(BloggerSuspendDTO dto);

	/**
	 * 解除暂停（幂等：未在暂停中也返回成功）
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	Boolean release(Long userId);

	/**
	 * 批量查询暂停状态（供管理端回填展示）
	 * @param userIds 用户ID列表
	 * @return 状态列表（无记录的用户不返回）
	 */
	List<BloggerStatusDTO> listByUserIds(List<Long> userIds);

	/**
	 * 当前登录用户的暂停状态（前台自助查询，无记录返回 null）
	 * @return 状态或 null
	 */
	BloggerStatusDTO myStatus();

	/**
	 * 发布链路拦截判定：未暂停返回 null；暂停中返回给作者看的拦截文案
	 * @param userId 作者ID
	 * @return 拦截文案或 null
	 */
	String blockedReason(Long userId);

}
