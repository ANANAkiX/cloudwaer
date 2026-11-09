package com.cloudwaer.workflow.api.enums;

import lombok.Getter;

/**
 * 流程节点类型
 */
@Getter
public enum NodeType {

	START("start", "开始节点"), END("end", "结束节点"), USER_TASK("user_task", "用户任务"), CC("cc", "抄送"),
	CONDITION("condition", "条件分支"), PARALLEL("parallel", "并行分支"), TIMER("timer", "延时器"), TRIGGER("trigger", "触发器"),
	SERVICE("service", "服务任务"), SUB_PROCESS("sub_process", "子流程");

	private final String code;

	private final String desc;

	NodeType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static NodeType getByCode(String code) {
		for (NodeType type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		return null;
	}

}
