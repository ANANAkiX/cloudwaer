package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 任务状态
 */
@Getter
public enum TaskStatus {

	PENDING(0, "pending", "待处理"), CLAIMED(1, "claimed", "已认领"), COMPLETED(2, "completed", "已完成"),
	REJECTED(3, "rejected", "已驳回"), TRANSFERRED(4, "transferred", "已转办"), CANCELED(5, "canceled", "已取消"),
	RETURNED(6, "returned", "已退回");

	private final Integer code;

	private final String value;

	private final String desc;

	TaskStatus(Integer code, String value, String desc) {
		this.code = code;
		this.value = value;
		this.desc = desc;
	}

	public static TaskStatus getByCode(Integer code) {
		for (TaskStatus s : values()) {
			if (s.code.equals(code)) {
				return s;
			}
		}
		return null;
	}

}
