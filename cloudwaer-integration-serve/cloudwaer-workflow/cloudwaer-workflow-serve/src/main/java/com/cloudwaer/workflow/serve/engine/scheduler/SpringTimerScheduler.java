package com.cloudwaer.workflow.serve.engine.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.cloudwaer.workflow.serve.engine.ProcessEngine;

/**
 * Spring 调度器实现：@Scheduled + 内存队列 + DB 轮询兜底。
 * <p>
 * 当前为简化实现（内存 Map），后续可扩展为 DB 持久化 + 锁。
 */
@Slf4j
@Component
public class SpringTimerScheduler implements SchedulerProvider {

	/** 延时任务表：jobId → ScheduledJob */
	private final Map<String, ScheduledJob> jobMap = new ConcurrentHashMap<>();

	@Autowired(required = false)
	@org.springframework.context.annotation.Lazy
	private ProcessEngine processEngine;

	@Override
	public String getType() {
		return "spring";
	}

	@Override
	public void schedule(String jobId, String instanceId, String nodeId, long delayMs) {
		LocalDateTime fireAt = LocalDateTime.now().plusNanos(delayMs * 1_000_000L);
		jobMap.put(jobId, new ScheduledJob(jobId, instanceId, nodeId, fireAt));
		log.info("调度延时任务: jobId={}, instanceId={}, nodeId={}, delayMs={}", jobId, instanceId, nodeId, delayMs);
	}

	@Override
	public void cancel(String jobId) {
		jobMap.remove(jobId);
		log.info("取消延时任务: jobId={}", jobId);
	}

	/**
	 * 每 5 秒扫描一次到期任务
	 */
	@Scheduled(fixedDelay = 5000)
	public void scanAndFire() {
		LocalDateTime now = LocalDateTime.now();
		for (Map.Entry<String, ScheduledJob> entry : jobMap.entrySet()) {
			ScheduledJob job = entry.getValue();
			if (job.fireAt.isBefore(now)) {
				jobMap.remove(entry.getKey());
				fireJob(job);
			}
		}
	}

	private void fireJob(ScheduledJob job) {
		log.info("延时任务到期: jobId={}, instanceId={}, nodeId={}", job.jobId, job.instanceId, job.nodeId);
		// 调用 ProcessEngine 推进流程到延时节点的下一个节点
		try {
			if (processEngine != null) {
				processEngine.resumeFromNode(job.instanceId, job.nodeId);
			}
		}
		catch (Exception e) {
			log.error("延时任务唤醒失败: jobId={}, err={}", job.jobId, e.getMessage(), e);
		}
	}

	/**
	 * 内部延时任务模型
	 */
	private static class ScheduledJob {

		final String jobId;

		final String instanceId;

		final String nodeId;

		final LocalDateTime fireAt;

		ScheduledJob(String jobId, String instanceId, String nodeId, LocalDateTime fireAt) {
			this.jobId = jobId;
			this.instanceId = instanceId;
			this.nodeId = nodeId;
			this.fireAt = fireAt;
		}

	}

}
