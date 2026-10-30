package com.cloudwaer.common.core.config;

/**
 * 全局设置刷新感知：发布设置（或收到发布广播）后被回调，用于把设置应用为全局副作用（如 JVM 代理）。
 * <p>
 * 由 {@link GlobalSettingRefreshListener} 在启动与每次刷新后统一触发。
 *
 * @author cloudwaer
 */
public interface GlobalSettingRefreshAware {

	/**
	 * 设置快照刷新后回调（启动时也会调用一次）
	 */
	void onGlobalSettingRefreshed();

}
