package com.cloudwaer.gateway.controller;

import com.cloudwaer.common.core.result.Result;
import com.cloudwaer.gateway.config.CaptchaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final StringRedisTemplate stringRedisTemplate;
    private final CaptchaProperties props;

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    @GetMapping("/captcha")
    public Result<Map<String, Object>> createCaptcha() {
        try {
            if (!props.isEnabled()) {
                Map<String, Object> data = new HashMap<>();
                data.put("enabled", false);
                return Result.success(data);
            }

            String id = UUID.randomUUID().toString().replace("-", "");
            String code = randomCode(props.getLength());

            // store to redis (lower case for case-insensitive compare)
            stringRedisTemplate.opsForValue().set(
                    CAPTCHA_KEY_PREFIX + id,
                    code.toLowerCase(),
                    Duration.ofSeconds(props.getExpireSeconds())
            );

            String imageBase64 = buildImageBase64(code, props.getWidth(), props.getHeight());

            Map<String, Object> data = new HashMap<>();
            data.put("enabled", true);
            data.put("captchaId", id);
            data.put("imageBase64", imageBase64);
            data.put("expireSeconds", props.getExpireSeconds());
            return Result.success(data);
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            return Result.fail(500, "生成验证码失败");
        }
    }

    private String randomCode(int len) {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZ23456789"; // 排除易混淆字符
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String buildImageBase64(String code, int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 干扰线
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(r.nextInt(150), r.nextInt(150), r.nextInt(150)));
            int x1 = r.nextInt(width), y1 = r.nextInt(height);
            int x2 = r.nextInt(width), y2 = r.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        // 文本
        g.setFont(new Font("Arial", Font.BOLD, height - 10));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(code);
        int x = (width - textWidth) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.BLACK);
        g.drawString(code, x, y);
        g.dispose();

        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
        return "data:image/png;base64," + base64;
    }
}
