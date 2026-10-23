package com.cloudwaer.flowable.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程操作历史时间线条目。
 */
@Data
public class FlowableProcessHistoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * 操作人（系统节点可为「系统」）
	 */
	private String userName;

	/**
	 * 动作短语，如：创建流程 / 等待处理 / 完成任务 / 重新发起 / 流程结束
	 */
	private String action;

	/**
	 * 事件类型：start / restart / wait / complete / approve / reject / end
	 */
	private String type;

	/**
	 * 事件时间
	 */
	private LocalDateTime time;

	/**
	 * 节点处理结束时间（等待类节点可为空）
	 */
	private LocalDateTime endTime;

	/**
	 * 处理结果：approve / reject / 其它原始值；等待/系统节点为空
	 */
	private String result;

	/**
	 * 处理意见原文（不带「处理意见:」前缀）
	 */
	private String comment;

	/**
	 * 耗时展示文本，如 1200ms / 2m 30s
	 */
	private String duration;

}
