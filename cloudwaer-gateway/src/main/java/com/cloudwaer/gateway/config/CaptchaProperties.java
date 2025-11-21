package com.cloudwaer.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public int getExpireSeconds() { return expireSeconds; }
    public void setExpireSeconds(int expireSeconds) { this.expireSeconds = expireSeconds; }
}
