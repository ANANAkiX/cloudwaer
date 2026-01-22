package com.cloudwaer.common.core.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法ID生成器
 *
 * @author cloudwaer
 */
public class SnowflakeIdGenerator {

	private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

	/**
	 * 生成ID
	 * @return 生成的ID
	 */
	public static Long nextId() {
		return SNOWFLAKE.nextId();
	}

}
