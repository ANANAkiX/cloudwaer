package com.cloudwaer.common.core.config;

import com.cloudwaer.common.core.service.GlobalSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 出站 HTTP 代理（JVM 系统属性）应用器：设置 → {@code http(s).proxyHost/Port} +
 * {@code http.nonProxyHosts}。
 * <p>
 * 读取顺序与全局设置一致：已发布快照 → yml 兜底（{@code cloudwaer.proxy.*}）。发布设置后由
 * {@link GlobalSettingRefreshListener} 触发本类重新应用，无需重启。开关关闭时清除系统属性。
 * <p>
 * 默认 non-proxy-hosts 含 localhost，避免单体 boot 的 Feign 内部调用被带进外部代理；微服务部署请把 内网网段/服务名补进该配置。
 *
 * @author cloudwaer
 */
@Slf4j
@Component
public class JvmProxySettingApplier implements GlobalSettingRefreshAware {

	private static final String DEFAULT_NON_PROXY_HOSTS = "localhost|127.0.0.1|::1";

	@Autowired
	private GlobalSettingService globalSettingService;

	@Value("${cloudwaer.proxy.enabled:false}")
	private boolean ymlEnabled;

	@Value("${cloudwaer.proxy.host:}")
	private String ymlHost;

	@Value("${cloudwaer.proxy.port:0}")
	private int ymlPort;

	@Value("${cloudwaer.proxy.non-proxy-hosts:" + DEFAULT_NON_PROXY_HOSTS + "}")
	private String ymlNonProxyHosts;

	@Override
	public void onGlobalSettingRefreshed() {
		apply();
	}

	/**
	 * 按当前设置（快照 → yml 兜底）应用或清除 JVM 代理系统属性
	 */
	public void apply() {
		boolean enabled = globalSettingService.getBool(GlobalSettingService.KEY_PROXY_ENABLED, ymlEnabled);
		String host = globalSettingService.getString(GlobalSettingService.KEY_PROXY_HOST, ymlHost);
		int port = globalSettingService.getInt(GlobalSettingService.KEY_PROXY_PORT, ymlPort);
		String nonProxyHosts = globalSettingService.getString(GlobalSettingService.KEY_PROXY_NON_PROXY_HOSTS,
				ymlNonProxyHosts);

		if (enabled && StringUtils.hasText(host) && port > 0) {
			String nonProxy = StringUtils.hasText(nonProxyHosts) ? nonProxyHosts : DEFAULT_NON_PROXY_HOSTS;
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", String.valueOf(port));
			System.setProperty("https.proxyHost", host);
			System.setProperty("https.proxyPort", String.valueOf(port));
			System.setProperty("http.nonProxyHosts", nonProxy);
			log.info("JVM 出站代理已应用: host={}, port={}, nonProxyHosts={}", host, port, nonProxy);
		}
		else {
			clear();
			if (enabled) {
				log.warn("出站代理已开启但 host/port 不完整，保持直连: host={}, port={}", host, port);
			}
		}
	}

	private void clear() {
		System.clearProperty("http.proxyHost");
		System.clearProperty("http.proxyPort");
		System.clearProperty("https.proxyHost");
		System.clearProperty("https.proxyPort");
		System.clearProperty("http.nonProxyHosts");
	}

}
