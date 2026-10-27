package com.cloudwaer.blog.serve.service;

import com.cloudwaer.blog.api.dto.SiteSettingDTO;

/**
 * 博客站点设置服务
 *
 * @author cloudwaer
 */
public interface BlogSettingService {

	/**
	 * 读取站点设置（缺项回落默认值）
	 * @return 扁平化设置
	 */
	SiteSettingDTO getSettings();

	/**
	 * 保存站点设置（按 key upsert，空值清空对应项）
	 * @param dto 设置
	 * @return 是否成功
	 */
	Boolean saveSettings(SiteSettingDTO dto);

}
