package com.cloudwaer.workflow.serve.engine.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.Bindings;

/**
 * 脚本动作执行器：通过 JSR-223 执行 Groovy / JavaScript 脚本
 * <p>
 * config: {"language":"groovy|javascript","script":"xxx","variables":{}} 脚本中可通过 bindings
 * 访问流程变量
 */
@Slf4j
@Component
public class ScriptActionExecutor implements ActionExecutor {

	@Autowired
	private ObjectMapper objectMapper;

	private final ScriptEngineManager engineManager = new ScriptEngineManager();

	@Override
	public String getActionType() {
		return "script";
	}

	@Override
	public void execute(ActionContext context, String config) {
		try {
			JsonNode root = objectMapper.readTree(config);
			String language = root.has("language") ? root.get("language").asText("groovy") : "groovy";
			String script = root.has("script") ? root.get("script").asText() : null;

			if (script == null || script.isBlank()) {
				log.warn("[SCRIPT] 脚本为空，跳过: config={}", config);
				return;
			}

			ScriptEngine engine = getEngine(language);
			if (engine == null) {
				log.warn("[SCRIPT] 不支持的脚本语言: {}", language);
				return;
			}

			// 绑定变量
			Bindings bindings = engine.createBindings();
			bindings.put("instanceId", context.getInstanceId());
			bindings.put("taskId", context.getTaskId());
			bindings.put("nodeId", context.getNodeId());
			bindings.put("eventType", context.getEventType());
			bindings.put("operator", context.getOperator());

			// 绑定自定义变量
			if (root.has("variables") && root.get("variables").isObject()) {
				root.get("variables").fields().forEachRemaining(e -> bindings.put(e.getKey(), e.getValue().asText()));
			}

			Object result = engine.eval(script, bindings);
			log.info("[SCRIPT] 执行完成: language={}, result={}", language, result);
		}
		catch (Exception e) {
			log.error("[SCRIPT] 执行失败: config={}, err={}", config, e.getMessage(), e);
			throw new RuntimeException("脚本执行失败: " + e.getMessage(), e);
		}
	}

	private ScriptEngine getEngine(String language) {
		if (language == null)
			return null;
		switch (language.toLowerCase()) {
			case "groovy":
				return engineManager.getEngineByName("groovy");
			case "javascript":
			case "js":
				return engineManager.getEngineByName("javascript");
			case "nashorn":
				return engineManager.getEngineByName("nashorn");
			default:
				return engineManager.getEngineByName(language);
		}
	}

}
