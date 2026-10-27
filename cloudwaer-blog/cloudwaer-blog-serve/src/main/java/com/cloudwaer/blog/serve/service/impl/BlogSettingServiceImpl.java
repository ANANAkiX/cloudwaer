package com.cloudwaer.blog.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudwaer.blog.api.dto.ArticleLayoutSetting;
import com.cloudwaer.blog.api.dto.HomeLayoutSetting;
import com.cloudwaer.blog.api.dto.NavSettingItem;
import com.cloudwaer.blog.api.dto.PageSettingItem;
import com.cloudwaer.blog.api.dto.SiteSettingDTO;
import com.cloudwaer.blog.serve.entity.BlogSiteSetting;
import com.cloudwaer.blog.serve.mapper.BlogSiteSettingMapper;
import com.cloudwaer.blog.serve.service.BlogSettingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客站点设置服务实现（key-value upsert；复杂配置 value 存 JSON 字符串）
 *
 * @author cloudwaer
 */
@Slf4j
@Service
public class BlogSettingServiceImpl implements BlogSettingService {

	/**
	 * 已知配置键顺序（读取/写入均按此序）
	 */
	private static final String[] KEYS = { "siteTitle", "siteDescription", "favicon", "enableSearch",
			"requireArticleAudit", "layout", "footerText", "nav", "pages", "articleLayout", "homeLayout" };

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Autowired
	private BlogSiteSettingMapper settingMapper;

