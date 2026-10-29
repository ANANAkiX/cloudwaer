package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.admin.serve.entity.SysOauthProvider;
import com.cloudwaer.admin.serve.mapper.SysOauthProviderMapper;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 第三方登录供应商管理（多组配置；登录流程本体暂未开放）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class SysOauthProviderService {

	/**
	 * 脱敏占位：保存时视为「保持原值」
	 */
	private static final String SECRET_MASK = "******";

	@Autowired
	private SysOauthProviderMapper oauthProviderMapper;

	public List<SysOauthProvider> list() {
		List<SysOauthProvider> list = oauthProviderMapper
			.selectList(new LambdaQueryWrapper<SysOauthProvider>().orderByAsc(SysOauthProvider::getSort));
		for (SysOauthProvider provider : list) {
			if (StringUtils.hasText(provider.getClientSecret())) {
				provider.setClientSecret(SECRET_MASK);
			}
		}
		return list;
	}

	public Boolean save(SysOauthProvider provider) {
		if (provider == null || !StringUtils.hasText(provider.getProviderType())
				|| !StringUtils.hasText(provider.getProviderName()) || !StringUtils.hasText(provider.getClientId())) {
			throw new BusinessException("供应商类型、名称、Client ID 不能为空");
		}
		if (provider.getEnabled() == null) {
			provider.setEnabled(1);
		}
		if (provider.getSort() == null) {
			provider.setSort(0);
		}
		if (SECRET_MASK.equals(provider.getClientSecret())) {
			provider.setClientSecret(null);
		}
		if (provider.getId() == null) {
			oauthProviderMapper.insert(provider);
		}
		else {
			SysOauthProvider existing = oauthProviderMapper.selectById(provider.getId());
			if (existing == null) {
				throw new BusinessException("供应商不存在");
			}
			// 留空或脱敏占位表示保持原 secret 不变
			if (!StringUtils.hasText(provider.getClientSecret())) {
				provider.setClientSecret(existing.getClientSecret());
			}
			oauthProviderMapper.updateById(provider);
		}
		log.info("第三方登录供应商已保存: id={}, type={}", provider.getId(), provider.getProviderType());
		return Boolean.TRUE;
	}

	public Boolean delete(Long id) {
		if (id == null) {
			throw new BusinessException("id 不能为空");
		}
		oauthProviderMapper.deleteById(id);
		log.info("第三方登录供应商已删除: id={}", id);
		return Boolean.TRUE;
	}

}
