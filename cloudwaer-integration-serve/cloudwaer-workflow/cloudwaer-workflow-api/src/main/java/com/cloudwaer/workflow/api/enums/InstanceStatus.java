package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 流程实例状态
 */
@Getter
public enum InstanceStatus {

	RUNNING(0, "running", "运行中"), COMPLETED(1, "completed", "已完成"), SUSPENDED(2, "suspended", "已挂起"),
	REJECTED(3, "rejected", "被驳回"), TERMINATED(4, "terminated", "已终止"), CANCELED(5, "canceled", "已取消");

	private final Integer code;

	private final String value;

	private final String desc;

	InstanceStatus(Integer code, String value, String desc) {
		this.code = code;
		this.value = value;
		this.desc = desc;
	}

	public static InstanceStatus getByCode(Integer code) {
		for (InstanceStatus s : values()) {
			if (s.code.equals(code)) {
				return s;
			}
		}
		return null;
	}

}
