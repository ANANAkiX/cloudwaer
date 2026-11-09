package com.cloudwaer.workflow.serve.engine.node;

import com.cloudwaer.workflow.serve.engine.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 条件分支节点执行器：根据条件表达式选择一条出线
 * <p>
 * 条件表达式格式：`${variableName == 'value'}` 或简化为 `variableName == value` 无条件的出线作为默认分支（兜底）。
 */
@Slf4j
@Component
public class ConditionNodeExecutor implements NodeExecutor {

	@Override
	public String getNodeType() {
		return "condition";
	}

	@Override
	public NodeResult execute(DslNode node, ExecutionContext context) {
		// 条件分支的出线选择由 ProcessEngine 结合 edges 的 condition 字段处理
		// 这里返回 AUTO 并附带提示，让引擎解析出线
		// 实际条件求值在 ProcessEngine.advance 中对 outgoingEdges 做条件过滤
		return NodeResult.auto(null);
	}

	/**
	 * 求值条件表达式（供 ProcessEngine 调用）
	 */
	public boolean evaluate(String condition, ExecutionContext context) {
		if (condition == null || condition.isBlank()) {
			return true; // 无条件 = 默认分支
		}
		String expr = condition.trim();
		// 去掉 ${} 包裹
		if (expr.startsWith("${") && expr.endsWith("}")) {
			expr = expr.substring(2, expr.length() - 1).trim();
		}
		try {
			// 支持简单表达式: var == value / var != value / var > value
			return simpleEval(expr, context);
		}
		catch (Exception e) {
			log.warn("条件求值失败: expr={}, err={}", expr, e.getMessage());
			return false;
		}
	}

	private boolean simpleEval(String expr, ExecutionContext context) {
		// 尝试 == / != / > / < / >= / <=
		String[] ops = { "==", "!=", ">=", "<=", ">", "<" };
		for (String op : ops) {
			int idx = expr.indexOf(op);
			if (idx > 0) {
				String left = expr.substring(0, idx).trim();
				String right = expr.substring(idx + op.length()).trim();
				Object leftVal = resolveValue(left, context);
				Object rightVal = resolveValue(right, context);
				return compare(leftVal, rightVal, op);
			}
		}
		// 纯变量名：取布尔值
		Object val = resolveValue(expr, context);
		return val != null && Boolean.parseBoolean(String.valueOf(val));
	}

	private Object resolveValue(String token, ExecutionContext context) {
		if (token == null) {
			return null;
		}
		// 去引号
		if ((token.startsWith("'") && token.endsWith("'")) || (token.startsWith("\"") && token.endsWith("\""))) {
			return token.substring(1, token.length() - 1);
		}
		// 数字
		try {
			if (token.contains(".")) {
				return Double.parseDouble(token);
			}
			return Long.parseLong(token);
		}
		catch (NumberFormatException ignored) {
		}
		// 变量
		return context.getVariable(token);
	}

	private boolean compare(Object left, Object right, String op) {
		if (left == null && right == null) {
			return "==".equals(op);
		}
		if (left == null || right == null) {
			return "!=".equals(op);
		}
		// 数值比较
		if (left instanceof Number && right instanceof Number) {
			double l = ((Number) left).doubleValue();
			double r = ((Number) right).doubleValue();
			switch (op) {
				case "==":
					return l == r;
				case "!=":
					return l != r;
				case ">":
					return l > r;
				case "<":
					return l < r;
				case ">=":
					return l >= r;
				case "<=":
					return l <= r;
			}
		}
		// 字符串比较
		String l = String.valueOf(left);
		String r = String.valueOf(right);
		switch (op) {
			case "==":
				return l.equals(r);
			case "!=":
				return !l.equals(r);
			case ">":
				return l.compareTo(r) > 0;
			case "<":
				return l.compareTo(r) < 0;
			case ">=":
				return l.compareTo(r) >= 0;
			case "<=":
				return l.compareTo(r) <= 0;
		}
		return false;
	}

}
