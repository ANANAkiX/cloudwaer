package com.cloudwaer.workflow.serve.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DSL 解析器：JSON DSL → {@link DslDefinition}
 */
@Slf4j
@Component
public class DslParser {

	@Autowired
	private ObjectMapper objectMapper;

	public DslDefinition parse(String dslJson) {
		if (dslJson == null || dslJson.isBlank()) {
			throw new IllegalArgumentException("DSL JSON 不能为空");
		}
		try {
			DslDefinition def = objectMapper.readValue(dslJson, DslDefinition.class);
			validate(def);
			return def;
		}
		catch (IllegalArgumentException e) {
			throw e;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("DSL JSON 解析失败: " + e.getMessage(), e);
		}
	}

	private void validate(DslDefinition def) {
		if (def.getNodes() == null || def.getNodes().isEmpty()) {
			throw new IllegalArgumentException("DSL 节点列表不能为空");
		}
		if (def.findStartNode() == null) {
			throw new IllegalArgumentException("DSL 缺少开始节点");
		}
		boolean hasEnd = def.getNodes().stream().anyMatch(n -> "end".equals(n.getNodeType()));
		if (!hasEnd) {
			throw new IllegalArgumentException("DSL 缺少结束节点");
		}
	}

}
