package com.cloudwaer.admin.serve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.admin.serve.dto.SettingItemDTO;
import com.cloudwaer.admin.serve.dto.SettingSaveItem;
import com.cloudwaer.admin.serve.entity.SysOauthProvider;
import com.cloudwaer.admin.serve.entity.SysSetting;
import com.cloudwaer.admin.serve.mapper.SysOauthProviderMapper;
import com.cloudwaer.admin.serve.mapper.SysSettingMapper;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.service.GlobalSettingService;
import com.cloudwaer.common.core.service.PasswordTransportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局动态设置管理（写入侧）。
 *
 * <p>
 * 保存只写库（草稿），「发布」才打包写入 Redis 快照并广播各实例实时生效——未发布不影响运行中的服务。 生效读取链见
 * {@link GlobalSettingService}：快照 → yml/代码兜底。
 * </p>
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class SysSettingService {

	private static final int MAX_KEY_LENGTH = 128;

	private static final int MAX_VALUE_LENGTH = 2048;

	/**
	 * 已知配置键元数据（分组/类型/备注/yml 映射/默认值展示/是否已实现可生效）
	 */
	private static final List<SettingKeyMeta> KEY_REGISTRY = List.of(
			// ---- 验证码 ----
			new SettingKeyMeta("captcha.enabled", "captcha", "BOOL", "登录验证码开关", "cloudwaer.captcha.enabled", "false",
					true),
			new SettingKeyMeta("captcha.expire-seconds", "captcha", "INT", "验证码有效期（秒）",
					"cloudwaer.captcha.expire-seconds", "120", true),
			new SettingKeyMeta("captcha.length", "captcha", "INT", "验证码字符数", "cloudwaer.captcha.length", "4", true),
			new SettingKeyMeta("captcha.width", "captcha", "INT", "验证码图片宽度（px）", "cloudwaer.captcha.width", "160",
					true),
			new SettingKeyMeta("captcha.height", "captcha", "INT", "验证码图片高度（px）", "cloudwaer.captcha.height", "40",
					true),
			// ---- 登录与 Token ----
			new SettingKeyMeta("jwt.expiration", "jwt", "LONG", "Token 有效期（毫秒）", "jwt.expiration", "86400000", true),
			new SettingKeyMeta("jwt.allow-multiple-login", "jwt", "BOOL", "是否允许多处登录（false=新登录踢旧会话）",
					"jwt.allow-multiple-login", "true", true),
			new SettingKeyMeta("auth.register.enabled", "auth", "BOOL", "匿名注册开关", null, "true", true),
			new SettingKeyMeta("login.fail-max", "auth", "INT", "登录失败锁定次数", null, null, false),
			new SettingKeyMeta("login.lock-minutes", "auth", "INT", "登录失败锁定时长（分钟）", null, null, false),
			// ---- 安全 ----
			new SettingKeyMeta("security.password-transport.enabled", "security", "BOOL", "登录密码传输加密（RSA）", null,
					"false", true),
			new SettingKeyMeta("security.password-transport.public-key", "security", "STRING", "密码加密公钥（PEM，下发登录页）",
					null, null, true),
			new SettingKeyMeta("security.password-transport.private-key", "security", "SECRET", "密码加密私钥（PEM，仅服务端）",
					null, null, true),
			new SettingKeyMeta("security.setting-encrypt.enabled", "security", "BOOL", "敏感配置值密文存储", null, null, false),
			// ---- 系统 ----
			new SettingKeyMeta("log.enabled", "system", "BOOL", "请求日志开关", "cloudwaer.log.enabled", "true", true),
			new SettingKeyMeta("log.exclude-paths", "system", "STRING", "请求日志排除路径（逗号分隔，Ant 风格）",
					"cloudwaer.log.exclude-paths", "", true),
			new SettingKeyMeta("api-doc.enabled", "system", "BOOL", "API 文档（Swagger）开关", "springdoc.api-docs.enabled",
					"true", true),
			new SettingKeyMeta("redis.cache.enabled", "system", "BOOL", "方法缓存 @RedisCache 开关",
					"cloudwaer.redis.cache.enabled", "true", true),
			new SettingKeyMeta("redis.cache.default-seconds", "system", "LONG", "方法缓存默认 TTL（秒，注解未指定时）",
					"cloudwaer.redis.cache.default-time-seconds", "86400", true),
			new SettingKeyMeta("upload.max-size", "system", "INT", "单文件上传大小限制（MB）", null, null, false),
			new SettingKeyMeta("upload.allowed-types", "system", "STRING", "允许上传的文件类型（逗号分隔）", null, null, false));

	@Autowired
	private SysSettingMapper settingMapper;

	@Autowired
	private SysOauthProviderMapper oauthProviderMapper;

	@Autowired
	private GlobalSettingService globalSettingService;

	@Autowired
	private PasswordTransportService passwordTransportService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment environment;

	/**
	 * 设置列表：已知键全量 + 库中自定义键；含生效值、来源、待发布标记
	 */
	public List<SettingItemDTO> listItems() {
		Map<String, SysSetting> dbMap = loadDbMap();
		Map<String, String> snapshot = globalSettingService.currentSnapshot();

		Map<String, SettingItemDTO> result = new LinkedHashMap<>();
		for (SettingKeyMeta meta : KEY_REGISTRY) {
			result.put(meta.key, buildItem(meta, dbMap.get(meta.key), snapshot));
		}
		// 库中额外的自定义键（不在注册表也展示）
		for (Map.Entry<String, SysSetting> entry : dbMap.entrySet()) {
			if (!result.containsKey(entry.getKey())) {
				SysSetting row = entry.getValue();
				SettingKeyMeta meta = new SettingKeyMeta(row.getSettingKey(), row.getGroupKey(), row.getValueType(),
						row.getRemark(), null, null, true);
				result.put(entry.getKey(), buildItem(meta, row, snapshot));
			}
		}
		return new ArrayList<>(result.values());
	}

	/**
	 * 保存设置（只写库，不发布）
	 */
	public Boolean save(List<SettingSaveItem> items) {
		if (items == null || items.isEmpty()) {
			throw new BusinessException("保存内容不能为空");
		}
		Map<String, SysSetting> dbMap = loadDbMap();
		for (SettingSaveItem item : items) {
			String key = item.getSettingKey() == null ? null : item.getSettingKey().trim();
			if (!StringUtils.hasText(key) || key.length() > MAX_KEY_LENGTH) {
				throw new BusinessException("配置键不合法");
			}
			String value = normalizeValue(item.getSettingValue());
			if (value != null && value.length() > MAX_VALUE_LENGTH) {
				throw new BusinessException("配置值过长: " + key);
			}
			SettingKeyMeta known = findMeta(key);
			if (known != null && !known.implemented) {
				throw new BusinessException("该配置暂未完成: " + key);
			}
			SysSetting existing = dbMap.get(key);
			if (existing != null) {
				existing.setSettingValue(value);
				settingMapper.updateById(existing);
			}
			else {
				SettingKeyMeta meta = findMeta(key);
				SysSetting row = new SysSetting();
				row.setSettingKey(key);
				row.setSettingValue(value);
				row.setValueType(meta != null ? meta.valueType : "STRING");
				row.setGroupKey(meta != null ? meta.groupKey : "system");
				row.setRemark(meta != null ? meta.remark : null);
				settingMapper.insert(row);
			}
		}
		log.info("全局设置已保存（未发布）: keys={}", items.stream().map(SettingSaveItem::getSettingKey).toList());
		return Boolean.TRUE;
	}

	/**
	 * 恢复默认：清空已保存值（回到 yml/代码兜底）。未发布的清空同样需发布后生效。
	 */
	public Boolean reset(List<String> keys) {
		if (keys == null || keys.isEmpty()) {
			throw new BusinessException("配置键不能为空");
		}
		Map<String, SysSetting> dbMap = loadDbMap();
		for (String key : keys) {
			SysSetting existing = dbMap.get(key == null ? null : key.trim());
			if (existing != null) {
				existing.setSettingValue(null);
				settingMapper.updateById(existing);
			}
		}
		log.info("全局设置已恢复默认（未发布）: keys={}", keys);
		return Boolean.TRUE;
	}

	/**
	 * 发布设置：库中非空值 + 派生的公开第三方登录列表 → 写 Redis 快照并广播，各实例立即生效
	 */
	public Map<String, Object> publish() {
		Map<String, String> values = new HashMap<>();
		for (SysSetting row : settingMapper.selectList(new LambdaQueryWrapper<>())) {
			if (StringUtils.hasText(row.getSettingValue())) {
				values.put(row.getSettingKey(), row.getSettingValue());
			}
		}
		values.put(GlobalSettingService.OAUTH_PUBLIC_KEY, buildPublicOauthJson());
		boolean ok = globalSettingService.publish(values);
		Map<String, Object> result = new HashMap<>();
		result.put("success", ok);
		result.put("keyCount", values.size());
		result.put("publishTime", globalSettingService.getRaw(GlobalSettingService.PUBLISH_TIME_KEY));
		List<String> warnings = new ArrayList<>();
		boolean ptEnabled = "true".equalsIgnoreCase(values.get(PasswordTransportService.KEY_PT_ENABLED))
				|| "1".equals(values.get(PasswordTransportService.KEY_PT_ENABLED));
		if (ptEnabled && (!StringUtils.hasText(values.get(PasswordTransportService.KEY_PT_PUBLIC_KEY))
				|| !StringUtils.hasText(values.get(PasswordTransportService.KEY_PT_PRIVATE_KEY)))) {
			warnings.add("已启用登录密码加密但密钥对不完整：请先「生成 RSA 密钥对」，并随本次发布一起生效，否则登录会报解密失败");
		}
		result.put("warnings", warnings);
		return result;
	}

	/**
	 * 生成密码传输加密的 RSA 密钥对并写入设置（草稿，发布后生效）
	 */
	public Map<String, Object> generatePasswordTransportKeys() {
		Map<String, String> keyPair = passwordTransportService.generateKeyPairPem();
		List<SettingSaveItem> items = new ArrayList<>();
		SettingSaveItem pub = new SettingSaveItem();
		pub.setSettingKey(PasswordTransportService.KEY_PT_PUBLIC_KEY);
		pub.setSettingValue(keyPair.get("publicKey"));
		items.add(pub);
		SettingSaveItem priv = new SettingSaveItem();
		priv.setSettingKey(PasswordTransportService.KEY_PT_PRIVATE_KEY);
		priv.setSettingValue(keyPair.get("privateKey"));
		items.add(priv);
		save(items);
		Map<String, Object> result = new HashMap<>();
		result.put("publicKey", keyPair.get("publicKey"));
		result.put("hint", "密钥对已保存为草稿；开启「登录密码传输加密」并点击「发布设置」后生效");
		return result;
	}

	/**
	 * 已启用第三方登录的公开信息（仅 type/name/icon），随设置快照分发
	 */
	private String buildPublicOauthJson() {
		List<SysOauthProvider> providers = oauthProviderMapper
			.selectList(new LambdaQueryWrapper<SysOauthProvider>().orderByAsc(SysOauthProvider::getSort));
		List<Map<String, String>> pub = providers.stream()
			.filter(p -> p.getEnabled() != null && p.getEnabled() == 1)
			.map(p -> {
				Map<String, String> item = new LinkedHashMap<>();
				item.put("type", p.getProviderType());
				item.put("name", p.getProviderName());
				if (StringUtils.hasText(p.getIcon())) {
					item.put("icon", p.getIcon());
				}
				return item;
			})
			.collect(Collectors.toList());
		try {
			return objectMapper.writeValueAsString(pub);
		}
		catch (Exception e) {
			log.warn("公开第三方登录列表序列化失败", e);
			return "[]";
		}
	}

	private SettingItemDTO buildItem(SettingKeyMeta meta, SysSetting row, Map<String, String> snapshot) {
		SettingItemDTO dto = new SettingItemDTO();
		dto.setSettingKey(meta.key);
		dto.setGroupKey(meta.groupKey);
		dto.setValueType(meta.valueType);
		dto.setRemark(meta.remark);
		String dbValue = row == null ? null : row.getSettingValue();
		dto.setDbValue(dbValue);

		String snapshotValue = snapshot.get(meta.key);
		if (StringUtils.hasText(snapshotValue)) {
			dto.setEffectiveValue(snapshotValue);
			dto.setSource("DB");
		}
		else {
			String ymlValue = meta.ymlProperty == null ? null : environment.getProperty(meta.ymlProperty);
			if (StringUtils.hasText(ymlValue)) {
				dto.setEffectiveValue(ymlValue);
				dto.setSource("YML");
			}
			else {
				dto.setEffectiveValue(meta.defaultDisplay);
				dto.setSource("DEFAULT");
			}
		}
		dto.setPendingPublish(!Objects.equals(dbValue, snapshotValue));
		dto.setImplemented(meta.implemented);
		return dto;
	}

	private Map<String, SysSetting> loadDbMap() {
		return settingMapper.selectList(new LambdaQueryWrapper<>())
			.stream()
			.collect(Collectors.toMap(SysSetting::getSettingKey, row -> row, (a, b) -> a, LinkedHashMap::new));
	}

	private SettingKeyMeta findMeta(String key) {
		return KEY_REGISTRY.stream().filter(m -> m.key.equals(key)).findFirst().orElse(null);
	}

	private String normalizeValue(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	/**
	 * 配置键元数据
	 */
	private static final class SettingKeyMeta {

		final String key;

		final String groupKey;

		final String valueType;

		final String remark;

		final String ymlProperty;

		final String defaultDisplay;

		/**
		 * 是否已实现可生效；false=暂未完成（拒绝保存，前端禁用）
		 */
		final boolean implemented;

		SettingKeyMeta(String key, String groupKey, String valueType, String remark, String ymlProperty,
				String defaultDisplay, boolean implemented) {
			this.key = key;
			this.groupKey = groupKey;
			this.valueType = valueType;
			this.remark = remark;
			this.ymlProperty = ymlProperty;
			this.defaultDisplay = defaultDisplay;
			this.implemented = implemented;
		}

	}

}
