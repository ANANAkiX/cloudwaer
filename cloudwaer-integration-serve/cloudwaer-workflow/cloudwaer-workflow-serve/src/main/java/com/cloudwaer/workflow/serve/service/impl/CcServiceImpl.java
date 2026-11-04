package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.util.SecurityContextUtil;
import com.cloudwaer.workflow.api.dto.WorkflowCcRecordDTO;
import com.cloudwaer.workflow.api.enums.InstanceStatus;
import com.cloudwaer.workflow.serve.entity.WfCcRecord;
import com.cloudwaer.workflow.serve.entity.WfInstance;
import com.cloudwaer.workflow.serve.mapper.WfCcRecordMapper;
import com.cloudwaer.workflow.serve.mapper.WfInstanceMapper;
import com.cloudwaer.workflow.serve.service.CcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CcServiceImpl implements CcService {

	@Autowired
	private WfCcRecordMapper ccRecordMapper;

	@Autowired
	private WfInstanceMapper instanceMapper;

	@Override
	public PageResult<WorkflowCcRecordDTO> listMyCc(PageDTO pageDTO) {
		String username = SecurityContextUtil.getCurrentUsername();
		if (username == null || username.isBlank()) {
			return new PageResult<>(List.of(), 0L, 1L, 10L);
		}

		Page<WfCcRecord> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfCcRecord> wrapper = new LambdaQueryWrapper<>();
		wrapper.like(WfCcRecord::getCcTarget, username);
		wrapper.orderByDesc(WfCcRecord::getCreateTime);
		IPage<WfCcRecord> result = ccRecordMapper.selectPage(page, wrapper);

		List<WorkflowCcRecordDTO> records = result.getRecords().stream().map(rec -> {
			WorkflowCcRecordDTO dto = new WorkflowCcRecordDTO();
			dto.setId(rec.getId());
			dto.setInstanceId(rec.getInstanceId());
			dto.setDefKey(rec.getDefKey());
			dto.setNodeId(rec.getNodeId());
			dto.setCcType(rec.getCcType());
			dto.setCcTarget(rec.getCcTarget());
			dto.setCcBy(rec.getCcBy());
			dto.setCreateTime(rec.getCreateTime());
			dto.setReadStatus(rec.getReadStatus());
			dto.setReadTime(rec.getReadTime());

			// 补充实例信息
			WfInstance instance = instanceMapper
				.selectOne(new LambdaQueryWrapper<WfInstance>().eq(WfInstance::getInstanceId, rec.getInstanceId()));
			if (instance != null) {
				dto.setDefName(instance.getDefName());
				dto.setTitle(instance.getTitle());
				dto.setInstStatus(instance.getInstStatus());
				InstanceStatus s = InstanceStatus.getByCode(instance.getInstStatus());
				dto.setStatusDesc(s != null ? s.getDesc() : "未知");
			}
			return dto;
		}).toList();

		return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
	}

	@Override
	public boolean markRead(Long id) {
		ccRecordMapper.update(null,
				new LambdaUpdateWrapper<WfCcRecord>().set(WfCcRecord::getReadStatus, 1)
					.set(WfCcRecord::getReadTime, LocalDateTime.now())
					.eq(WfCcRecord::getId, id));
		return true;
	}

	@Override
	public boolean markAllRead() {
		String username = SecurityContextUtil.getCurrentUsername();
		if (username == null || username.isBlank()) {
			return false;
		}
		ccRecordMapper.update(null,
				new LambdaUpdateWrapper<WfCcRecord>().set(WfCcRecord::getReadStatus, 1)
					.set(WfCcRecord::getReadTime, LocalDateTime.now())
					.like(WfCcRecord::getCcTarget, username)
					.eq(WfCcRecord::getReadStatus, 0));
		return true;
	}

}
