package com.cloudwaer.admin.serve.storage;

import com.cloudwaer.admin.serve.entity.SysOssConfig;
import com.cloudwaer.common.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地磁盘存储
 *
 * @author cloudwaer
 */
@Component
public class LocalFileStorage implements FileStorage {

	@Value("${cloudwaer.file.local-dir:./uploads}")
	private String localDir;

	@Override
	public String upload(SysOssConfig config, String objectKey, InputStream in, long size, String contentType) {
		try {
			Path target = resolve(objectKey);
			Files.createDirectories(target.getParent());
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			return objectKey;
		}
		catch (IOException e) {
			throw new BusinessException("本地文件写入失败：" + e.getMessage());
		}
	}

	@Override
	public void delete(SysOssConfig config, String objectKey) {
		try {
			Files.deleteIfExists(resolve(objectKey));
		}
		catch (IOException e) {
			throw new BusinessException("本地文件删除失败：" + e.getMessage());
		}
	}

	@Override
	public InputStream open(SysOssConfig config, String objectKey) {
		try {
			Path path = resolve(objectKey);
			if (!Files.exists(path)) {
				return null;
			}
			return Files.newInputStream(path);
		}
		catch (IOException e) {
			throw new BusinessException("本地文件读取失败：" + e.getMessage());
		}
	}

	@Override
	public String provider() {
		return "LOCAL";
	}

	private Path resolve(String objectKey) {
		String normalized = objectKey.replace("\\", "/");
		while (normalized.startsWith("/")) {
			normalized = normalized.substring(1);
		}
		if (normalized.contains("..")) {
			throw new BusinessException("非法文件路径");
		}
		return Paths.get(localDir, normalized).normalize();
	}

}
