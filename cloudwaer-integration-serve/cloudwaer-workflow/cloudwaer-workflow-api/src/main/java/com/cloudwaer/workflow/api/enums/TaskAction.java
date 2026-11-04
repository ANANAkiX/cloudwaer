package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 任务操作类型
 */
@Getter
public enum TaskAction {

	APPROVE("approve", "同意"), REJECT("reject", "驳回"), TRANSFER("transfer", "转办"), RETURN("return", "退回"),
	COUNTERSIGN("countersign", "加签"), CC("cc", "抄送");

	private final String code;

	private final String desc;

	TaskAction(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
