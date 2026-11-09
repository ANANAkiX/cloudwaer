package com.cloudwaer.workflow.serve.engine;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程 DSL 完整定义（JSON 反序列化目标）
 */
@Data
public class DslDefinition {

	private String defKey;

	private String defName;

	private String version;

	private List<DslNode> nodes;

	private List<DslEdge> edges;

	private Map<String, Object> variables;

	public DslNode findNode(String nodeId) {
		if (nodes == null || nodeId == null) {
			return null;
		}
		return nodes.stream().filter(n -> nodeId.equals(n.getNodeId())).findFirst().orElse(null);
	}

	public List<DslEdge> outgoingEdges(String nodeId) {
		if (edges == null || nodeId == null) {
			return List.of();
		}
		return edges.stream().filter(e -> nodeId.equals(e.getSourceId())).toList();
	}

	public List<DslEdge> incomingEdges(String nodeId) {
		if (edges == null || nodeId == null) {
			return List.of();
		}
		return edges.stream().filter(e -> nodeId.equals(e.getTargetId())).toList();
	}

	public DslNode findStartNode() {
		if (nodes == null) {
			return null;
		}
		return nodes.stream().filter(n -> "start".equals(n.getNodeType())).findFirst().orElse(null);
	}

}
