package com.cloudwaer.flowable.serve.constant;

public final class FlowableConstants {

	private FlowableConstants() {
	}

	/** 草稿 */
	public static final int MODEL_STATUS_DRAFT = 0;

	/** 已发布 */
	public static final int MODEL_STATUS_RELEASED = 1;

	/** 已归档（历史版本） */
	public static final int MODEL_STATUS_ARCHIVED = 2;

	/** 已更新未发布 */
	public static final int MODEL_STATUS_UPDATED = 3;

	/** 已回滚未发布 */
	public static final int MODEL_STATUS_ROLLED_BACK = 4;

	/** 已关闭（手动下线发布，可删除） */
	public static final int MODEL_STATUS_CLOSED = 5;

	public static final int DEPLOY_STATUS_ACTIVE = 1;

	public static final int DEPLOY_STATUS_ARCHIVED = 0;

	public static final String EVENT_TASK_CREATE = "task_create";

	public static final String EVENT_TASK_COMPLETE = "task_complete";

	public static final String EVENT_PROCESS_START = "process_start";

	public static final String EVENT_PROCESS_END = "process_end";

	public static final String EVENT_TIMEOUT = "timeout";

	public static final String EVENT_SIGNAL = "signal";

	public static final String ACTION_LOG = "log";

	public static final String ACTION_NOTIFY = "notify";

	public static final String ACTION_HTTP = "http";

	public static final String ACTION_MQ = "mq";

}
