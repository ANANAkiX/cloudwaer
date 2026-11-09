package com.cloudwaer.workflow.serve.engine;

import lombok.Data;

import java.util.List;

/**
 * 节点执行结果
 */
@Data
public class NodeResult {

	public enum NextType {

		/** 自动流转到下一个节点 */
		AUTO,
		/** 等待人工处理（用户任务） */
		WAIT,
		/** 流程结束 */
		END,
		/** 等待定时触发 */
		TIMER,
		/** 等待外部信号 */
		SIGNAL

	}

	private NextType nextType = NextType.AUTO;

	/** 下一个节点ID列表（并行分支时可能多个） */
	private List<String> nextNodeIds;

	/** 等待时的任务ID（用户任务） */
	private String waitingTaskId;

	/** 错误信息 */
	private String error;

	public static NodeResult auto(List<String> nextNodeIds) {
		NodeResult r = new NodeResult();
		r.nextType = NextType.AUTO;
		r.nextNodeIds = nextNodeIds;
		return r;
	}

	public static NodeResult wait(String taskId) {
		NodeResult r = new NodeResult();
		r.nextType = NextType.WAIT;
		r.waitingTaskId = taskId;
		return r;
	}

	public static NodeResult end() {
		NodeResult r = new NodeResult();
		r.nextType = NextType.END;
		return r;
	}

	public static NodeResult timer(List<String> nextNodeIds) {
		NodeResult r = new NodeResult();
		r.nextType = NextType.TIMER;
		r.nextNodeIds = nextNodeIds;
		return r;
	}

	public static NodeResult signal() {
		NodeResult r = new NodeResult();
		r.nextType = NextType.SIGNAL;
		return r;
	}

	public static NodeResult error(String message) {
		NodeResult r = new NodeResult();
		r.error = message;
		return r;
	}

	public boolean hasError() {
		return error != null;
	}

}
