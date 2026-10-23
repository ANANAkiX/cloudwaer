package com.cloudwaer.flowable.serve.listener;

import com.cloudwaer.admin.api.feign.AdminFeignClient;
import com.cloudwaer.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 候选组（角色ID）运行时展开为候选用户。
 * <p>
 * Feign 用 ObjectProvider 惰性获取，避免 Flowable 引擎初始化阶段强依赖 admin 服务导致启动失败。
 */
@Slf4j
@Component("candidateExpandTaskListener")
public class CandidateExpandTaskListener implements TaskListener {

	@Autowired(required = false)
	private ObjectProvider<AdminFeignClient> adminFeignClientProvider;

	@Autowired(required = false)
	private ObjectProvider<TaskService> taskServiceProvider;

	@Override
	public void notify(DelegateTask delegateTask) {
		if (delegateTask == null || !"create".equals(delegateTask.getEventName())) {
			return;
		}
		try {
			TaskService taskService = taskServiceProvider != null ? taskServiceProvider.getIfAvailable() : null;
			if (taskService == null) {
				return;
			}
			List<String> roleIds = resolveCandidateGroupIds(taskService, delegateTask.getId());
			if (roleIds.isEmpty()) {
				return;
			}
			List<String> usernames = expandRoleIdsToUsernames(roleIds);
			if (usernames.isEmpty()) {
				log.warn("候选组展开为空: taskId={}, roleIds={}", delegateTask.getId(), roleIds);
				return;
			}
			for (String username : usernames) {
				taskService.addCandidateUser(delegateTask.getId(), username);
			}
			log.info("候选组已展开: taskId={}, roles={}, users={}", delegateTask.getId(), roleIds, usernames);
		}
		catch (Exception e) {
			log.error("候选组展开失败: taskId={}, error={}", delegateTask.getId(), e.getMessage(), e);
		}
	}

	public List<String> expandRoleIdsToUsernames(List<String> roleIds) {
		if (roleIds == null || roleIds.isEmpty() || adminFeignClientProvider == null) {
			return List.of();
		}
		AdminFeignClient adminFeignClient = adminFeignClientProvider.getIfAvailable();
		if (adminFeignClient == null) {
			return List.of();
		}
		List<Long> ids = new ArrayList<>();
		for (String roleId : roleIds) {
			if (roleId == null || roleId.isBlank()) {
				continue;
			}
			try {
				ids.add(Long.parseLong(roleId.trim()));
			}
			catch (NumberFormatException ignored) {
				// 非数字角色ID跳过
			}
		}
		if (ids.isEmpty()) {
			return List.of();
		}
		try {
			Result<List<String>> result = adminFeignClient.getUsernamesByRoleIds(ids);
			if (result != null && result.getData() != null) {
				return result.getData();
			}
		}
		catch (Exception e) {
			log.error("调用 admin 展开角色失败: roleIds={}, err={}", ids, e.getMessage());
		}
		return List.of();
	}

	private List<String> resolveCandidateGroupIds(TaskService taskService, String taskId) {
		if (taskService == null || taskId == null) {
			return List.of();
		}
		Set<String> groups = new LinkedHashSet<>();
		try {
			List<IdentityLink> links = taskService.getIdentityLinksForTask(taskId);
			if (links == null) {
				return List.of();
			}
			for (IdentityLink link : links) {
				if (IdentityLinkType.CANDIDATE.equals(link.getType()) && link.getGroupId() != null
						&& !link.getGroupId().isBlank()) {
					groups.add(link.getGroupId().trim());
				}
			}
		}
		catch (Exception e) {
			log.debug("读取候选组失败: taskId={}, err={}", taskId, e.getMessage());
		}
		return new ArrayList<>(groups);
	}

}