	@Override
	public SiteSettingDTO getSettings() {
		Map<String, String> map = loadMap();
		SiteSettingDTO dto = new SiteSettingDTO();
		dto.setSiteTitle(defaultIfBlank(map.get("siteTitle"), "CloudWaer 博客"));
		dto.setSiteDescription(nullToEmpty(map.get("siteDescription")));
		dto.setFavicon(nullToEmpty(map.get("favicon")));
		dto.setEnableSearch(parseEnableSearch(map.get("enableSearch")));
		dto.setRequireArticleAudit(parseRequireArticleAudit(map.get("requireArticleAudit")));
		dto.setLayout(defaultIfBlank(map.get("layout"), "default"));
		dto.setFooterText(nullToEmpty(map.get("footerText")));
		dto.setNav(parseNav(map.get("nav")));
		dto.setPages(parsePages(map.get("pages")));
		dto.setArticleLayout(parseArticleLayout(map.get("articleLayout")));
		dto.setHomeLayout(parseHomeLayout(map.get("homeLayout")));
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveSettings(SiteSettingDTO dto) {
		if (dto == null) {
			return Boolean.FALSE;
		}
		Map<String, String> values = toValueMap(dto);
		Map<String, BlogSiteSetting> existing = loadEntityMap();
		for (String key : KEYS) {
			String value = values.get(key);
			BlogSiteSetting row = existing.get(key);
			if (row == null) {
				row = new BlogSiteSetting();
				row.setSettingKey(key);
				row.setSettingValue(value);
				row.setRemark(remarkOf(key));
				settingMapper.insert(row);
			}
			else {
				row.setSettingValue(value);
				settingMapper.updateById(row);
			}
		}
		return Boolean.TRUE;
	}

	private Map<String, String> loadMap() {
		Map<String, String> map = new LinkedHashMap<>();
		for (BlogSiteSetting row : settingMapper
			.selectList(new LambdaQueryWrapper<BlogSiteSetting>().orderByAsc(BlogSiteSetting::getId))) {
			map.put(row.getSettingKey(), row.getSettingValue());
		}
		return map;
	}

	private Map<String, BlogSiteSetting> loadEntityMap() {
		Map<String, BlogSiteSetting> map = new LinkedHashMap<>();
		List<BlogSiteSetting> rows = settingMapper
			.selectList(new LambdaQueryWrapper<BlogSiteSetting>().orderByAsc(BlogSiteSetting::getId));
		for (BlogSiteSetting row : rows) {
			map.put(row.getSettingKey(), row);
		}
		return map;
	}

	private Map<String, String> toValueMap(SiteSettingDTO dto) {
		Map<String, String> values = new LinkedHashMap<>();
		values.put("siteTitle", defaultIfBlank(dto.getSiteTitle(), "CloudWaer 博客"));
		values.put("siteDescription", nullToEmpty(dto.getSiteDescription()));
		values.put("favicon", nullToEmpty(dto.getFavicon()));
		values.put("enableSearch", dto.getEnableSearch() != null && dto.getEnableSearch() == 1 ? "1" : "0");
		values.put("requireArticleAudit",
				dto.getRequireArticleAudit() != null && dto.getRequireArticleAudit() == 0 ? "0" : "1");
		values.put("layout", defaultIfBlank(dto.getLayout(), "default"));
		values.put("footerText", nullToEmpty(dto.getFooterText()));
		values.put("nav", toJson(dto.getNav() != null ? dto.getNav() : defaultNav()));
		values.put("pages", toJson(dto.getPages() != null ? dto.getPages() : defaultPages()));
		values.put("articleLayout",
				toJson(dto.getArticleLayout() != null ? dto.getArticleLayout() : defaultArticleLayout()));
		values.put("homeLayout", toJson(dto.getHomeLayout() != null ? dto.getHomeLayout() : defaultHomeLayout()));
		return values;
	}

	private Integer parseEnableSearch(String raw) {
		if (!StringUtils.hasText(raw)) {
			return 1;
		}
		return "1".equals(raw.trim()) ? 1 : 0;
	}

	/**
	 * 文章审核开关，缺省为开（1）
	 */
	private Integer parseRequireArticleAudit(String raw) {
		if (!StringUtils.hasText(raw)) {
			return 1;
		}
		return "0".equals(raw.trim()) ? 0 : 1;
	}

	private String nullToEmpty(String raw) {
		return raw == null ? "" : raw;
	}

	private String defaultIfBlank(String raw, String fallback) {
		return StringUtils.hasText(raw) ? raw : fallback;
	}

	private String toJson(Object value) {
		try {
			return MAPPER.writeValueAsString(value);
		}
		catch (Exception e) {
			log.warn("序列化站点设置 JSON 失败: {}", e.getMessage());
			return "";
		}
	}

	private <T> T fromJson(String raw, TypeReference<T> type, T fallback) {
		if (!StringUtils.hasText(raw)) {
			return fallback;
		}
		try {
			T parsed = MAPPER.readValue(raw, type);
			return parsed != null ? parsed : fallback;
		}
		catch (Exception e) {
			log.warn("解析站点设置 JSON 失败，回落默认: {}", e.getMessage());
			return fallback;
		}
	}

	// ========== JSON 配置默认值 ==========

	private List<NavSettingItem> defaultNav() {
		List<NavSettingItem> nav = new ArrayList<>();
		nav.add(navItem("home", "首页", "/", 1));
		nav.add(navItem("forum", "微论坛", "/forum", 2));
		nav.add(navItem("archive", "归档", "/archive", 3));
		nav.add(navItem("about", "关于", "/about", 4));
		nav.add(navItem("write", "写文章", "/write", 5));
		return nav;
	}

	private NavSettingItem navItem(String key, String label, String path, int order) {
		NavSettingItem item = new NavSettingItem();
		item.setKey(key);
		item.setLabel(label);
		item.setPath(path);
		item.setVisible(Boolean.TRUE);
		item.setOrder(order);
		return item;
	}

	private Map<String, PageSettingItem> defaultPages() {
		Map<String, PageSettingItem> pages = new LinkedHashMap<>();
		PageSettingItem about = new PageSettingItem();
		about.setTitle("关于云记");
		about.setContent("云记博客是基于 cloudwaer-blog 的多作者技术写作与轻论坛平台，记录工程实践、踩坑与思考。");
		pages.put("about", about);
		return pages;
	}

	private ArticleLayoutSetting defaultArticleLayout() {
		ArticleLayoutSetting setting = new ArticleLayoutSetting();
		setting.setTocPosition("right");
		return setting;
	}

	private HomeLayoutSetting defaultHomeLayout() {
		HomeLayoutSetting setting = new HomeLayoutSetting();
		setting.setShowFeatured(Boolean.TRUE);
		setting.setShowTagCloud(Boolean.TRUE);
		setting.setShowHot(Boolean.TRUE);
		return setting;
	}

	private List<NavSettingItem> parseNav(String raw) {
		List<NavSettingItem> nav = fromJson(raw, new TypeReference<List<NavSettingItem>>() {
		}, null);
		if (nav == null || nav.isEmpty()) {
			return defaultNav();
		}
		nav.sort((a, b) -> {
			int oa = a.getOrder() != null ? a.getOrder() : Integer.MAX_VALUE;
			int ob = b.getOrder() != null ? b.getOrder() : Integer.MAX_VALUE;
			return Integer.compare(oa, ob);
		});
		return nav;
	}

	private Map<String, PageSettingItem> parsePages(String raw) {
		Map<String, PageSettingItem> pages = fromJson(raw, new TypeReference<Map<String, PageSettingItem>>() {
		}, null);
		if (pages == null || pages.isEmpty()) {
			return defaultPages();
		}
		return pages;
	}

	private ArticleLayoutSetting parseArticleLayout(String raw) {
		ArticleLayoutSetting setting = fromJson(raw, new TypeReference<ArticleLayoutSetting>() {
		}, null);
		if (setting == null) {
			return defaultArticleLayout();
		}
		if (!StringUtils.hasText(setting.getTocPosition())) {
			setting.setTocPosition("right");
		}
		return setting;
	}

	private HomeLayoutSetting parseHomeLayout(String raw) {
		HomeLayoutSetting setting = fromJson(raw, new TypeReference<HomeLayoutSetting>() {
		}, null);
		if (setting == null) {
			return defaultHomeLayout();
		}
		if (setting.getShowFeatured() == null) {
			setting.setShowFeatured(Boolean.TRUE);
		}
		if (setting.getShowTagCloud() == null) {
			setting.setShowTagCloud(Boolean.TRUE);
		}
		if (setting.getShowHot() == null) {
			setting.setShowHot(Boolean.TRUE);
		}
		return setting;
	}

	private String remarkOf(String key) {
		return switch (key) {
			case "siteTitle" -> "站点标题";
			case "siteDescription" -> "站点副标题/描述";
			case "favicon" -> "站点图标 URL（favicon/icon）";
			case "enableSearch" -> "是否开启搜索：0-关闭，1-开启";
			case "requireArticleAudit" -> "非管理员发布文章是否需审核：0-免审，1-需审核";
			case "layout" -> "网站布局：default / compact / magazine";
			case "footerText" -> "页脚文案";
			case "nav" -> "导航菜单 JSON 数组（key/label/path/visible/order）";
			case "pages" -> "独立页面文案 JSON（如 about.title/about.content）";
			case "articleLayout" -> "文章详情布局 JSON（tocPosition: left/right/off）";
			case "homeLayout" -> "首页模块开关 JSON（showFeatured/showTagCloud/showHot）";
			default -> "";
		};
	}

}
