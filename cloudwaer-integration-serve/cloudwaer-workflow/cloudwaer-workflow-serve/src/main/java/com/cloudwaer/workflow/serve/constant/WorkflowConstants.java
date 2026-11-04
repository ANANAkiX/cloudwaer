package com.cloudwaer.workflow.serve.constant;

/**
 * 工作流常量
 */
public final class WorkflowConstants {

	private WorkflowConstants() {
	}

	// ===== 流程定义状态 =====
	public static final int DEF_STATUS_DRAFT = 0;

	public static final int DEF_STATUS_PUBLISHED = 1;

	public static final int DEF_STATUS_ARCHIVED = 2;

	public static final int DEF_STATUS_OFFLINE = 3;

	// ===== 流程实例状态 =====
	public static final int INST_RUNNING = 0;

	public static final int INST_COMPLETED = 1;

	public static final int INST_SUSPENDED = 2;

	public static final int INST_REJECTED = 3;

	public static final int INST_TERMINATED = 4;

	public static final int INST_CANCELED = 5;

	// ===== 任务状态 =====
	public static final int TASK_PENDING = 0;

	public static final int TASK_CLAIMED = 1;

	public static final int TASK_COMPLETED = 2;

	public static final int TASK_REJECTED = 3;

	public static final int TASK_TRANSFERRED = 4;

	public static final int TASK_CANCELED = 5;

	public static final int TASK_RETURNED = 6;

	// ===== 节点类型 =====
	public static final String NODE_START = "start";

	public static final String NODE_END = "end";

	public static final String NODE_USER_TASK = "user_task";

	public static final String NODE_CC = "cc";

	public static final String NODE_CONDITION = "condition";

	public static final String NODE_PARALLEL = "parallel";

	public static final String NODE_TIMER = "timer";

	public static final String NODE_TRIGGER = "trigger";

	public static final String NODE_SERVICE = "service";

	public static final String NODE_SUB_PROCESS = "sub_process";

	// ===== 历史动作 =====
	public static final String ACTION_START = "start";

	public static final String ACTION_CREATE = "create";

	public static final String ACTION_CLAIM = "claim";

	public static final String ACTION_APPROVE = "approve";

	public static final String ACTION_REJECT = "reject";

	public static final String ACTION_TRANSFER = "transfer";

	public static final String ACTION_RETURN = "return";

	public static final String ACTION_CC = "cc";

	public static final String ACTION_END = "end";

	public static final String ACTION_RESTART = "restart";

}
