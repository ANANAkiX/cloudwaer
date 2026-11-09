package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 触发器类型
 */
@Getter
public enum TriggerType {

	MANUAL("manual", "手动触发"), TIMER("timer", "定时触发"), SIGNAL("signal", "信号触发"), MESSAGE("message", "消息触发"),
	WEBHOOK("webhook", "Webhook触发");

	private final String code;

	private final String desc;

	TriggerType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
