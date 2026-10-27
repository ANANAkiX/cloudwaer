package com.cloudwaer.common.datasource;

import java.util.Map;

/**
 * 路由键解析：约定优先，配置覆盖。
 *
 * <ul>
 * <li>包名：{@code com.cloudwaer.<key>.serve...} → {@code <key>}；匹配不到返回 {@code null}</li>
 * <li>路径：{@code /<key>/...} → {@code <key>}（保留段回落 defaultKey）</li>
 * <li>{@link CloudwaerDataSource} 显式注解优先于约定</li>
 * </ul>
 */
public final class DataSourceRouteResolver {

	private static final String BASE_PACKAGE = "com.cloudwaer.";

	private DataSourceRouteResolver() {
	}

	/**
	 * 从类的包名解析路由键。
	 * @return 约定/配置命中时的键；无法判定时 {@code null}（调用方决定是否回落 defaultKey）
	 */
	public static String resolveFromPackage(String packageName, DataSourceRouteProperties props) {
		if (packageName == null || !packageName.startsWith(BASE_PACKAGE)) {
			return null;
		}
		String override = longestPrefixMatch(packageName, props.getPackagePrefixes());
		if (override != null) {
			return override;
		}
		// com.cloudwaer.<key>.serve[...]
		String rest = packageName.substring(BASE_PACKAGE.length());
		int dot = rest.indexOf('.');
		if (dot <= 0) {
			return null;
		}
		String key = rest.substring(0, dot);
		String tail = rest.substring(dot);
		if (tail.startsWith(".serve") && isRouteKey(key)) {
			return key;
		}
		return null;
	}

	/**
	 * 从请求 URI 解析路由键（始终给出明确键，未知回落 defaultKey）。
	 */
	public static String resolveFromPath(String uri, DataSourceRouteProperties props) {
		if (uri == null || uri.isEmpty() || "/".equals(uri)) {
			return props.getDefaultKey();
		}
		String override = longestPrefixMatch(uri, props.getPathPrefixes());
		if (override != null) {
			return override;
		}
		String path = uri.startsWith("/") ? uri.substring(1) : uri;
		int slash = path.indexOf('/');
		String segment = slash < 0 ? path : path.substring(0, slash);
		if (segment.isEmpty() || isReserved(segment, props)) {
			return props.getDefaultKey();
		}
		return isRouteKey(segment) ? segment : props.getDefaultKey();
	}

	private static boolean isReserved(String segment, DataSourceRouteProperties props) {
		String[] reserved = props.getReservedPathSegments();
		if (reserved == null) {
			return false;
		}
		for (String s : reserved) {
			if (s != null && s.equalsIgnoreCase(segment)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isRouteKey(String key) {
		if (key == null || key.isEmpty()) {
			return false;
		}
		char c = key.charAt(0);
		if (!Character.isLowerCase(c) || !Character.isLetter(c)) {
			return false;
		}
		for (int i = 1; i < key.length(); i++) {
			char ch = key.charAt(i);
			if (!Character.isLetterOrDigit(ch) && ch != '-' && ch != '_') {
				return false;
			}
		}
		return true;
	}

	private static String longestPrefixMatch(String value, Map<String, String> prefixes) {
		if (prefixes == null || prefixes.isEmpty()) {
			return null;
		}
		String bestPrefix = null;
		String bestValue = null;
		for (Map.Entry<String, String> e : prefixes.entrySet()) {
			String p = e.getKey();
			if (p == null || p.isEmpty()) {
				continue;
			}
			if (value.startsWith(p) && (bestPrefix == null || p.length() > bestPrefix.length())) {
				bestPrefix = p;
				bestValue = e.getValue();
			}
		}
		return bestValue;
	}

}
