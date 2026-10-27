package com.cloudwaer.wiki.serve.service;

import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.wiki.api.dto.WikiShareAccessDTO;
import com.cloudwaer.wiki.api.dto.WikiShareDTO;
import com.cloudwaer.wiki.api.dto.WikiShareSaveDTO;

public interface WikiShareService {

	PageResult<WikiShareDTO> page(Long current, Long size, String targetType, Long targetId);

	WikiShareDTO create(WikiShareSaveDTO dto);

	Boolean revoke(Long id);

	/**
	 * 分享链接写入（仅 accessLevel=2）
	 */
	WikiShareAccessDTO updateViaShare(String token, String password, String title, String content);

	/** 匿名分享访问（校验过期/密码） */
	WikiShareAccessDTO access(String token, String password);

}
