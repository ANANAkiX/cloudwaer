package com.cloudwaer.blog.serve.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 博客上传目录静态资源映射：/blog/uploads/** → 本地磁盘
 * <p>
 * 网关 /blog/** 可直达；前台经 /api 前缀访问。
 *
 * @author cloudwaer
 */
@Configuration
public class BlogUploadConfig implements WebMvcConfigurer {

	@Value("${blog.upload-dir:./uploads/blog}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
		if (!location.endsWith("/")) {
			location = location + "/";
		}
		registry.addResourceHandler("/blog/uploads/**").addResourceLocations(location);
	}

}
