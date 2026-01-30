package com.cloudwaer.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudwaer.captcha")
public class CaptchaProperties {

	/** 是否启用登录验证码 */
	private boolean enabled = false;

	/** 验证码长度 */
	private int length = 4;

	/** 图片宽度 */
	private int width = 160;

	/** 图片高度 */
	private int height = 40;

	/** 过期秒数 */
	private int expireSeconds = 120;

}
