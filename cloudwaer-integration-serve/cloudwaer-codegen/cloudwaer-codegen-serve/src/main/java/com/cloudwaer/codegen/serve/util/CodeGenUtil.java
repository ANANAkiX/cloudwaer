package com.cloudwaer.codegen.serve.util;

import org.springframework.stereotype.Component;

/**
 * 代码生成工具类
 *
 * @author cloudwaer
 */
@Component
public class CodeGenUtil {

	/**
	 * 将下划线命名转换为驼峰命名
	 */
	public String toCamelCase(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		String[] parts = name.toLowerCase().split("_");
		StringBuilder result = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			if (!parts[i].isEmpty()) {
				result.append(Character.toUpperCase(parts[i].charAt(0)));
				if (parts[i].length() > 1) {
					result.append(parts[i].substring(1));
				}
			}
		}
		return result.toString();
	}

	/**
	 * 将下划线命名转换为帕斯卡命名（首字母大写）
	 */
	public String toPascalCase(String name) {
		String camelCase = toCamelCase(name);
		if (camelCase == null || camelCase.isEmpty()) {
			return camelCase;
		}
		return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
	}

	/**
	 * 将驼峰命名转换为下划线命名
	 */
	public String toSnakeCase(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					result.append('_');
				}
				result.append(Character.toLowerCase(c));
			}
			else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * 首字母小写
	 */
	public String toFirstLowerCase(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * 首字母大写
	 */
	public String toFirstUpperCase(String name) {
		if (name == null || name.isEmpty()) {
			return name;
		}
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
