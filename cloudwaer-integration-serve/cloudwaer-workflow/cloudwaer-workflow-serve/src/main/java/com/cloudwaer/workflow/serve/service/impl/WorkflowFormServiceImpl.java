package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.workflow.api.dto.WorkflowFormDTO;
import com.cloudwaer.workflow.serve.entity.WfForm;
import com.cloudwaer.workflow.serve.mapper.WfFormMapper;
import com.cloudwaer.workflow.serve.service.WorkflowFormService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowFormServiceImpl implements WorkflowFormService {

	@Autowired
	private WfFormMapper formMapper;

	@Override
	public Long saveForm(WorkflowFormDTO dto) {
		if (dto.getFormKey() == null || dto.getFormKey().isBlank()) {
			throw new BusinessException("表单标识不能为空");
		}
		if (dto.getFormName() == null || dto.getFormName().isBlank()) {
			throw new BusinessException("表单名称不能为空");
		}
		if (dto.getId() == null) {
			WfForm form = new WfForm();
			BeanUtils.copyProperties(dto, form);
			Integer max = formMapper
				.selectList(new LambdaQueryWrapper<WfForm>().eq(WfForm::getFormKey, dto.getFormKey())
					.select(WfForm::getVersion))
				.stream()
				.map(WfForm::getVersion)
				.max(Integer::compare)
				.orElse(0);
			form.setVersion(max + 1);
			form.setFormStatus(0);
			formMapper.insert(form);
			return form.getId();
		}
		WfForm form = formMapper.selectById(dto.getId());
		if (form == null) {
			throw new BusinessException("表单不存在");
		}
		BeanUtils.copyProperties(dto, form);
		formMapper.updateById(form);
		return form.getId();
	}

	@Override
	public WorkflowFormDTO getDetail(Long id) {
		WfForm form = formMapper.selectById(id);
		if (form == null) {
			throw new BusinessException("表单不存在");
		}
		WorkflowFormDTO dto = new WorkflowFormDTO();
		BeanUtils.copyProperties(form, dto);
		return dto;
	}

	@Override
	public PageResult<WorkflowFormDTO> list(PageDTO pageDTO, String category, String keyword) {
		Page<WfForm> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		LambdaQueryWrapper<WfForm> wrapper = new LambdaQueryWrapper<>();
		if (category != null && !category.isBlank() && !"all".equals(category)) {
			wrapper.eq(WfForm::getCategory, category);
		}
		if (keyword != null && !keyword.isBlank()) {
			wrapper.and(w -> w.like(WfForm::getFormKey, keyword).or().like(WfForm::getFormName, keyword));
		}
		wrapper.orderByDesc(WfForm::getUpdateTime);
		IPage<WfForm> result = formMapper.selectPage(page, wrapper);
		List<WorkflowFormDTO> records = result.getRecords().stream().map(form -> {
			WorkflowFormDTO dto = new WorkflowFormDTO();
			BeanUtils.copyProperties(form, dto);
			return dto;
		}).toList();
		return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
	}

	@Override
	public boolean deleteForm(Long id) {
		formMapper.deleteById(id);
		return true;
	}

	@Override
	public WorkflowFormDTO getByKeyAndVersion(String formKey, Integer version) {
		WfForm form = formMapper.selectOne(
				new LambdaQueryWrapper<WfForm>().eq(WfForm::getFormKey, formKey).eq(WfForm::getVersion, version));
		if (form == null) {
			return null;
		}
		WorkflowFormDTO dto = new WorkflowFormDTO();
		BeanUtils.copyProperties(form, dto);
		return dto;
	}

}
