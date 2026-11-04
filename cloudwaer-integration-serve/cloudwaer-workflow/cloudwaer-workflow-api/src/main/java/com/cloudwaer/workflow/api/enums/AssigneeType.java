package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 审批人类型
 */
@Getter
public enum AssigneeType {

	USER("user", "指定用户"), ROLE("role", "角色"), DEPT("dept", "部门"), VARIABLE("variable", "流程变量"),
	INITIATOR("initiator", "发起人"), LEADER("leader", "上级领导");

	private final String code;

	private final String desc;

	AssigneeType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
