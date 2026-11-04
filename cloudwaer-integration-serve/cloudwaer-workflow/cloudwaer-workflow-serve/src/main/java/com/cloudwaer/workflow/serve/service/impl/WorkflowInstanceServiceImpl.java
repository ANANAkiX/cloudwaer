package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.workflow.api.dto.ProcessStartDTO;
import com.cloudwaer.workflow.api.dto.WorkflowHistoryDTO;
import com.cloudwaer.workflow.api.dto.WorkflowInstanceDTO;
import com.cloudwaer.workflow.api.enums.InstanceStatus;
import com.cloudwaer.workflow.serve.engine.*;
import com.cloudwaer.workflow.serve.entity.WfDefinition;
import com.cloudwaer.workflow.serve.entity.WfHistory;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.entity.WfTask;
import com.cloudwaer.workflow.serve.mapper.WfDefinitionMapper;
import com.cloudwaer.workflow.serve.mapper.WfHistoryMapper;
import com.cloudwaer.workflow.serve.mapper.WfInstanceMapper;
import com.cloudwaer.workflow.serve.mapper.WfTaskMapper;
import com.cloudwaer.workflow.serve.service.InstanceNodeFormService;
import com.cloudwaer.workflow.serve.service.WorkflowInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService {

	@Autowired
	private WfInstanceMapper instanceMapper;

	@Autowired
	private WfTaskMapper taskMapper;

	@Autowired
	private WfDefinitionMapper defMapper;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private DslParser dslParser;

	@Autowired
	private WfHistoryMapper historyMapper;

	@Autowired
	private InstanceNodeFormService instanceNodeFormService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String startProcess(ProcessStartDTO dto) {
		String username = SecurityContextUtil.getCurrentUsername();
		if (username == null || username.isBlank()) {
			throw new BusinessException("用户未登录");
		}

		// 查找最新已发布版本
		WfDefinition def = defMapper
			.selectOne(new LambdaQueryWrapper<WfDefinition>().eq(WfDefinition::getDefKey, dto.getDefKey())
				.eq(WfDefinition::getDefStatus, 1)
				.orderByDesc(WfDefinition::getVersion)
				.last("LIMIT 1"));
		if (def == null) {
			throw new BusinessException("流程定义不存在或未发布: " + dto.getDefKey());
		}

		// 校验 endTime
		if (def.getEndTime() != null && def.getEndTime().isBefore(LocalDateTime.now())) {
			throw new BusinessException("流程已过期");
		}

		Map<String, Object> variables = dto.getVariables() != null ? new HashMap<>(dto.getVariables())
				: new HashMap<>();
		variables.put("startUser", username);
		variables.put("defKey", dto.getDefKey());

		// 通过引擎启动
		String instanceId = processEngine.startProcess(dto.getDefKey(), def.getDslJson(), dto.getBusinessKey(),
				dto.getTitle(), dto.getFormJson(), username, dto.getPriority(), variables, def.getId());

		// 初始化节点表单记录
		instanceNodeFormService.initNodeForms(instanceId, dto.getDefKey(), def.getVersion(), def.getDslJson());

		// 更新实例的 defId/defVersion
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance != null) {
			instance.setDefId(def.getId());
			instance.setDefVersion(def.getVersion());
			instance.setDueTime(def.getEndTime());
			instanceMapper.updateById(instance);
		}

		return instanceId;
	}

	@Override
	public PageResult<WorkflowInstanceDTO> listStarted(PageDTO pageDTO) {
		String username = SecurityContextUtil.getCurrentUsername();
		Page<WfInstance> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfInstance> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(WfInstance::getStartUser, username);
		wrapper.orderByDesc(WfInstance::getCreateTime);
		IPage<WfInstance> result = instanceMapper.selectPage(page, wrapper);
		return toPageResult(result);
	}

	@Override
	public PageResult<WorkflowInstanceDTO> listAll(PageDTO pageDTO, String status, String keyword) {
		Page<WfInstance> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfInstance> wrapper = new LambdaQueryWrapper<>();
		if (status != null && !status.isBlank() && !"all".equals(status)) {
			wrapper.eq(WfInstance::getInstStatus, Integer.parseInt(status));
		}
		if (keyword != null && !keyword.isBlank()) {
			wrapper.and(w -> w.like(WfInstance::getTitle, keyword).or().like(WfInstance::getDefName, keyword));
		}
		wrapper.orderByDesc(WfInstance::getCreateTime);
		IPage<WfInstance> result = instanceMapper.selectPage(page, wrapper);
		return toPageResult(result);
	}

	@Override
	public WorkflowInstanceDTO getDetail(String instanceId) {
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance == null) {
			throw new BusinessException("流程实例不存在");
		}
		WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
		BeanUtils.copyProperties(instance, dto);
		InstanceStatus s = InstanceStatus.getByCode(instance.getInstStatus());
		dto.setStatusDesc(s != null ? s.getDesc() : "未知");
		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean suspend(String instanceId) {
		WfInstance instance = getRunning(instanceId);
		instance.setInstStatus(InstanceStatus.SUSPENDED.getCode());
		instanceMapper.updateById(instance);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean activate(String instanceId) {
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance == null) {
			throw new BusinessException("流程实例不存在");
		}
		if (instance.getInstStatus() == null || instance.getInstStatus() != InstanceStatus.SUSPENDED.getCode()) {
			throw new BusinessException("仅挂起状态可激活");
		}
		instance.setInstStatus(InstanceStatus.RUNNING.getCode());
		instanceMapper.updateById(instance);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean terminate(String instanceId) {
		WfInstance instance = getRunning(instanceId);
		instance.setInstStatus(InstanceStatus.TERMINATED.getCode());
		instance.setEndTime(LocalDateTime.now());
		instanceMapper.updateById(instance);

		// 同步取消所有待处理/已认领任务
		taskMapper.update(null, new LambdaUpdateWrapper<WfTask>().set(WfTask::getTaskStatus, 5) // canceled
			.eq(WfTask::getInstanceId, instanceId)
			.in(WfTask::getTaskStatus, 0, 1)); // pending/claimed
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean restart(String instanceId) {
		String username = SecurityContextUtil.getCurrentUsername();
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance == null) {
			throw new BusinessException("流程实例不存在");
		}
		if (instance.getInstStatus() == null || instance.getInstStatus() != InstanceStatus.REJECTED.getCode()) {
			throw new BusinessException("仅被驳回的流程可重新发起");
		}
		instance.setInstStatus(InstanceStatus.RUNNING.getCode());
		instance.setEndTime(null);
		instanceMapper.updateById(instance);

		// 从开始节点重新驱动
		try {
			WfDefinition def = defMapper.selectById(instance.getDefId());
			if (def != null && def.getDslJson() != null) {
				DslDefinition dsl = dslParser.parse(def.getDslJson());
				DslNode startNode = dsl.findStartNode();
				if (startNode != null) {
					ExecutionContext ctx = new ExecutionContext();
					ctx.setInstanceId(instanceId);
					ctx.setDefKey(instance.getDefKey());
					ctx.setDefVersion(instance.getDefVersion());
					ctx.setOperator(username);
					// 加载变量
					if (instance.getVarsJson() != null) {
						try {
							var varsMap = new com.fasterxml.jackson.databind.ObjectMapper()
								.readValue(instance.getVarsJson(), java.util.Map.class);
							ctx.getVariables().putAll(varsMap);
						}
						catch (Exception ignored) {
						}
					}
					processEngine.advance(dsl, ctx, startNode);
					log.info("流程已重启: instanceId={}", instanceId);
				}
			}
		}
		catch (Exception e) {
			log.error("重启流程异常: instanceId={}, err={}", instanceId, e.getMessage(), e);
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteInstance(String instanceId) {
		instanceMapper.delete(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		return true;
	}

	private WfInstance getRunning(String instanceId) {
		WfInstance instance = instanceMapper
			.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, instanceId));
		if (instance == null) {
			throw new BusinessException("流程实例不存在");
		}
		if (instance.getInstStatus() == null || instance.getInstStatus() != InstanceStatus.RUNNING.getCode()) {
			throw new BusinessException("流程不在运行中");
		}
		return instance;
	}

	private PageResult<WorkflowInstanceDTO> toPageResult(IPage<WfInstance> result) {
		List<WorkflowInstanceDTO> records = result.getRecords().stream().map(inst -> {
			WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
			BeanUtils.copyProperties(inst, dto);
			InstanceStatus s = InstanceStatus.getByCode(inst.getInstStatus());
			dto.setStatusDesc(s != null ? s.getDesc() : "未知");
			return dto;
		}).toList();
		return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
	}

	@Override
	public java.util.List<WorkflowHistoryDTO> getHistory(String instanceId) {
		java.util.List<WfHistory> records = historyMapper
			.selectList(new LambdaQueryWrapper<WfHistory>().eq(WfHistory::getInstanceId, instanceId)
				.orderByAsc(WfHistory::getCreateTime));
		return records.stream().map(h -> {
			WorkflowHistoryDTO dto = new WorkflowHistoryDTO();
			dto.setId(h.getId());
			dto.setInstanceId(h.getInstanceId());
			dto.setTaskId(h.getTaskId());
			dto.setNodeId(h.getNodeId());
			dto.setNodeName(h.getNodeName());
			dto.setNodeType(h.getNodeType());
			dto.setAction(h.getAction());
			dto.setOperator(h.getOperator());
			dto.setDetail(h.getDetail());
			dto.setResult(h.getResult());
			dto.setDurationMs(h.getDurationMs());
			dto.setCreateTime(h.getCreateTime());
			return dto;
		}).toList();
	}

}
