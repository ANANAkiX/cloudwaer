package com.cloudwaer.workflow.serve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwaer.common.core.dto.PageDTO;
import com.cloudwaer.common.core.dto.PageResult;
import com.cloudwaer.common.core.exception.BusinessException;
import com.cloudwaer.workflow.api.dto.*;
import com.cloudwaer.workflow.serve.entity.*;
import com.cloudwaer.workflow.serve.mapper.*;
import com.cloudwaer.workflow.serve.service.WorkflowDefService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WorkflowDefServiceImpl implements WorkflowDefService {

	@Autowired
	private WfDefinitionMapper defMapper;

	@Autowired
	private WfNodeMapper nodeMapper;

	@Autowired
	private WfEdgeMapper edgeMapper;

	@Autowired
	private ObjectMapper objectMapper;

	private static final int STATUS_DRAFT = 0;

	private static final int STATUS_PUBLISHED = 1;

	private static final int STATUS_ARCHIVED = 2;

	private static final int STATUS_OFFLINE = 3;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long saveDef(WorkflowDefDTO dto) {
		if (dto.getDefKey() == null || dto.getDefKey().isBlank()) {
			throw new BusinessException("流程标识不能为空");
		}
		if (dto.getDefName() == null || dto.getDefName().isBlank()) {
			throw new BusinessException("流程名称不能为空");
		}

		if (dto.getId() == null) {
			// 新建
			WfDefinition def = new WfDefinition();
			BeanUtils.copyProperties(dto, def);
			def.setVersion(resolveNextVersion(dto.getDefKey()));
			def.setDefStatus(STATUS_DRAFT);
			def.setDslJson(buildDslJson(dto));
			defMapper.insert(def);
			saveNodesAndEdges(def.getId(), def.getDefKey(), def.getVersion(), dto);
			return def.getId();
		}

		// 更新
		WfDefinition def = defMapper.selectById(dto.getId());
		if (def == null) {
			throw new BusinessException("流程定义不存在");
		}
		// 版本不可变：非草稿（已发布/已归档/已下线）一律不许原地改写
		if (def.getDefStatus() != null && def.getDefStatus() != STATUS_DRAFT) {
			throw new BusinessException("该版本不可直接修改，请使用「另存为新版本」或「恢复」生成新草稿");
		}

		def.setDefName(dto.getDefName());
		def.setCategory(dto.getCategory());
		// 总是从 nodes/edges 重建 DSL（确保条件表达式等配置不丢失）
		def.setDslJson(buildDslJson(dto));
		def.setFormId(dto.getFormId());
		def.setFormKey(dto.getFormKey());
		def.setEndTime(dto.getEndTime());
		def.setRemark(dto.getRemark());
		defMapper.updateById(def);

		// 保存节点和连线
		nodeMapper.delete(new LambdaQueryWrapper<WfNode>().eq(WfNode::getDefId, def.getId()));
		edgeMapper.delete(new LambdaQueryWrapper<WfEdge>().eq(WfEdge::getDefId, def.getId()));
		saveNodesAndEdges(def.getId(), def.getDefKey(), def.getVersion(), dto);
		return def.getId();
	}

	@Override
	public WorkflowDefDTO getDetail(Long id) {
		WfDefinition def = defMapper.selectById(id);
		if (def == null) {
			throw new BusinessException("流程定义不存在");
		}
		WorkflowDefDTO dto = new WorkflowDefDTO();
		BeanUtils.copyProperties(def, dto);

		// 加载节点和连线
		List<WfNode> nodes = nodeMapper
			.selectList(new LambdaQueryWrapper<WfNode>().eq(WfNode::getDefId, id).orderByAsc(WfNode::getSortOrder));
		List<WorkflowNodeDTO> nodeDtos = new ArrayList<>();
		for (WfNode n : nodes) {
			WorkflowNodeDTO nd = new WorkflowNodeDTO();
			BeanUtils.copyProperties(n, nd);
			// 从 configJson 中解析 x/y
			if (n.getConfigJson() != null) {
				try {
					com.fasterxml.jackson.databind.JsonNode cfg = objectMapper.readTree(n.getConfigJson());
					if (cfg.has("x"))
						nd.setX(cfg.get("x").asDouble());
					if (cfg.has("y"))
						nd.setY(cfg.get("y").asDouble());
				}
				catch (Exception ignored) {
				}
			}
			nodeDtos.add(nd);
		}
		dto.setNodes(nodeDtos);

		List<WfEdge> edges = edgeMapper
			.selectList(new LambdaQueryWrapper<WfEdge>().eq(WfEdge::getDefId, id).orderByAsc(WfEdge::getSortOrder));
		List<WorkflowEdgeDTO> edgeDtos = new ArrayList<>();
		for (WfEdge e : edges) {
			WorkflowEdgeDTO ed = new WorkflowEdgeDTO();
			BeanUtils.copyProperties(e, ed);
			edgeDtos.add(ed);
		}
		dto.setEdges(edgeDtos);
		return dto;
	}

	@Override
	public PageResult<WorkflowDefDTO> list(PageDTO pageDTO, String category, String keyword) {
		Page<WfDefinition> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());
		String cat = (category == null || category.isBlank() || "all".equals(category)) ? null : category.trim();
		String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
		// 主列表按 defKey 聚合：每个流程一行（已发布 > 最新草稿 > 最新已下线）
		IPage<WfDefinition> result = defMapper.selectRepresentatives(page, cat, kw);

		List<WfDefinition> rows = result.getRecords();
		// 标记「有未发布草稿」的流程（避免草稿被聚合隐藏后不可发现）
		Map<String, WfDefinition> drafts = new HashMap<>();
		if (!rows.isEmpty()) {
			List<String> keys = rows.stream().map(WfDefinition::getDefKey).distinct().toList();
			// orderByDesc(version) 后 putIfAbsent：每 key 取最高版本草稿
			defMapper
				.selectList(new LambdaQueryWrapper<WfDefinition>().in(WfDefinition::getDefKey, keys)
					.eq(WfDefinition::getDefStatus, STATUS_DRAFT)
					.orderByDesc(WfDefinition::getVersion))
				.forEach(d -> drafts.putIfAbsent(d.getDefKey(), d));
		}

		List<WorkflowDefDTO> records = rows.stream().map(def -> {
			WorkflowDefDTO dto = new WorkflowDefDTO();
			BeanUtils.copyProperties(def, dto);
			WfDefinition draft = drafts.get(def.getDefKey());
			if (draft != null && !draft.getId().equals(def.getId())) {
				dto.setDraftId(draft.getId());
				dto.setDraftVersion(draft.getVersion());
			}
			return dto;
		}).toList();
		return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
	}

	@Override
	public List<WorkflowDefDTO> listVersions(String defKey) {
		List<WfDefinition> list = defMapper
			.selectList(new LambdaQueryWrapper<WfDefinition>().eq(WfDefinition::getDefKey, defKey)
				.orderByDesc(WfDefinition::getVersion));
		return list.stream().map(def -> {
			WorkflowDefDTO dto = new WorkflowDefDTO();
			BeanUtils.copyProperties(def, dto);
			return dto;
		}).toList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean publish(Long id) {
		WfDefinition def = defMapper.selectById(id);
		if (def == null) {
			throw new BusinessException("流程定义不存在");
		}
		// 归档同 Key 其它已发布版本
		defMapper.update(null,
				new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<WfDefinition>()
					.set(WfDefinition::getDefStatus, STATUS_ARCHIVED)
					.eq(WfDefinition::getDefKey, def.getDefKey())
					.eq(WfDefinition::getDefStatus, STATUS_PUBLISHED)
					.ne(WfDefinition::getId, id));
		def.setDefStatus(STATUS_PUBLISHED);
		defMapper.updateById(def);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean offline(Long id) {
		WfDefinition def = defMapper.selectById(id);
		if (def == null) {
			throw new BusinessException("流程定义不存在");
		}
		if (def.getDefStatus() == null || def.getDefStatus() != STATUS_PUBLISHED) {
			throw new BusinessException("仅已发布流程可下线");
		}
		def.setDefStatus(STATUS_OFFLINE);
		defMapper.updateById(def);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteDef(Long id) {
		WfDefinition def = defMapper.selectById(id);
		if (def == null) {
			return true;
		}
		if (def.getDefStatus() != null && def.getDefStatus() == STATUS_PUBLISHED) {
			throw new BusinessException("已发布流程不可删除，请先下线");
		}
		nodeMapper.delete(new LambdaQueryWrapper<WfNode>().eq(WfNode::getDefId, id));
		edgeMapper.delete(new LambdaQueryWrapper<WfEdge>().eq(WfEdge::getDefId, id));
		defMapper.deleteById(id);
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long rollback(Long id) {
		WorkflowDefDTO source = getDetail(id);
		if (source == null) {
			throw new BusinessException("流程定义不存在");
		}
		// 以历史版本内容生成新的草稿版本（历史行保持不动）
		source.setId(null);
		return saveDef(source);
	}

	@Override
	public WorkflowDefDTO getByKeyAndVersion(String defKey, Integer version) {
		WfDefinition def = defMapper
			.selectOne(new LambdaQueryWrapper<WfDefinition>().eq(WfDefinition::getDefKey, defKey)
				.eq(WfDefinition::getVersion, version));
		if (def == null) {
			return null;
		}
		WorkflowDefDTO dto = new WorkflowDefDTO();
		BeanUtils.copyProperties(def, dto);
		return dto;
	}

	private void saveNodesAndEdges(Long defId, String defKey, Integer version, WorkflowDefDTO dto) {
		if (dto.getNodes() != null) {
			int order = 0;
			for (WorkflowNodeDTO nd : dto.getNodes()) {
				WfNode node = new WfNode();
				BeanUtils.copyProperties(nd, node);
				node.setId(null);
				node.setDefId(defId);
				node.setDefKey(defKey);
				node.setDefVersion(version);
				node.setSortOrder(order++);
				// 把 x/y 存入 configJson
				if (nd.getX() != null || nd.getY() != null) {
					try {
						com.fasterxml.jackson.databind.node.ObjectNode cfg = objectMapper.createObjectNode();
						if (node.getConfigJson() != null && !node.getConfigJson().isBlank()) {
							com.fasterxml.jackson.databind.JsonNode existing = objectMapper
								.readTree(node.getConfigJson());
							if (existing.isObject()) {
								existing.fields().forEachRemaining(e -> cfg.set(e.getKey(), e.getValue()));
							}
						}
						if (nd.getX() != null)
							cfg.put("x", nd.getX());
						if (nd.getY() != null)
							cfg.put("y", nd.getY());
						node.setConfigJson(objectMapper.writeValueAsString(cfg));
					}
					catch (Exception ignored) {
					}
				}
				nodeMapper.insert(node);
			}
		}
		if (dto.getEdges() != null) {
			int order = 0;
			for (WorkflowEdgeDTO ed : dto.getEdges()) {
				WfEdge edge = new WfEdge();
				BeanUtils.copyProperties(ed, edge);
				edge.setId(null);
				edge.setDefId(defId);
				edge.setDefKey(defKey);
				edge.setDefVersion(version);
				edge.setSortOrder(order++);
				edgeMapper.insert(edge);
			}
		}
	}

	private int resolveNextVersion(String defKey) {
		Integer max = defMapper.selectMaxVersion(defKey);
		int next = (max != null ? max : 0) + 1;
		while (defMapper.countVersion(defKey, next) > 0) {
			next++;
		}
		return next;
	}

	/**
	 * 从 DTO 的 nodes/edges 构建 DSL JSON
	 */
	private String buildDslJson(WorkflowDefDTO dto) {
		try {
			com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			com.fasterxml.jackson.databind.node.ObjectNode root = om.createObjectNode();
			root.put("defKey", dto.getDefKey());
			root.put("defName", dto.getDefName());

			com.fasterxml.jackson.databind.node.ArrayNode nodesArr = root.putArray("nodes");
			if (dto.getNodes() != null) {
				for (WorkflowNodeDTO nd : dto.getNodes()) {
					com.fasterxml.jackson.databind.node.ObjectNode n = nodesArr.addObject();
					n.put("nodeId", nd.getNodeId());
					n.put("nodeName", nd.getNodeName());
					n.put("nodeType", nd.getNodeType());
					if (nd.getAssigneeType() != null)
						n.put("assigneeType", nd.getAssigneeType());
					if (nd.getAssigneeVal() != null)
						n.put("assigneeVal", nd.getAssigneeVal());
					if (nd.getMultiType() != null)
						n.put("multiType", nd.getMultiType());
					if (nd.getMultiCount() != null)
						n.put("multiCount", nd.getMultiCount());
					if (nd.getDueHours() != null)
						n.put("dueHours", nd.getDueHours());
					if (nd.getFormJson() != null)
						n.put("formJson", nd.getFormJson());
					if (nd.getActionJson() != null)
						n.put("actionJson", nd.getActionJson());
					if (nd.getListenerJson() != null)
						n.put("listenerJson", nd.getListenerJson());
					if (nd.getConfigJson() != null)
						n.put("configJson", nd.getConfigJson());
					if (nd.getX() != null)
						n.put("x", nd.getX());
					if (nd.getY() != null)
						n.put("y", nd.getY());
				}
			}

			com.fasterxml.jackson.databind.node.ArrayNode edgesArr = root.putArray("edges");
			if (dto.getEdges() != null) {
				log.info("[DSL] 构建 DSL: {} 条边", dto.getEdges().size());
				for (WorkflowEdgeDTO ed : dto.getEdges()) {
					log.info("[DSL] 边: source={}, target={}, condition=\"{}\"", ed.getSourceId(), ed.getTargetId(),
							ed.getEdgeCondition());
					com.fasterxml.jackson.databind.node.ObjectNode e = edgesArr.addObject();
					e.put("edgeId", ed.getEdgeId());
					e.put("sourceId", ed.getSourceId());
					e.put("targetId", ed.getTargetId());
					if (ed.getEdgeName() != null)
						e.put("edgeName", ed.getEdgeName());
					if (ed.getEdgeCondition() != null && !ed.getEdgeCondition().isBlank())
						e.put("condition", ed.getEdgeCondition());
				}
			}

			return om.writeValueAsString(root);
		}
		catch (Exception e) {
			log.error("构建 DSL JSON 失败: {}", e.getMessage(), e);
			return null;
		}
	}

}
