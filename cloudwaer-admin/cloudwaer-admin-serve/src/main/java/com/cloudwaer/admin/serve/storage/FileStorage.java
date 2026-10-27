package com.cloudwaer.admin.serve.storage;

import com.cloudwaer.admin.serve.entity.SysOssConfig;

import java.io.InputStream;

/**
 * 对象存储抽象；配置来自 sys_oss_config 或 yml 兜底
 *
 * @author cloudwaer
 */
public interface FileStorage {

	/**
	 * 上传；config 可为 null（LOCAL 不依赖）
	 */
	String upload(SysOssConfig config, String objectKey, InputStream in, long size, String contentType);

	void delete(SysOssConfig config, String objectKey);

	InputStream open(SysOssConfig config, String objectKey);

	String provider();

	/**
	 * 生成可公网访问的 URL（阿里云=签名 URL；LOCAL=相对路径代理）；不支持则返回 null
	 */
	default String accessUrl(SysOssConfig config, String objectKey) {
		return null;
	}

}
