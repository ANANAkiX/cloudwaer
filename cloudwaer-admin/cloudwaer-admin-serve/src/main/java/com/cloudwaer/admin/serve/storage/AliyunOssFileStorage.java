package com.cloudwaer.admin.serve.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.cloudwaer.admin.serve.entity.SysOssConfig;
import com.cloudwaer.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 阿里云 OSS 实现。
 * <p>
 * 连接参数优先取 sys_oss_config（后台可配）； 若 config 为空则回落 yml： cloudwaer.oss.aliyun.endpoint /
 * access-key-id / access-key-secret / bucket
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class AliyunOssFileStorage implements FileStorage {

	@Value("${cloudwaer.oss.aliyun.endpoint:}")
	private String ymlEndpoint;

	@Value("${cloudwaer.oss.aliyun.access-key-id:}")
	private String ymlAccessKeyId;

	@Value("${cloudwaer.oss.aliyun.access-key-secret:}")
	private String ymlAccessKeySecret;

	@Value("${cloudwaer.oss.aliyun.bucket:}")
	private String ymlBucket;

	@Override
	public String upload(SysOssConfig config, String objectKey, InputStream in, long size, String contentType) {
		String endpoint = pick(config, config == null ? null : config.getEndpoint(), ymlEndpoint);
		String ak = pick(config, config == null ? null : config.getAccessKey(), ymlAccessKeyId);
		String sk = pick(config, config == null ? null : config.getSecretKey(), ymlAccessKeySecret);
		String bucket = pick(config, config == null ? null : config.getBucket(), ymlBucket);
		require(endpoint, ak, sk, bucket);

		OSS client = new OSSClientBuilder().build(endpoint, ak, sk);
		try {
			client.putObject(bucket, objectKey, in);
			log.info("OSS upload ok bucket={}, key={}", bucket, objectKey);
			return objectKey;
		}
		catch (Exception e) {
			log.error("OSS upload fail key={}", objectKey, e);
			throw new BusinessException("阿里云 OSS 上传失败：" + e.getMessage());
		}
		finally {
			client.shutdown();
		}
	}

	@Override
	public void delete(SysOssConfig config, String objectKey) {
		String endpoint = pick(config, config == null ? null : config.getEndpoint(), ymlEndpoint);
		String ak = pick(config, config == null ? null : config.getAccessKey(), ymlAccessKeyId);
		String sk = pick(config, config == null ? null : config.getSecretKey(), ymlAccessKeySecret);
		String bucket = pick(config, config == null ? null : config.getBucket(), ymlBucket);
		require(endpoint, ak, sk, bucket);

		OSS client = new OSSClientBuilder().build(endpoint, ak, sk);
		try {
			client.deleteObject(bucket, objectKey);
			log.info("OSS delete ok bucket={}, key={}", bucket, objectKey);
		}
		catch (Exception e) {
			log.error("OSS delete fail key={}", objectKey, e);
			throw new BusinessException("阿里云 OSS 删除失败：" + e.getMessage());
		}
		finally {
			client.shutdown();
		}
	}

	@Override
	public InputStream open(SysOssConfig config, String objectKey) {
		String endpoint = pick(config, config == null ? null : config.getEndpoint(), ymlEndpoint);
		String ak = pick(config, config == null ? null : config.getAccessKey(), ymlAccessKeyId);
		String sk = pick(config, config == null ? null : config.getSecretKey(), ymlAccessKeySecret);
		String bucket = pick(config, config == null ? null : config.getBucket(), ymlBucket);
		require(endpoint, ak, sk, bucket);

		OSS client = new OSSClientBuilder().build(endpoint, ak, sk);
		try {
			OSSObject object = client.getObject(bucket, objectKey);
			try (InputStream raw = object.getObjectContent()) {
				byte[] bytes = raw.readAllBytes();
				return new java.io.ByteArrayInputStream(bytes);
			}
		}
		catch (Exception e) {
			log.error("OSS open fail key={}", objectKey, e);
			throw new BusinessException("阿里云 OSS 读取失败：" + e.getMessage());
		}
		finally {
			client.shutdown();
		}
	}

	@Override
	public String provider() {
		return "ALIYUN_OSS";
	}

	/**
	 * 私有桶：生成签名 URL（默认 7 天）。公有桶也可用 CDN 域名拼接。
	 */
	@Override
	public String accessUrl(SysOssConfig config, String objectKey) {
		// CDN/自定义域名 + 公有读：直接拼 URL（控制台需对 uploads/ 设公共读）
		if (config != null && StringUtils.hasText(config.getCdnDomain())) {
			return config.getCdnDomain().replaceAll("/+$", "") + "/" + objectKey;
		}
		String endpoint = pick(config, config == null ? null : config.getEndpoint(), ymlEndpoint);
		String ak = pick(config, config == null ? null : config.getAccessKey(), ymlAccessKeyId);
		String sk = pick(config, config == null ? null : config.getSecretKey(), ymlAccessKeySecret);
		String bucket = pick(config, config == null ? null : config.getBucket(), ymlBucket);
		if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(ak) || !StringUtils.hasText(sk)
				|| !StringUtils.hasText(bucket)) {
			return null;
		}
		OSS client = new OSSClientBuilder().build(endpoint, ak, sk);
		try {
			java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
			return client.generatePresignedUrl(bucket, objectKey, expiration).toString();
		}
		catch (Exception e) {
			log.warn("生成 OSS 签名 URL 失败 key={}", objectKey, e);
			return null;
		}
		finally {
			client.shutdown();
		}
	}

	private String pick(SysOssConfig config, String fromDb, String fromYml) {
		if (StringUtils.hasText(fromDb)) {
			return fromDb.trim();
		}
		return fromYml == null ? "" : fromYml.trim();
	}

	private void require(String... values) {
		for (String v : values) {
			if (!StringUtils.hasText(v)) {
				throw new BusinessException("阿里云 OSS 配置不完整（endpoint/ak/sk/bucket），请在文件管理·OSS 配置或 yml 中填写");
			}
		}
	}

}
