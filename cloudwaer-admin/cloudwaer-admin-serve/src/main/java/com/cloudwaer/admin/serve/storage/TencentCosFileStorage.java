package com.cloudwaer.admin.serve.storage;

import com.cloudwaer.admin.serve.entity.SysOssConfig;
import com.cloudwaer.common.core.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 腾讯云 COS 占位（接入 SDK 后填充，方法签名与阿里云一致）
 *
 * @author cloudwaer
 */
@Component
public class TencentCosFileStorage implements FileStorage {

	@Override
	public String upload(SysOssConfig config, String objectKey, InputStream in, long size, String contentType) {
		throw new BusinessException("腾讯云 COS 尚未接入 SDK，请使用 LOCAL 或 ALIYUN_OSS");
	}

	@Override
	public void delete(SysOssConfig config, String objectKey) {
		throw new BusinessException("腾讯云 COS 尚未接入 SDK");
	}

	@Override
	public InputStream open(SysOssConfig config, String objectKey) {
		throw new BusinessException("腾讯云 COS 尚未接入 SDK");
	}

	@Override
	public String provider() {
		return "TENCENT_COS";
	}

}
