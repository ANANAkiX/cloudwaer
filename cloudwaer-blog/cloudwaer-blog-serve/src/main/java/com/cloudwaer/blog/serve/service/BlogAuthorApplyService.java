package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.AuthorApplyDTO;
import com.cloudwaer.common.core.dto.PageResult;

/**
 * 博客主申请服务（MVP）
 *
 * @author cloudwaer
 */
public interface BlogAuthorApplyService {

	/**
	 * 自助申请成为博客主
	 * @param reason 申请理由
	 * @return 申请ID
	 */
	Long apply(String reason);

	/**
	 * 当前用户申请状态（无申请返回 null）
	 * @return 申请DTO或null
	 */
	AuthorApplyDTO myApply();

	/**
	 * 管理端申请分页
	 * @param current 页码
	 * @param size 页大小
	 * @param applyStatus 状态筛选
	 * @param keyword 关键字
	 * @return 分页结果
	 */
	PageResult<AuthorApplyDTO> page(Long current, Long size, Integer applyStatus, String keyword);

	/**
	 * 处理申请（仅改申请状态；角色由管理端用户页分配）
	 * @param id 申请ID
	 * @param applyStatus 1-通过，2-拒绝
	 * @param remark 备注
	 * @return 是否成功
	 */
	Boolean handle(Long id, Integer applyStatus, String remark);

}
