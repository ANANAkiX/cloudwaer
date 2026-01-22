package com.cloudwaer.flowable.serve.constant;

public final class FlowableConstants {

	private FlowableConstants() {
	}

	public static final int MODEL_STATUS_DRAFT = 0;

	public static final int MODEL_STATUS_RELEASED = 1;

	public static final int MODEL_STATUS_ARCHIVED = 2;

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
