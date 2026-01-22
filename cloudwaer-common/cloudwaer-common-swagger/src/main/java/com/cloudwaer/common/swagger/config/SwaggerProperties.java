package com.cloudwaer.common.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger配置属性
 *
 * @author cloudwaer
 */
@Data
@ConfigurationProperties(prefix = "cloudwaer.swagger")
public class SwaggerProperties {

	/**
	 * API标题
	 */
	private String title = "Cloudwaer API";

	/**
	 * API版本
	 */
	private String version = "1.0.0";

	/**
	 * API描述
	 */
	private String description = "Cloudwaer系统API文档";

	/**
	 * 联系人姓名
	 */
	private String contactName = "Cloudwaer";

	/**
	 * 联系人邮箱
	 */
	private String contactEmail = "support@cloudwaer.com";

	/**
	 * 许可证名称
	 */
	private String licenseName = "Apache 2.0";

	/**
	 * 许可证URL
	 */
	private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.html";

}
